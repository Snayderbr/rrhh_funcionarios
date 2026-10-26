package co.edu.iudigital.rrhh.excepciones;

/** Se lanza cuando se viola una restricción de unicidad (identificación repetida). */
public class DuplicadoException extends DAOException {
    private static final long serialVersionUID = 1L;

    public DuplicadoException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
