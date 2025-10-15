package es.uclm.esi.iso2.bibliotecamonolitica.modelo.entidades;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.NotNullValueAllowedException;

/**
 * Pruebas unitarias para la clase Usuario.
 */
public class UsuarioTest {
    
    private Usuario usuario;
    
    @BeforeEach
    public void setUp() throws NotNullValueAllowedException {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellidos("García López");
        usuario.setDniNif("12345678A");
        usuario.setDireccion("Calle Mayor 1, Madrid");
        usuario.setTelefono("600111222");
        usuario.setEmail("juan.garcia@email.com");
        usuario.setTipoUsuario(TipoUsuario.ESTUDIANTE);
        usuario.setEstado(EstadoUsuario.ACTIVO);
    }
    
    @Test
    @DisplayName("Test de creación de usuario válido")
    public void testCreacionUsuarioValido() {
        assertNotNull(usuario);
        assertEquals("Juan", usuario.getNombre());
        assertEquals("García López", usuario.getApellidos());
        assertEquals("12345678A", usuario.getDniNif());
        assertEquals("Calle Mayor 1, Madrid", usuario.getDireccion());
        assertEquals("600111222", usuario.getTelefono());
        assertEquals("juan.garcia@email.com", usuario.getEmail());
        assertEquals(TipoUsuario.ESTUDIANTE, usuario.getTipoUsuario());
        assertEquals(EstadoUsuario.ACTIVO, usuario.getEstado());
    }
    
    @Test
    @DisplayName("Test de constructor con parámetros")
    public void testConstructorConParametros() {
        try {
            Usuario usuarioConParams = new Usuario("Pedro", "Martínez Ruiz", "87654321B", TipoUsuario.PROFESOR);
            
            assertEquals("Pedro", usuarioConParams.getNombre());
            assertEquals("Martínez Ruiz", usuarioConParams.getApellidos());
            assertEquals("87654321B", usuarioConParams.getDniNif());
            assertEquals(TipoUsuario.PROFESOR, usuarioConParams.getTipoUsuario());
            assertEquals(EstadoUsuario.ACTIVO, usuarioConParams.getEstado());
            assertNotNull(usuarioConParams.getFechaAlta());
        } catch (NotNullValueAllowedException e) {
            fail("No debería lanzar excepción con parámetros válidos");
        }
    }
    
    @Test
    @DisplayName("Test de validación de nombre nulo o vacío")
    public void testValidacionNombreNuloOVacio() {
        assertThrows(NotNullValueAllowedException.class, () -> {
            usuario.setNombre(null);
        });
        
        assertThrows(NotNullValueAllowedException.class, () -> {
            usuario.setNombre("");
        });
        
        assertThrows(NotNullValueAllowedException.class, () -> {
            usuario.setNombre("  ");
        });
    }
    
    @Test
    @DisplayName("Test de validación de apellidos nulos o vacíos")
    public void testValidacionApellidosNulosOVacios() {
        assertThrows(NotNullValueAllowedException.class, () -> {
            usuario.setApellidos(null);
        });
        
        assertThrows(NotNullValueAllowedException.class, () -> {
            usuario.setApellidos("");
        });
        
        assertThrows(NotNullValueAllowedException.class, () -> {
            usuario.setApellidos("  ");
        });
    }
    
    @Test
    @DisplayName("Test de validación de DNI/NIF nulo o vacío")
    public void testValidacionDniNifNuloOVacio() {
        assertThrows(NotNullValueAllowedException.class, () -> {
            usuario.setDniNif(null);
        });
        
        assertThrows(NotNullValueAllowedException.class, () -> {
            usuario.setDniNif("");
        });
        
        assertThrows(NotNullValueAllowedException.class, () -> {
            usuario.setDniNif("  ");
        });
    }
    
    @Test
    @DisplayName("Test de constructor con parámetros inválidos")
    public void testConstructorConParametrosInvalidos() {
        // Nombre nulo
        assertThrows(NotNullValueAllowedException.class, () -> {
            new Usuario(null, "Martínez Ruiz", "87654321B", TipoUsuario.PROFESOR);
        });
        
        // Apellidos nulos
        assertThrows(NotNullValueAllowedException.class, () -> {
            new Usuario("Pedro", null, "87654321B", TipoUsuario.PROFESOR);
        });
        
        // DNI/NIF nulo
        assertThrows(NotNullValueAllowedException.class, () -> {
            new Usuario("Pedro", "Martínez Ruiz", null, TipoUsuario.PROFESOR);
        });
        
        // Tipo de usuario nulo
        assertThrows(NullPointerException.class, () -> {
            new Usuario("Pedro", "Martínez Ruiz", "87654321B", null);
        });
    }
    
    @Test
    @DisplayName("Test de cambio de estado")
    public void testCambioEstado() {
        assertEquals(EstadoUsuario.ACTIVO, usuario.getEstado());
        
        usuario.setEstado(EstadoUsuario.INACTIVO);
        assertEquals(EstadoUsuario.INACTIVO, usuario.getEstado());
        
        usuario.setEstado(EstadoUsuario.BLOQUEADO);
        assertEquals(EstadoUsuario.BLOQUEADO, usuario.getEstado());
    }
}