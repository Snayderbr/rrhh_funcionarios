package co.edu.iudigital.rrhh.servicio;

import co.edu.iudigital.rrhh.dao.CatalogoDAO;
import co.edu.iudigital.rrhh.dao.FuncionarioDAO;
import co.edu.iudigital.rrhh.dao.TipoCatalogo;
import co.edu.iudigital.rrhh.dao.impl.CatalogoDAOImpl;
import co.edu.iudigital.rrhh.dao.impl.FuncionarioDAOImpl;
import co.edu.iudigital.rrhh.excepciones.DAOException;
import co.edu.iudigital.rrhh.excepciones.ValidacionException;
import co.edu.iudigital.rrhh.modelo.Catalogos;
import co.edu.iudigital.rrhh.modelo.Funcionario;
import co.edu.iudigital.rrhh.modelo.InformacionAcademica;
import co.edu.iudigital.rrhh.modelo.MiembroFamiliar;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/** Lógica de negocio: valida los datos y delega la persistencia en los DAO. */
public class FuncionarioService {

    private static final Pattern IDENTIFICACION = Pattern.compile("^[A-Za-z0-9]{5,20}$");
    private static final Pattern TELEFONO = Pattern.compile("^[0-9+ ]{7,20}$");
    private static final int EDAD_MINIMA_FUNCIONARIO = 18;

    private final FuncionarioDAO funcionarioDAO;
    private final CatalogoDAO catalogoDAO;

    public FuncionarioService() {
        this(new FuncionarioDAOImpl(), new CatalogoDAOImpl());
    }

    public FuncionarioService(FuncionarioDAO funcionarioDAO, CatalogoDAO catalogoDAO) {
        this.funcionarioDAO = funcionarioDAO;
        this.catalogoDAO = catalogoDAO;
    }

    public List<Funcionario> listar() throws DAOException {
        return funcionarioDAO.listar();
    }

    public Funcionario buscar(int id) throws DAOException {
        return funcionarioDAO.buscarPorId(id);
    }

    public Funcionario crear(Funcionario f) throws ValidacionException, DAOException {
        validar(f);
        return funcionarioDAO.crear(f);
    }

    public void actualizar(Funcionario f) throws ValidacionException, DAOException {
        if (f.getId() == null) {
            throw new ValidacionException("El funcionario a actualizar no tiene identificador.");
        }
        validar(f);
        funcionarioDAO.actualizar(f);
    }

    public void eliminar(int id) throws DAOException {
        funcionarioDAO.eliminar(id);
    }

    public Catalogos cargarCatalogos() throws DAOException {
        return new Catalogos(
                catalogoDAO.listar(TipoCatalogo.TIPO_IDENTIFICACION),
                catalogoDAO.listar(TipoCatalogo.ESTADO_CIVIL),
                catalogoDAO.listar(TipoCatalogo.SEXO),
                catalogoDAO.listar(TipoCatalogo.ROL_FAMILIAR),
                catalogoDAO.listar(TipoCatalogo.NIVEL_ESTUDIO),
                catalogoDAO.listar(TipoCatalogo.UNIVERSIDAD));
    }

    // ------------------------------------------------------------ validaciones
    public void validar(Funcionario f) throws ValidacionException {
        requerido(f.getTipoIdentificacion(), "el tipo de identificación");
        identificacion(f.getNumeroIdentificacion());
        texto(f.getNombres(), "Los nombres", 80);
        texto(f.getApellidos(), "Los apellidos", 80);
        requerido(f.getEstadoCivil(), "el estado civil");
        requerido(f.getSexo(), "el sexo");
        texto(f.getDireccion(), "La dirección", 150);
        if (f.getTelefono() == null || !TELEFONO.matcher(f.getTelefono().trim()).matches()) {
            throw new ValidacionException("El teléfono debe tener entre 7 y 20 caracteres (dígitos, espacios o +).");
        }
        fechaPasada(f.getFechaNacimiento(), "de nacimiento del funcionario");
        if (Period.between(f.getFechaNacimiento(), LocalDate.now()).getYears() < EDAD_MINIMA_FUNCIONARIO) {
            throw new ValidacionException("El funcionario debe ser mayor de " + EDAD_MINIMA_FUNCIONARIO + " años.");
        }

        Set<String> vistos = new HashSet<>();
        for (MiembroFamiliar m : f.getGrupoFamiliar()) {
            validarMiembro(m);
            String clave = m.getTipoIdentificacion().getId() + "-" + m.getNumeroIdentificacion();
            if (!vistos.add(clave)) {
                throw new ValidacionException("El miembro familiar con identificación "
                        + m.getNumeroIdentificacion() + " está repetido.");
            }
        }
        for (InformacionAcademica ia : f.getInformacionAcademica()) {
            validarAcademica(ia);
        }
    }

    public void validarMiembro(MiembroFamiliar m) throws ValidacionException {
        requerido(m.getTipoIdentificacion(), "el tipo de identificación del familiar");
        identificacion(m.getNumeroIdentificacion());
        texto(m.getNombres(), "Los nombres del familiar", 80);
        texto(m.getApellidos(), "Los apellidos del familiar", 80);
        fechaPasada(m.getFechaNacimiento(), "de nacimiento del familiar");
        requerido(m.getSexo(), "el sexo del familiar");
        requerido(m.getRolFamiliar(), "el rol del familiar");
    }

    public void validarAcademica(InformacionAcademica ia) throws ValidacionException {
        requerido(ia.getUniversidad(), "la universidad");
        requerido(ia.getNivelEstudio(), "el nivel de estudio");
        texto(ia.getTitulo(), "El título", 150);
    }

    private void requerido(Object valor, String campo) throws ValidacionException {
        if (valor == null) {
            throw new ValidacionException("Debe seleccionar " + campo + ".");
        }
    }

    private void texto(String valor, String campo, int max) throws ValidacionException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionException(campo + " es obligatorio.");
        }
        if (valor.trim().length() > max) {
            throw new ValidacionException(campo + " no puede superar " + max + " caracteres.");
        }
    }

    private void identificacion(String valor) throws ValidacionException {
        if (valor == null || !IDENTIFICACION.matcher(valor.trim()).matches()) {
            throw new ValidacionException("El número de identificación debe ser alfanumérico (5 a 20 caracteres).");
        }
    }

    private void fechaPasada(LocalDate fecha, String descripcion) throws ValidacionException {
        if (fecha == null) {
            throw new ValidacionException("La fecha " + descripcion + " es obligatoria.");
        }
        if (fecha.isAfter(LocalDate.now())) {
            throw new ValidacionException("La fecha " + descripcion + " no puede ser futura.");
        }
    }
}
