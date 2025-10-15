package es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la clase NotNullValueAllowedException.
 */
public class NotNullValueAllowedExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        String errorMessage = "El valor no puede ser nulo o vacío";
        NotNullValueAllowedException exception = new NotNullValueAllowedException(errorMessage);
        
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    public void testConstructorWithMessageAndCause() {
        String errorMessage = "El valor no puede ser nulo o vacío";
        Throwable cause = new IllegalArgumentException("Causa original");
        
        NotNullValueAllowedException exception = new NotNullValueAllowedException(errorMessage, cause);
        
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    public void testExceptionHierarchy() {
        NotNullValueAllowedException exception = new NotNullValueAllowedException("Test");
        
        // Verificar que hereda de Exception
        assertTrue(exception instanceof Exception);
    }
}