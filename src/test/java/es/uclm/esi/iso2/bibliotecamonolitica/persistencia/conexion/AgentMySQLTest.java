package es.uclm.esi.iso2.bibliotecamonolitica.persistencia.agentes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import es.uclm.esi.iso2.bibliotecamonolitica.config.app.ConfigManager;
import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.DatabaseConnectionException;

/**
 * Pruebas unitarias para la clase AgentMySQL.
 * Usa Mockito para simular conexiones reales a la base de datos.
 */
@ExtendWith(MockitoExtension.class)
public class AgentMySQLTest {
    
    @Mock
    private ConfigManager mockConfigManager;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;
    
    @Mock
    private Statement mockStatement;
    
    private Properties mockProperties;
    private AgentMySQL agentMySQL;
    
    @BeforeEach
    public void setUp() throws Exception {
        // Configurar propiedades simuladas
        mockProperties = new Properties();
        mockProperties.setProperty("db.mysql.url", "jdbc:mysql://localhost:3306/biblioteca_test");
        mockProperties.setProperty("db.mysql.user", "test_user");
        mockProperties.setProperty("db.mysql.password", "test_password");
        mockProperties.setProperty("db.mysql.driver", "com.mysql.cj.jdbc.Driver");
        
        // Usar MockedStatic para simular ConfigManager.getInstance()
        try (MockedStatic<ConfigManager> mockedStatic = mockStatic(ConfigManager.class)) {
            mockedStatic.when(ConfigManager::getInstance).thenReturn(mockConfigManager);
            when(mockConfigManager.getDatabaseProperties()).thenReturn(mockProperties);
            
            // Forzar la creación de una nueva instancia de AgentMySQL
            java.lang.reflect.Field instanceField = Agent.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
            
            // Ahora podemos obtener la instancia
            agentMySQL = (AgentMySQL) Agent.getInstance("mysql");
        }
        
        // Configurar behavior de los mocks
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
    }
    
    @Test
    public void testSingletonPattern() throws DatabaseConnectionException {
        // Verificar que se obtiene la misma instancia
        Agent agent1 = Agent.getInstance("mysql");
        Agent agent2 = Agent.getInstance("mysql");
        assertSame(agent1, agent2);
    }
    
    @Test
    public void testGetInstanceWithInvalidType() {
        // Verificar que se lanza excepción con tipo inválido
        assertThrows(DatabaseConnectionException.class, () -> {
            Agent.getInstance("invalid_db_type");
        });
    }
    
    @Test
    public void testExecuteQuery() throws Exception {
        // Simular conexión exitosa
        java.lang.reflect.Method connectMethod = AgentMySQL.class.getDeclaredMethod("connect");
        connectMethod.setAccessible(true);
        
        // Usar ReflectionTestUtils para establecer un mock
        java.lang.reflect.Field connectMethodField = AgentMySQL.class.getDeclaredField("connect");
        connectMethodField.setAccessible(true);
        
        // Mock del método connect para devolver la conexión mock
        AgentMySQL spyAgent = spy(agentMySQL);
        doReturn(mockConnection).when(spyAgent).connect();
        
        // Ejecutar la consulta
        ResultSet resultSet = spyAgent.executeQuery("SELECT * FROM libros WHERE id = ?", 1);
        
        // Verificar que se llamaron los métodos correctos
        verify(mockConnection).prepareStatement("SELECT * FROM libros WHERE id = ?");
        verify(mockPreparedStatement).setObject(1, 1);
        verify(mockPreparedStatement).executeQuery();
        
        // Verificar que el resultado es el esperado
        assertSame(mockResultSet, resultSet);
    }
    
    @Test
    public void testExecuteUpdate() throws Exception {
        // Mock del método connect
        AgentMySQL spyAgent = spy(agentMySQL);
        doReturn(mockConnection).when(spyAgent).connect();
        
        // Simular que la actualización afectó 1 fila
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Ejecutar la actualización
        int result = spyAgent.executeUpdate("UPDATE libros SET titulo = ? WHERE id = ?", "Nuevo título", 1);
        
        // Verificar que se llamaron los métodos correctos
        verify(mockConnection).prepareStatement("UPDATE libros SET titulo = ? WHERE id = ?", Statement.RETURN_GENERATED_KEYS);
        verify(mockPreparedStatement).setObject(1, "Nuevo título");
        verify(mockPreparedStatement).setObject(2, 1);
        verify(mockPreparedStatement).executeUpdate();
        
        // Verificar que el resultado es el esperado
        assertEquals(1, result);
    }
    
    @Test
    public void testBeginTransaction() throws Exception {
        // Mock del método connect
        AgentMySQL spyAgent = spy(agentMySQL);
        doReturn(mockConnection).when(spyAgent).connect();
        
        // Iniciar transacción
        Connection connection = spyAgent.beginTransaction();
        
        // Verificar que se deshabilitó autocommit
        verify(mockConnection).setAutoCommit(false);
        
        // Verificar que el resultado es la conexión esperada
        assertSame(mockConnection, connection);
    }
    
    @Test
    public void testCommitTransaction() throws Exception {
        // Confirmar transacción
        agentMySQL.commitTransaction(mockConnection);
        
        // Verificar que se llamaron los métodos correctos
        verify(mockConnection).commit();
        verify(mockConnection).setAutoCommit(true);
    }
    
    @Test
    public void testRollbackTransaction() throws Exception {
        // Revertir transacción
        agentMySQL.rollbackTransaction(mockConnection);
        
        // Verificar que se llamaron los métodos correctos
        verify(mockConnection).rollback();
        verify(mockConnection).setAutoCommit(true);
    }
    
    @Test
    public void testRollbackTransactionHandlesException() throws Exception {
        // Simular que rollback lanza excepción
        doThrow(new SQLException("Test exception")).when(mockConnection).rollback();
        
        // No debería propagar la excepción
        assertDoesNotThrow(() -> {
            agentMySQL.rollbackTransaction(mockConnection);
        });
    }
    
    @Test
    public void testDisconnect() throws Exception {
        // Cerrar conexión
        agentMySQL.disconnect(mockConnection);
        
        // Verificar que se cerró la conexión
        verify(mockConnection).close();
    }
    
    @Test
    public void testDisconnectWithNullConnection() {
        // No debería lanzar excepción con conexión nula
        assertDoesNotThrow(() -> {
            agentMySQL.disconnect(null);
        });
    }
}