package co.edu.iudigital.rrhh.dao.impl;

import co.edu.iudigital.rrhh.dao.FuncionarioDAO;
import co.edu.iudigital.rrhh.excepciones.DAOException;
import co.edu.iudigital.rrhh.excepciones.DuplicadoException;
import co.edu.iudigital.rrhh.excepciones.NoEncontradoException;
import co.edu.iudigital.rrhh.modelo.Catalogo;
import co.edu.iudigital.rrhh.modelo.Funcionario;
import co.edu.iudigital.rrhh.modelo.InformacionAcademica;
import co.edu.iudigital.rrhh.modelo.MiembroFamiliar;
import co.edu.iudigital.rrhh.util.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC del DAO de funcionarios. Todas las consultas usan PreparedStatement. */
public class FuncionarioDAOImpl implements FuncionarioDAO {

    private static final int MYSQL_DUPLICADO = 1062;

    private static final String SELECT_FUNCIONARIO =
            "SELECT f.id, f.numero_identificacion, f.nombres, f.apellidos, f.direccion, f.telefono, "
          + "       f.fecha_nacimiento, ti.id ti_id, ti.nombre ti_nombre, "
          + "       ec.id ec_id, ec.nombre ec_nombre, s.id s_id, s.nombre s_nombre "
          + "FROM funcionario f "
          + "JOIN tipo_identificacion ti ON ti.id = f.tipo_identificacion_id "
          + "JOIN estado_civil ec ON ec.id = f.estado_civil_id "
          + "JOIN sexo s ON s.id = f.sexo_id ";

    private static final String INSERT_FUNCIONARIO =
            "INSERT INTO funcionario (tipo_identificacion_id, numero_identificacion, nombres, apellidos, "
          + "estado_civil_id, sexo_id, direccion, telefono, fecha_nacimiento) VALUES (?,?,?,?,?,?,?,?,?)";

    private static final String UPDATE_FUNCIONARIO =
            "UPDATE funcionario SET tipo_identificacion_id=?, numero_identificacion=?, nombres=?, apellidos=?, "
          + "estado_civil_id=?, sexo_id=?, direccion=?, telefono=?, fecha_nacimiento=? WHERE id=?";

    private static final String INSERT_MIEMBRO =
            "INSERT INTO miembro_familiar (funcionario_id, tipo_identificacion_id, numero_identificacion, "
          + "nombres, apellidos, fecha_nacimiento, sexo_id, rol_familiar_id) VALUES (?,?,?,?,?,?,?,?)";

    private static final String INSERT_ACADEMICA =
            "INSERT INTO informacion_academica (funcionario_id, universidad_id, nivel_estudio_id, titulo) "
          + "VALUES (?,?,?,?)";

    private static final String SELECT_MIEMBROS =
            "SELECT m.id, m.numero_identificacion, m.nombres, m.apellidos, m.fecha_nacimiento, "
          + "       ti.id ti_id, ti.nombre ti_nombre, s.id s_id, s.nombre s_nombre, rf.id rf_id, rf.nombre rf_nombre "
          + "FROM miembro_familiar m "
          + "JOIN tipo_identificacion ti ON ti.id = m.tipo_identificacion_id "
          + "JOIN sexo s ON s.id = m.sexo_id "
          + "JOIN rol_familiar rf ON rf.id = m.rol_familiar_id "
          + "WHERE m.funcionario_id = ? ORDER BY m.id";

    private static final String SELECT_ACADEMICA =
            "SELECT ia.id, ia.titulo, u.id u_id, u.nombre u_nombre, ne.id ne_id, ne.nombre ne_nombre "
          + "FROM informacion_academica ia "
          + "JOIN universidad u ON u.id = ia.universidad_id "
          + "JOIN nivel_estudio ne ON ne.id = ia.nivel_estudio_id "
          + "WHERE ia.funcionario_id = ? ORDER BY ia.id";

    // ------------------------------------------------------------------ CREATE
    @Override
    public Funcionario crear(Funcionario f) throws DAOException {
        try (Connection con = ConexionBD.obtener()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(INSERT_FUNCIONARIO, Statement.RETURN_GENERATED_KEYS)) {
                    asignarParametrosFuncionario(ps, f);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            f.setId(keys.getInt(1));
                        }
                    }
                }
                insertarRelaciones(con, f);
                con.commit();
                return f;
            } catch (SQLException e) {
                con.rollback();
                throw traducir("crear el funcionario", e);
            }
        } catch (SQLException e) {
            throw traducir("crear el funcionario", e);
        }
    }

    // ------------------------------------------------------------------ UPDATE
    @Override
    public void actualizar(Funcionario f) throws DAOException {
        try (Connection con = ConexionBD.obtener()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(UPDATE_FUNCIONARIO)) {
                    asignarParametrosFuncionario(ps, f);
                    ps.setInt(10, f.getId());
                    if (ps.executeUpdate() == 0) {
                        throw new NoEncontradoException("El funcionario con id " + f.getId() + " ya no existe.");
                    }
                }
                borrarRelaciones(con, f.getId());
                insertarRelaciones(con, f);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw traducir("actualizar el funcionario", e);
            } catch (NoEncontradoException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw traducir("actualizar el funcionario", e);
        }
    }

    // ------------------------------------------------------------------ DELETE
    @Override
    public void eliminar(int id) throws DAOException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("DELETE FROM funcionario WHERE id = ?")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new NoEncontradoException("El funcionario con id " + id + " no existe.");
            }
        } catch (SQLException e) {
            throw traducir("eliminar el funcionario", e);
        }
    }

    // -------------------------------------------------------------------- READ
    @Override
    public Funcionario buscarPorId(int id) throws DAOException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(SELECT_FUNCIONARIO + "WHERE f.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new NoEncontradoException("El funcionario con id " + id + " no existe.");
                }
                Funcionario f = mapearFuncionario(rs);
                f.setGrupoFamiliar(cargarMiembros(con, id));
                f.setInformacionAcademica(cargarAcademica(con, id));
                return f;
            }
        } catch (SQLException e) {
            throw traducir("consultar el funcionario", e);
        }
    }

    @Override
    public List<Funcionario> listar() throws DAOException {
        List<Funcionario> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(SELECT_FUNCIONARIO + "ORDER BY f.apellidos, f.nombres");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearFuncionario(rs));
            }
        } catch (SQLException e) {
            throw traducir("listar los funcionarios", e);
        }
        return lista;
    }

    // ----------------------------------------------------------------- helpers
    private void asignarParametrosFuncionario(PreparedStatement ps, Funcionario f) throws SQLException {
        ps.setInt(1, f.getTipoIdentificacion().getId());
        ps.setString(2, f.getNumeroIdentificacion());
        ps.setString(3, f.getNombres());
        ps.setString(4, f.getApellidos());
        ps.setInt(5, f.getEstadoCivil().getId());
        ps.setInt(6, f.getSexo().getId());
        ps.setString(7, f.getDireccion());
        ps.setString(8, f.getTelefono());
        ps.setDate(9, Date.valueOf(f.getFechaNacimiento()));
    }

    private void insertarRelaciones(Connection con, Funcionario f) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(INSERT_MIEMBRO)) {
            for (MiembroFamiliar m : f.getGrupoFamiliar()) {
                ps.setInt(1, f.getId());
                ps.setInt(2, m.getTipoIdentificacion().getId());
                ps.setString(3, m.getNumeroIdentificacion());
                ps.setString(4, m.getNombres());
                ps.setString(5, m.getApellidos());
                ps.setDate(6, Date.valueOf(m.getFechaNacimiento()));
                ps.setInt(7, m.getSexo().getId());
                ps.setInt(8, m.getRolFamiliar().getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
        try (PreparedStatement ps = con.prepareStatement(INSERT_ACADEMICA)) {
            for (InformacionAcademica ia : f.getInformacionAcademica()) {
                ps.setInt(1, f.getId());
                ps.setInt(2, ia.getUniversidad().getId());
                ps.setInt(3, ia.getNivelEstudio().getId());
                ps.setString(4, ia.getTitulo());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void borrarRelaciones(Connection con, int funcionarioId) throws SQLException {
        for (String tabla : new String[] {"miembro_familiar", "informacion_academica"}) {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + tabla + " WHERE funcionario_id = ?")) {
                ps.setInt(1, funcionarioId);
                ps.executeUpdate();
            }
        }
    }

    private List<MiembroFamiliar> cargarMiembros(Connection con, int funcionarioId) throws SQLException {
        List<MiembroFamiliar> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(SELECT_MIEMBROS)) {
            ps.setInt(1, funcionarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MiembroFamiliar m = new MiembroFamiliar();
                    m.setId(rs.getInt("id"));
                    m.setTipoIdentificacion(catalogo(rs, "ti"));
                    m.setNumeroIdentificacion(rs.getString("numero_identificacion"));
                    m.setNombres(rs.getString("nombres"));
                    m.setApellidos(rs.getString("apellidos"));
                    m.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
                    m.setSexo(catalogo(rs, "s"));
                    m.setRolFamiliar(catalogo(rs, "rf"));
                    lista.add(m);
                }
            }
        }
        return lista;
    }

    private List<InformacionAcademica> cargarAcademica(Connection con, int funcionarioId) throws SQLException {
        List<InformacionAcademica> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(SELECT_ACADEMICA)) {
            ps.setInt(1, funcionarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformacionAcademica ia = new InformacionAcademica();
                    ia.setId(rs.getInt("id"));
                    ia.setUniversidad(catalogo(rs, "u"));
                    ia.setNivelEstudio(catalogo(rs, "ne"));
                    ia.setTitulo(rs.getString("titulo"));
                    lista.add(ia);
                }
            }
        }
        return lista;
    }

    private Funcionario mapearFuncionario(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("id"));
        f.setTipoIdentificacion(catalogo(rs, "ti"));
        f.setNumeroIdentificacion(rs.getString("numero_identificacion"));
        f.setNombres(rs.getString("nombres"));
        f.setApellidos(rs.getString("apellidos"));
        f.setEstadoCivil(catalogo(rs, "ec"));
        f.setSexo(catalogo(rs, "s"));
        f.setDireccion(rs.getString("direccion"));
        f.setTelefono(rs.getString("telefono"));
        f.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
        return f;
    }

    private Catalogo catalogo(ResultSet rs, String prefijo) throws SQLException {
        return new Catalogo(rs.getInt(prefijo + "_id"), rs.getString(prefijo + "_nombre"));
    }

    /** Traduce una SQLException a una excepción de dominio con mensaje entendible. */
    private DAOException traducir(String operacion, SQLException e) {
        if (e.getErrorCode() == MYSQL_DUPLICADO) {
            return new DuplicadoException(
                    "Ya existe un registro con el mismo tipo y número de identificación.", e);
        }
        return new DAOException("Error de base de datos al " + operacion + ".", e);
    }
}
