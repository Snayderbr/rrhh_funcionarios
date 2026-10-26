package co.edu.iudigital.rrhh.dao;

import co.edu.iudigital.rrhh.excepciones.DAOException;
import co.edu.iudigital.rrhh.modelo.Funcionario;

import java.util.List;

/** Contrato de acceso a datos para funcionarios (CRUD). */
public interface FuncionarioDAO {

    /** Inserta el funcionario con su grupo familiar e información académica (transacción). */
    Funcionario crear(Funcionario funcionario) throws DAOException;

    /** Actualiza el funcionario y reemplaza sus relaciones (transacción). */
    void actualizar(Funcionario funcionario) throws DAOException;

    /** Elimina el funcionario y, en cascada, sus relaciones. */
    void eliminar(int id) throws DAOException;

    /** Busca un funcionario con todas sus relaciones cargadas. */
    Funcionario buscarPorId(int id) throws DAOException;

    /** Lista los funcionarios (sin cargar relaciones). */
    List<Funcionario> listar() throws DAOException;
}
