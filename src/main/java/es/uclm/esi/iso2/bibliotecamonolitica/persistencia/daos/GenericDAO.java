package es.uclm.esi.iso2.bibliotecamonolitica.persistencia.daos;

import java.util.List;
import java.util.Optional;
import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.DAOException;

/**
 * Interfaz genérica para el patrón DAO.
 * Define operaciones básicas de persistencia para cualquier entidad.
 * 
 * @param <T> Tipo de entidad
 * @param <ID> Tipo del identificador de la entidad
 * 
 * @author Sistema de Gestión de Bibliotecas
 * @version 1.0
 */
public interface GenericDAO<T, ID> {
    
    /**
     * Crea un nuevo registro en la base de datos.
     * 
     * @param entity Entidad a crear
     * @return Entidad creada con su ID asignado
     * @throws DAOException Si ocurre un error en la operación
     */
    T create(T entity) throws DAOException;
    
    /**
     * Busca una entidad por su identificador.
     * 
     * @param id Identificador de la entidad
     * @return Optional que contiene la entidad si existe
     * @throws DAOException Si ocurre un error en la operación
     */
    Optional<T> findById(ID id) throws DAOException;
    
    /**
     * Actualiza un registro existente.
     * 
     * @param entity Entidad con los datos actualizados
     * @return Entidad actualizada
     * @throws DAOException Si ocurre un error en la operación
     */
    T update(T entity) throws DAOException;
    
    /**
     * Elimina un registro por su identificador.
     * 
     * @param id Identificador de la entidad a eliminar
     * @throws DAOException Si ocurre un error en la operación
     */
    void delete(ID id) throws DAOException;
    
    /**
     * Recupera todos los registros.
     * 
     * @return Lista de todas las entidades
     * @throws DAOException Si ocurre un error en la operación
     */
    List<T> findAll() throws DAOException;
}