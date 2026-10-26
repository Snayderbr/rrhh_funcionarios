package co.edu.iudigital.rrhh.excepciones;

/** Se lanza cuando los datos ingresados por el usuario no cumplen las reglas de negocio. */
public class ValidacionException extends Exception {
    private static final long serialVersionUID = 1L;

    public ValidacionException(String mensaje) { super(mensaje); }
}
