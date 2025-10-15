package es.uclm.esi.iso2.bibliotecamonolitica.persistencia.daos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import es.uclm.esi.iso2.bibliotecamonolitica.modelo.entidades.Categoria;
import es.uclm.esi.iso2.bibliotecamonolitica.modelo.entidades.Libro;
import es.uclm.esi.iso2.bibliotecamonolitica.persistencia.agentes.Agent;
import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.DAOException;
import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.DatabaseConnectionException;
import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.NotNullValueAllowedException;

/**
 * Pruebas unitarias para la clase LibroDAO.
 */
@ExtendWith(MockitoExtension.class)
public class LibroDAOTest {
    
    @Mock
    private Agent mockAgent;
    
    @Mock
    private ResultSet mockResultSet;
    
    @Mock
    private CategoriaDAO mockCategoriaDAO;
    
    private LibroDAO libroDAO;
    private Libro libroTest;
    private Categoria categoriaTest;
    
    @BeforeEach
    public void setUp() throws Exception {
        // Configurar mocks estáticos
        try (MockedStatic<Agent> mockedStatic = mockStatic(Agent.class)) {
            mockedStatic.when(() -> Agent.getInstance(anyString())).thenReturn(mockAgent);
            
            // Crear instancia de DAO
            libroDAO = new LibroDAO();
            
            // Inyectar mock de CategoriaDAO mediante reflection
            java.lang.reflect.Field categoriaDAOField = LibroDAO.class.getDeclaredField("categoriaDAO");
            categoriaDAOField.setAccessible(true);
            categoriaDAOField.set(libroDAO, mockCategoriaDAO);
        }
        
        // Crear objetos de prueba
        categoriaTest = new Categoria();
        categoriaTest.setId(1L);
        categoriaTest.setNombre("Novela");
        
        libroTest = new Libro();
        libroTest.setId(1L);
        libroTest.setTitulo("Don Quijote de la Mancha");
        libroTest.setAutor("Miguel de Cervantes");
        libroTest.setEditorial("Cátedra");
        libroTest.setAnioPublicacion(1605);
        libroTest.setIsbn("9788437622774");
        libroTest.setCategoria(categoriaTest);
        libroTest.setDescripcion("La obra cumbre de la literatura española");
        libroTest.setFechaAlta(new java.util.Date());
    }
    
    @Test
    public void testCreate() throws Exception {
        // Configurar comportamiento del mock
        when(mockAgent.executeUpdate(anyString(), any())).thenReturn(1);
        
        // Ejecutar método
        Libro resultado = libroDAO.create(libroTest);
        
        // Verificar comportamiento y resultado
        verify(mockAgent).executeUpdate(
            eq("INSERT INTO libros (titulo, autor, editorial, anio_publicacion, isbn, categoria_id, descripcion, fecha_alta) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"),
            eq(libroTest.getTitulo()),
            eq(libroTest.getAutor()),
            eq(libroTest.getEditorial()),
            eq(libroTest.getAnioPublicacion()),
            eq(libroTest.getIsbn()),
            eq(libroTest.getCategoria().getId()),
            eq(libroTest.getDescripcion()),
            any(Date.class)
        );
        
        assertNotNull(resultado);
        assertEquals(libroTest, resultado);
    }
    
    @Test
    public void testCreateHandlesException() throws Exception {
        // Configurar comportamiento del mock para lanzar excepción
        when(mockAgent.executeUpdate(anyString(), any())).thenThrow(new SQLException("Test exception"));
        
        // Verificar que se propaga como DAOException
        assertThrows(DAOException.class, () -> {
            libroDAO.create(libroTest);
        });
    }
    
    @Test
    public void testFindById() throws Exception {
        // Configurar comportamiento del mock
        when(mockAgent.executeQuery(anyString(), eq(1L))).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getLong("id")).thenReturn(1L);
        when(mockResultSet.getString("titulo")).thenReturn("Don Quijote de la Mancha");
        when(mockResultSet.getString("autor")).thenReturn("Miguel de Cervantes");
        when(mockResultSet.getString("editorial")).thenReturn("Cátedra");
        when(mockResultSet.getInt("anio_publicacion")).thenReturn(1605);
        when(mockResultSet.getString("isbn")).thenReturn("9788437622774");
        when(mockResultSet.getString("descripcion")).thenReturn("La obra cumbre de la literatura española");
        when(mockResultSet.getDate("fecha_alta")).thenReturn(new Date(System.currentTimeMillis()));
        when(mockResultSet.getLong("categoria_id")).thenReturn(1L);
        when(mockResultSet.wasNull()).thenReturn(false);
        when(mockCategoriaDAO.findById(1L)).thenReturn(Optional.of(categoriaTest));
        
        // Ejecutar método
        Optional<Libro> resultado = libroDAO.findById(1L);
        
        // Verificar comportamiento y resultado
        verify(mockAgent).executeQuery("SELECT * FROM libros WHERE id = ?", 1L);
        
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Don Quijote de la Mancha", resultado.get().getTitulo());
        assertEquals("Miguel de Cervantes", resultado.get().getAutor());
        assertEquals(categoriaTest, resultado.get().getCategoria());
    }
    
    @Test
    public void testFindByIdNotFound() throws Exception {
        // Configurar comportamiento del mock
        when(mockAgent.executeQuery(anyString(), eq(999L))).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Ejecutar método
        Optional<Libro> resultado = libroDAO.findById(999L);
        
        // Verificar comportamiento y resultado
        verify(mockAgent).executeQuery("SELECT * FROM libros WHERE id = ?", 999L);
        
        assertFalse(resultado.isPresent());
    }
    
    @Test
    public void testUpdate() throws Exception {
        // Configurar comportamiento del mock
        when(mockAgent.executeUpdate(anyString(), any())).thenReturn(1);
        
        // Ejecutar método
        Libro resultado = libroDAO.update(libroTest);
        
        // Verificar comportamiento y resultado
        verify(mockAgent).executeUpdate(
            eq("UPDATE libros SET titulo = ?, autor = ?, editorial = ?, anio_publicacion = ?, isbn = ?, categoria_id = ?, descripcion = ? WHERE id = ?"),
            eq(libroTest.getTitulo()),
            eq(libroTest.getAutor()),
            eq(libroTest.getEditorial()),
            eq(libroTest.getAnioPublicacion()),
            eq(libroTest.getIsbn()),
            eq(libroTest.getCategoria().getId()),
            eq(libroTest.getDescripcion()),
            eq(libroTest.getId())
        );
        
        assertNotNull(resultado);
        assertEquals(libroTest, resultado);
    }
    
    @Test
    public void testDelete() throws Exception {
        // Configurar comportamiento del mock
        when(mockAgent.executeUpdate(anyString(), eq(1L))).thenReturn(1);
        
        // Ejecutar método
        libroDAO.delete(1L);
        
        // Verificar comportamiento
        verify(mockAgent).executeUpdate("DELETE FROM libros WHERE id = ?", 1L);
    }
    
    @Test
    public void testDeleteNotFound() throws Exception {
        // Configurar comportamiento del mock
        when(mockAgent.executeUpdate(anyString(), eq(999L))).thenReturn(0);
        
        // Verificar que se lanza excepción
        assertThrows(DAOException.class, () -> {
            libroDAO.delete(999L);
        });
    }
    
    @Test
    public void testFindAll() throws Exception {
        // Configurar comportamiento del mock para simular dos libros
        when(mockAgent.executeQuery(eq("SELECT * FROM libros"))).thenReturn(mockResultSet);
        
        // Simular dos filas en el ResultSet
        when(mockResultSet.next())
            .thenReturn(true) // Primera fila
            .thenReturn(true) // Segunda fila
            .thenReturn(false); // Fin del resultset
            
        // Valores para la primera fila
        Answer<Object> firstRowAnswer = invocation -> {
            String columnName = invocation.getArgument(0);
            switch (columnName) {
                case "id": return 1L;
                case "titulo": return "Don Quijote de la Mancha";
                case "autor": return "Miguel de Cervantes";
                case "editorial": return "Cátedra";
                case "anio_publicacion": return 1605;
                case "isbn": return "9788437622774";
                case "descripcion": return "La obra cumbre de la literatura española";
                case "fecha_alta": return new Date(System.currentTimeMillis());
                case "categoria_id": return 1L;
                default: return null;
            }
        };
        
        // Valores para la segunda fila
        Answer<Object> secondRowAnswer = invocation -> {
            String columnName = invocation.getArgument(0);
            switch (columnName) {
                case "id": return 2L;
                case "titulo": return "Cien años de soledad";
                case "autor": return "Gabriel García Márquez";
                case "editorial": return "Sudamericana";
                case "anio_publicacion": return 1967;
                case "isbn": return "9780307474728";
                case "descripcion": return "Una saga familiar en Macondo";
                case "fecha_alta": return new Date(System.currentTimeMillis());
                case "categoria_id": return 1L;
                default: return null;
            }
        };
        
        // Configurar el comportamiento con las dos respuestas
        when(mockResultSet.getLong(anyString()))
            .thenAnswer(firstRowAnswer)
            .thenAnswer(secondRowAnswer);
        when(mockResultSet.getString(anyString()))
            .thenAnswer(firstRowAnswer)
            .thenAnswer(secondRowAnswer);
        when(mockResultSet.getInt(anyString()))
            .thenAnswer(firstRowAnswer)
            .thenAnswer(secondRowAnswer);
        when(mockResultSet.getDate(anyString()))
            .thenReturn(new Date(System.currentTimeMillis()))
            .thenReturn(new Date(System.currentTimeMillis()));
        when(mockResultSet.wasNull()).thenReturn(false);
        
        // Mock para la categoría
        when(mockCategoriaDAO.findById(1L)).thenReturn(Optional.of(categoriaTest));
        
        // Ejecutar método
        List<Libro> resultados = libroDAO.findAll();
        
        // Verificar comportamiento y resultado
        verify(mockAgent).executeQuery("SELECT * FROM libros");
        
        assertEquals(2, resultados.size());
        assertEquals("Don Quijote de la Mancha", resultados.get(0).getTitulo());
        assertEquals("Cien años de soledad", resultados.get(1).getTitulo());
    }
    
    @Test
    public void testFindByTitulo() throws Exception {
        // Configurar comportamiento del mock
        when(mockAgent.executeQuery(eq("SELECT * FROM libros WHERE titulo LIKE ?"), eq("%Quijote%"))).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getLong("id")).thenReturn(1L);
        when(mockResultSet.getString("titulo")).thenReturn("Don Quijote de la Mancha");
        when(mockResultSet.getString("autor")).thenReturn("Miguel de Cervantes");
        when(mockResultSet.getString("editorial")).thenReturn("Cátedra");
        when(mockResultSet.getInt("anio_publicacion")).thenReturn(1605);
        when(mockResultSet.getString("isbn")).thenReturn("9788437622774");
        when(mockResultSet.getString("descripcion")).thenReturn("La obra cumbre de la literatura española");
        when(mockResultSet.getDate("fecha_alta")).thenReturn(new Date(System.currentTimeMillis()));
        when(mockResultSet.getLong("categoria_id")).thenReturn(1L);
        when(mockResultSet.wasNull()).thenReturn(false);
        when(mockCategoriaDAO.findById(1L)).thenReturn(Optional.of(categoriaTest));
        
        // Ejecutar método
        List<Libro> resultados = libroDAO.findByTitulo("Quijote");
        
        // Verificar comportamiento y resultado
        verify(mockAgent).executeQuery("SELECT * FROM libros WHERE titulo LIKE ?", "%Quijote%");
        
        assertEquals(1, resultados.size());
        assertEquals("Don Quijote de la Mancha", resultados.get(0).getTitulo());
    }
}