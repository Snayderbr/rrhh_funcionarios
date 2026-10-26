package co.edu.iudigital.rrhh.dao;

import co.edu.iudigital.rrhh.excepciones.DAOException;
import co.edu.iudigital.rrhh.modelo.Catalogo;

import java.util.List;

/** Consulta de catálogos (listas de valores). */
public interface CatalogoDAO {
    List<Catalogo> listar(TipoCatalogo tipo) throws DAOException;
}
