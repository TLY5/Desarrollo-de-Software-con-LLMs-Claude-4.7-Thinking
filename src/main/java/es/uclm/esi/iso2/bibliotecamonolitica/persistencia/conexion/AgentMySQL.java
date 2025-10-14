package es.uclm.esi.iso2.bibliotecamonolitica.persistencia.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import es.uclm.esi.iso2.bibliotecamonolitica.config.app.ConfigManager;
import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.DatabaseConnectionException;
import es.uclm.esi.iso2.bibliotecamonolitica.util.helpers.LogHelper;

/**
 * Implementación concreta del agente de base de datos para MySQL.
 * Gestiona las conexiones y operaciones específicas para MySQL.
 * 
 * @author Sistema de Gestión de Bibliotecas
 * @version 1.0
 */
public class AgentMySQL extends Agent {
    
    private static final LogHelper logger = LogHelper.getLogger(AgentMySQL.class);
    private String url;
    private String user;
    private String password;
    private String driverClassName;
    
    /**
     * Constructor para AgentMySQL.
     * Carga la configuración desde el archivo de propiedades.
     * 
     * @throws DatabaseConnectionException Si hay un error al cargar la configuración
     */
    protected AgentMySQL() throws DatabaseConnectionException {
        try {
            Properties props = ConfigManager.getInstance().getDatabaseProperties();
            this.url = props.getProperty("db.mysql.url");
            this.user = props.getProperty("db.mysql.user");
            this.password = props.getProperty("db.mysql.password");
            this.driverClassName = props.getProperty("db.mysql.driver");
            
            // Registra el driver JDBC
            Class.forName(driverClassName);
            logger.info("Driver MySQL registrado correctamente: {}", driverClassName);
        } catch (ClassNotFoundException e) {
            logger.error("Error al cargar el driver MySQL", e);
            throw new DatabaseConnectionException("No se pudo cargar el driver MySQL", e);
        } catch (Exception e) {
            logger.error("Error al inicializar AgentMySQL", e);
            throw new DatabaseConnectionException("Error de configuración de MySQL", e);
        }
    }
    
    @Override
    public Connection connect() throws DatabaseConnectionException {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            logger.debug("Conexión establecida con MySQL");
            return connection;
        } catch (SQLException e) {
            logger.error("Error al conectar con MySQL", e);
            throw new DatabaseConnectionException("No se pudo establecer conexión con MySQL", e);
        }
    }
    
    @Override
    public void disconnect(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                logger.debug("Conexión cerrada correctamente");
            } catch (SQLException e) {
                logger.warn("Error al cerrar la conexión", e);
            }
        }
    }
    
    @Override
    public ResultSet executeQuery(String sql, Object... params) throws SQLException, DatabaseConnectionException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = connect();
            statement = connection.prepareStatement(sql);
            
            // Establecer los parámetros en la consulta preparada
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            
            resultSet = statement.executeQuery();
            logger.debug("Consulta ejecutada: {}", sql);
            
            // No cerrar la conexión aquí, ya que se necesita para leer el ResultSet
            return resultSet;
            
        } catch (SQLException e) {
            disconnect(connection);
            logger.error("Error al ejecutar consulta: {}", sql, e);
            throw e;
        }
    }
    
    @Override
    public int executeUpdate(String sql, Object... params) throws SQLException, DatabaseConnectionException {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = connect();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            // Establecer los parámetros en la consulta preparada
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            
            int result = statement.executeUpdate();
            logger.debug("Operación de actualización ejecutada: {}", sql);
            
            // Para operaciones INSERT, devolver el ID generado si existe
            if (sql.trim().toLowerCase().startsWith("insert")) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    result = generatedKeys.getInt(1);
                }
            }
            
            return result;
            
        } finally {
            disconnect(connection);
        }
    }
    
    @Override
    public Connection beginTransaction() throws SQLException, DatabaseConnectionException {
        Connection connection = connect();
        connection.setAutoCommit(false);
        logger.debug("Transacción iniciada");
        return connection;
    }
    
    @Override
    public void commitTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.commit();
            connection.setAutoCommit(true);
            logger.debug("Transacción confirmada");
        }
    }
    
    @Override
    public void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
                logger.debug("Transacción revertida");
            } catch (SQLException e) {
                logger.warn("Error al revertir la transacción", e);
            }
        }
    }
}