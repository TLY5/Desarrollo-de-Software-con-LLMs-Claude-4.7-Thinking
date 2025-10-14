package es.uclm.esi.iso2.bibliotecamonolitica.persistencia.conexion;

import java.sql.Connection;
import java.sql.SQLException;

import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.DatabaseConnectionException;

/**
 * Clase abstracta que implementa el patrón Singleton para gestionar conexiones a bases de datos.
 * Proporciona una interfaz común para diferentes implementaciones de bases de datos.
 * 
 * @author Sistema de Gestión de Bibliotecas
 * @version 1.0
 */
public abstract class Agent {
    
    /** Instancia única del agente siguiendo el patrón Singleton */
    private static Agent instance;
    
    /**
     * Constructor protegido para evitar instanciación directa
     */
    protected Agent() {
        // Constructor vacío para ser extendido
    }
    
    /**
     * Método para obtener la instancia del agente según el tipo especificado.
     * Implementa el patrón Singleton.
     * 
     * @param dbType Tipo de base de datos ("mysql", "oracle", etc.)
     * @return Instancia del agente correspondiente
     * @throws DatabaseConnectionException Si el tipo de base de datos no está soportado
     */
    public static synchronized Agent getInstance(String dbType) throws DatabaseConnectionException {
        if (instance == null) {
            switch (dbType.toLowerCase()) {
                case "mysql":
                    instance = new AgentMySQL();
                    break;
                // Aquí se podrían agregar más casos para otros SGBD
                default:
                    throw new DatabaseConnectionException("Tipo de base de datos no soportado: " + dbType);
            }
        }
        return instance;
    }
    
    /**
     * Establece una conexión con la base de datos.
     * 
     * @return Conexión establecida
     * @throws DatabaseConnectionException Si ocurre un error al establecer la conexión
     */
    public abstract Connection connect() throws DatabaseConnectionException;
    
    /**
     * Cierra una conexión con la base de datos.
     * 
     * @param connection Conexión a cerrar
     */
    public abstract void disconnect(Connection connection);
    
    /**
     * Ejecuta una consulta de tipo SELECT.
     * 
     * @param sql Consulta SQL a ejecutar
     * @param params Parámetros para la consulta preparada
     * @return ResultSet con los resultados de la consulta
     * @throws SQLException Si ocurre un error al ejecutar la consulta
     * @throws DatabaseConnectionException Si ocurre un error de conexión
     */
    public abstract java.sql.ResultSet executeQuery(String sql, Object... params) 
            throws SQLException, DatabaseConnectionException;
    
    /**
     * Ejecuta una operación de actualización (INSERT, UPDATE, DELETE).
     * 
     * @param sql Sentencia SQL a ejecutar
     * @param params Parámetros para la sentencia preparada
     * @return Número de filas afectadas o ID generado para INSERTs
     * @throws SQLException Si ocurre un error al ejecutar la operación
     * @throws DatabaseConnectionException Si ocurre un error de conexión
     */
    public abstract int executeUpdate(String sql, Object... params) 
            throws SQLException, DatabaseConnectionException;
    
    /**
     * Inicia una transacción.
     * 
     * @return Conexión con transacción iniciada
     * @throws SQLException Si ocurre un error al iniciar la transacción
     * @throws DatabaseConnectionException Si ocurre un error de conexión
     */
    public abstract Connection beginTransaction() 
            throws SQLException, DatabaseConnectionException;
    
    /**
     * Confirma una transacción.
     * 
     * @param connection Conexión con la transacción a confirmar
     * @throws SQLException Si ocurre un error al confirmar la transacción
     */
    public abstract void commitTransaction(Connection connection) throws SQLException;
    
    /**
     * Revierte una transacción.
     * 
     * @param connection Conexión con la transacción a revertir
     */
    public abstract void rollbackTransaction(Connection connection);
}