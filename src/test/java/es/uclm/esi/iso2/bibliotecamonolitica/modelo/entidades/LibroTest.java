package es.uclm.esi.iso2.bibliotecamonolitica.modelo.entidades;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.NotNullValueAllowedException;

/**
 * Pruebas unitarias para la clase Libro.
 */
public class LibroTest {
    
    private Libro libro;
    private Categoria categoria;
    
    @BeforeEach
    public void setUp() throws NotNullValueAllowedException {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Novela");
        
        libro = new Libro();
        libro.setId(1L);
        libro.setTitulo("Don Quijote de la Mancha");
        libro.setAutor("Miguel de Cervantes");
        libro.setEditorial("Cátedra");
        libro.setAnioPublicacion(1605);
        libro.setIsbn("9788437622774");
        libro.setCategoria(categoria);
        libro.setDescripcion("La obra cumbre de la literatura española");
        libro.setFechaAlta(new Date());
    }
    
    @Test
    @DisplayName("Test de creación de libro válido")
    public void testCreacionLibroValido() {
        assertNotNull(libro);
        assertEquals("Don Quijote de la Mancha", libro.getTitulo());
        assertEquals("Miguel de Cervantes", libro.getAutor());
        assertEquals("Cátedra", libro.getEditorial());
        assertEquals(1605, libro.getAnioPublicacion());
        assertEquals("9788437622774", libro.getIsbn());
        assertNotNull(libro.getCategoria());
        assertEquals("Novela", libro.getCategoria().getNombre());
    }
    
    @Test
    @DisplayName("Test de validación de título nulo o vacío")
    public void testValidacionTituloNuloOVacio() {
        assertThrows(NotNullValueAllowedException.class, () -> {
            libro.setTitulo(null);
        });
        
        assertThrows(NotNullValueAllowedException.class, () -> {
            libro.setTitulo("");
        });
        
        assertThrows(NotNullValueAllowedException.class, () -> {
            libro.setTitulo("  ");
        });
    }
    
    @Test
    @DisplayName("Test de validación de autor nulo o vacío")
    public void testValidacionAutorNuloOVacio() {
        assertThrows(NotNullValueAllowedException.class, () -> {
            libro.setAutor(null);
        });
        
        assertThrows(NotNullValueAllowedException.class, () -> {
            libro.setAutor("");
        });
        
        assertThrows(NotNullValueAllowedException.class, () -> {
            libro.setAutor("  ");
        });
    }
    
    @Test
    @DisplayName("Test de agregar ejemplares")
    public void testAgregarEjemplares() {
        // El libro empieza sin ejemplares
        assertEquals(0, libro.getEjemplares().size());
        
        // Añadimos un ejemplar
        Ejemplar ejemplar1 = libro.agregarEjemplar("Estantería A");
        assertEquals(1, libro.getEjemplares().size());
        assertEquals(EstadoEjemplar.DISPONIBLE, ejemplar1.getEstado());
        assertEquals("Estantería A", ejemplar1.getUbicacion());
        assertEquals(libro, ejemplar1.getLibro());
        
        // Añadimos un segundo ejemplar
        Ejemplar ejemplar2 = libro.agregarEjemplar("Estantería B");
        assertEquals(2, libro.getEjemplares().size());
        assertNotEquals(ejemplar1.getCodigoBarras(), ejemplar2.getCodigoBarras());
    }
    
    @Test
    @DisplayName("Test de eliminar ejemplares")
    public void testEliminarEjemplares() {
        // Añadimos dos ejemplares
        Ejemplar ejemplar1 = libro.agregarEjemplar("Estantería A");
        Ejemplar ejemplar2 = libro.agregarEjemplar("Estantería B");
        assertEquals(2, libro.getEjemplares().size());
        
        // Eliminamos un ejemplar
        boolean resultado = libro.eliminarEjemplar(ejemplar1);
        assertTrue(resultado);
        assertEquals(1, libro.getEjemplares().size());
        assertEquals(ejemplar2, libro.getEjemplares().get(0));
        
        // Intentamos eliminar un ejemplar que ya no existe
        resultado = libro.eliminarEjemplar(ejemplar1);
        assertFalse(resultado);
        assertEquals(1, libro.getEjemplares().size());
    }
    
    @Test
    @DisplayName("Test de contar ejemplares disponibles")
    public void testContarEjemplaresDisponibles() {
        // Añadimos tres ejemplares
        Ejemplar ejemplar1 = libro.agregarEjemplar("Estantería A");
        Ejemplar ejemplar2 = libro.agregarEjemplar("Estantería B");
        Ejemplar ejemplar3 = libro.agregarEjemplar("Estantería C");
        
        // Inicialmente todos están disponibles
        assertEquals(3, libro.contarEjemplaresDisponibles());
        
        // Cambiamos el estado de un ejemplar
        ejemplar2.setEstado(EstadoEjemplar.PRESTADO);
        assertEquals(2, libro.contarEjemplaresDisponibles());
        
        // Cambiamos el estado de otro ejemplar
        ejemplar3.setEstado(EstadoEjemplar.REPARACION);
        assertEquals(1, libro.contarEjemplaresDisponibles());
        
        // Cambiamos el estado del último ejemplar disponible
        ejemplar1.setEstado(EstadoEjemplar.PRESTADO);
        assertEquals(0, libro.contarEjemplaresDisponibles());
    }
    
    @Test
    @DisplayName("Test de equals y hashCode")
    public void testEqualsAndHashCode() {
        // Crear un libro con el mismo ISBN
        Libro libroMismoISBN = new Libro();
        try {
            libroMismoISBN.setId(2L);
            libroMismoISBN.setTitulo("Don Quijote - Otra edición");
            libroMismoISBN.setAutor("Miguel de Cervantes");
            libroMismoISBN.setIsbn("9788437622774");
        } catch (NotNullValueAllowedException e) {
            fail("No debería lanzar excepción aquí");
        }
        
        // Mismo ISBN, deberían ser iguales
        assertEquals(libro, libroMismoISBN);
        assertEquals(libro.hashCode(), libroMismoISBN.hashCode());
        
        // Crear un libro con distinto ISBN
        Libro libroDistintoISBN = new Libro();
        try {
            libroDistintoISBN.setId(3L);
            libroDistintoISBN.setTitulo("Don Quijote de la Mancha");
            libroDistintoISBN.setAutor("Miguel de Cervantes");
            libroDistintoISBN.setIsbn("1234567890123");
        } catch (NotNullValueAllowedException e) {
            fail("No debería lanzar excepción aquí");
        }
        
        // Distinto ISBN, no deberían ser iguales
        assertNotEquals(libro, libroDistintoISBN);
        assertNotEquals(libro.hashCode(), libroDistintoISBN.hashCode());
    }
}