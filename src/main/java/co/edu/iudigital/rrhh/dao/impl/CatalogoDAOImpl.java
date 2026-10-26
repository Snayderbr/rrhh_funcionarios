package co.edu.iudigital.rrhh.dao.impl;

import co.edu.iudigital.rrhh.dao.CatalogoDAO;
import co.edu.iudigital.rrhh.dao.TipoCatalogo;
import co.edu.iudigital.rrhh.excepciones.DAOException;
import co.edu.iudigital.rrhh.modelo.Catalogo;
import co.edu.iudigital.rrhh.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogoDAOImpl implements CatalogoDAO {

    @Override
    public List<Catalogo> listar(TipoCatalogo tipo) throws DAOException {
        // El nombre de la tabla viene del enum, nunca del usuario.
        String sql = "SELECT id, nombre FROM " + tipo.getTabla() + " ORDER BY id";
        List<Catalogo> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Catalogo(rs.getInt("id"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al consultar el catálogo " + tipo + ".", e);
        }
        return lista;
    }
}
