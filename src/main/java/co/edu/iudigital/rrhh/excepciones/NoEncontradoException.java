package co.edu.iudigital.rrhh.excepciones;

/** Se lanza cuando el registro buscado, actualizado o eliminado no existe. */
public class NoEncontradoException extends DAOException {
    private static final long serialVersionUID = 1L;

    public NoEncontradoException(String mensaje) { super(mensaje); }
}
