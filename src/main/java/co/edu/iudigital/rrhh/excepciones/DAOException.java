package co.edu.iudigital.rrhh.excepciones;

/** Excepción base de la capa de acceso a datos. Envuelve errores de JDBC. */
public class DAOException extends Exception {
    private static final long serialVersionUID = 1L;

    public DAOException(String mensaje) { super(mensaje); }

    public DAOException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
