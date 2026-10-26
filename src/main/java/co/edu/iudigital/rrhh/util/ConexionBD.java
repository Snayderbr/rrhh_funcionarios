package co.edu.iudigital.rrhh.util;

import co.edu.iudigital.rrhh.excepciones.DAOException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Fábrica de conexiones JDBC. Lee la configuración de db.properties. */
public final class ConexionBD {
    private static final Properties CONFIG = new Properties();

    static {
        try (InputStream in = ConexionBD.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                CONFIG.load(in);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private ConexionBD() { }

    public static Connection obtener() throws DAOException {
        try {
            return DriverManager.getConnection(
                    CONFIG.getProperty("db.url"),
                    CONFIG.getProperty("db.user"),
                    CONFIG.getProperty("db.password"));
        } catch (SQLException e) {
            throw new DAOException("No fue posible conectar con la base de datos. "
                    + "Verifique que el servidor esté activo y que db.properties sea correcto.", e);
        }
    }
}
