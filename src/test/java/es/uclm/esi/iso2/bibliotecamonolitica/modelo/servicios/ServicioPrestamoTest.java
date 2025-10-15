package es.uclm.esi.iso2.bibliotecamonolitica.modelo.servicios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.uclm.esi.iso2.bibliotecamonolitica.modelo.entidades.*;
import es.uclm.esi.iso2.bibliotecamonolitica.persistencia.daos.*;
import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.*;

/**
 * Pruebas unitarias para la clase ServicioPrestamo.
 */
@ExtendWith(MockitoExtension.class)
public class ServicioPrestamoTest {
    
    @Mock
    private PrestamoDAO mockPrestamoDAO;
    
    @Mock
    private DetallePrestamDAO mockDetallePrestamDAO;
    
    @Mock
    private UsuarioDAO mockUsuarioDAO;
    
    @Mock
    private EjemplarDAO mockEjemplarDAO;
    
    @Mock
    private MultaDAO mockMultaDAO;
    
    @InjectMocks
    private ServicioPrestamo servicioPrestamo;
    
    private Usuario usuario;
    private Libro libro;
    private Ejemplar ejemplar1, ejemplar2;
    private List<Long> ejemplarIds;
    private Prestamo prestamo;
    private DetallePrestamo detallePrestamo;
    
    @BeforeEach
    public void setUp() throws NotNullValueAllowedException {
        // Configurar objetos de prueba
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellidos("García López");
        usuario.setDniNif("12345678A");
        usuario.setTipoUsuario(TipoUsuario.ESTUDIANTE);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        
        libro = new Libro();
        libro.setId(1L);
        libro.setTitulo("Don Quijote de la Mancha");
        libro.setAutor("Miguel de Cervantes");
        
        ejemplar1 = new Ejemplar();
        ejemplar1.setId(1L);
        ejemplar1.setLibro(libro);
        ejemplar1.setEstado(EstadoEjemplar.DISPONIBLE);
        ejemplar1.setCodigoBarras("EJM-001");
        
        ejemplar2 = new Ejemplar();
        ejemplar2.setId(2L);
        ejemplar2.setLibro(libro);
        ejemplar2.setEstado(EstadoEjemplar.DISPONIBLE);
        ejemplar2.setCodigoBarras("EJM-002");
        
        ejemplarIds = Arrays.asList(1L, 2L);
        
        prestamo = new Prestamo();
        prestamo.setId(1L);
        prestamo.setUsuario(usuario);
        prestamo.setFecha(new Date());
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 15); // 15 días para devolución
        prestamo.setFechaDevolucionPrevista(cal.getTime());
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        
        detallePrestamo = new DetallePrestamo();
        detallePrestamo.setId(1L);
        detallePrestamo.setPrestamo(prestamo);
        detallePrestamo.setEjemplar(ejemplar1);
        detallePrestamo.setEstado(EstadoDetallePrestamo.PRESTADO);
    }
    
    @Test
    public void testRealizarPrestamo() throws Exception {
        // Configurar comportamiento de los mocks
        when(mockUsuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(mockEjemplarDAO.findById(1L)).thenReturn(Optional.of(ejemplar1));
        when(mockEjemplarDAO.findById(2L)).thenReturn(Optional.of(ejemplar2));
        when(mockMultaDAO.existeMultaActivaUsuario(1L)).thenReturn(false);
        when(mockPrestamoDAO.contarPrestamosActivosUsuario(1L)).thenReturn(0);
        when(mockPrestamoDAO.create(any(Prestamo.class))).thenAnswer(inv -> {
            Prestamo p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        
        // Ejecutar método
        Prestamo resultado = servicioPrestamo.realizarPrestamo(1L, ejemplarIds);
        
        // Verificar comportamiento y resultado
        verify(mockUsuarioDAO).findById(1L);
        verify(mockEjemplarDAO).findById(1L);
        verify(mockEjemplarDAO).findById(2L);
        verify(mockMultaDAO).existeMultaActivaUsuario(1L);
        verify(mockPrestamoDAO).contarPrestamosActivosUsuario(1L);
        verify(mockPrestamoDAO).create(any(Prestamo.class));
        verify(mockDetallePrestamDAO, times(2)).create(any(DetallePrestamo.class));
        verify(mockEjemplarDAO, times(2)).update(any(Ejemplar.class));
        
        assertNotNull(resultado);
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(EstadoPrestamo.ACTIVO, resultado.getEstado());
        assertNotNull(resultado.getFecha());
        assertNotNull(resultado.getFechaDevolucionPrevista());
    }
    
    @Test
    public void testRealizarPrestamoUsuarioNoEncontrado() {
        // Configurar comportamiento de los mocks
        when(mockUsuarioDAO.findById(999L)).thenReturn(Optional.empty());
        
        // Verificar que se lanza excepción
        assertThrows(PrestamoException.class, () -> {
            servicioPrestamo.realizarPrestamo(999L, ejemplarIds);
        });
    }
    
    @Test
    public void testRealizarPrestamoUsuarioInactivo() throws Exception {
        // Configurar usuario inactivo
        usuario.setEstado(EstadoUsuario.INACTIVO);
        
        // Configurar comportamiento de los mocks
        when(mockUsuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        
        // Verificar que se lanza excepción
        assertThrows(PrestamoException.class, () -> {
            servicioPrestamo.realizarPrestamo(1L, ejemplarIds);
        });
    }
    
    @Test
    public void testRealizarPrestamoConMultaActiva() throws Exception {
        // Configurar comportamiento de los mocks
        when(mockUsuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(mockMultaDAO.existeMultaActivaUsuario(1L)).thenReturn(true);
        
        // Verificar que se lanza excepción
        assertThrows(PrestamoException.class, () -> {
            servicioPrestamo.realizarPrestamo(1L, ejemplarIds);
        });
    }
    
    @Test
    public void testRealizarPrestamoExcedeLimite() throws Exception {
        // Configurar comportamiento de los mocks
        when(mockUsuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(mockMultaDAO.existeMultaActivaUsuario(1L)).thenReturn(false);
        when(mockPrestamoDAO.contarPrestamosActivosUsuario(1L)).thenReturn(5); // Ya tiene el máximo de préstamos
        
        // Verificar que se lanza excepción
        assertThrows(PrestamoException.class, () -> {
            servicioPrestamo.realizarPrestamo(1L, ejemplarIds);
        });
    }
    
    @Test
    public void testRealizarPrestamoEjemplarNoDisponible() throws Exception {
        // Configurar ejemplar no disponible
        ejemplar1.setEstado(EstadoEjemplar.PRESTADO);
        
        // Configurar comportamiento de los mocks
        when(mockUsuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(mockEjemplarDAO.findById(1L)).thenReturn(Optional.of(ejemplar1));
        when(mockMultaDAO.existeMultaActivaUsuario(1L)).thenReturn(false);
        when(mockPrestamoDAO.contarPrestamosActivosUsuario(1L)).thenReturn(0);
        
        // Verificar que se lanza excepción
        assertThrows(PrestamoException.class, () -> {
            servicioPrestamo.realizarPrestamo(1L, Arrays.asList(1L));
        });
    }
    
    @Test
    public void testRenovarPrestamo() throws Exception {
        // Configurar préstamo con fecha próxima
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2); // 2 días para vencimiento
        prestamo.setFechaDevolucionPrevista(cal.getTime());
        
        List<DetallePrestamo> detalles = new ArrayList<>();
        detalles.add(detallePrestamo);
        prestamo.setDetalles(detalles);
        
        // Configurar comportamiento de los mocks
        when(mockPrestamoDAO.findById(1L)).thenReturn(Optional.of(prestamo));
        when(mockPrestamoDAO.contarRenovacionesPrestamo(1L)).thenReturn(0);
        when(mockPrestamoDAO.update(any(Prestamo.class))).thenReturn(prestamo);
        
        // Ejecutar método
        Prestamo resultado = servicioPrestamo.renovarPrestamo(1L);
        
        // Verificar comportamiento y resultado
        verify(mockPrestamoDAO).findById(1L);
        verify(mockPrestamoDAO).contarRenovacionesPrestamo(1L);
        verify(mockPrestamoDAO).update(prestamo);
        
        assertNotNull(resultado);
        // Verificar que la fecha se extendió aproximadamente 15 días
        Calendar calOriginal = Calendar.getInstance();
        calOriginal.setTime(cal.getTime());
        calOriginal.add(Calendar.DAY_OF_MONTH, 15);
        
        Calendar calResultado = Calendar.getInstance();
        calResultado.setTime(resultado.getFechaDevolucionPrevista());
        
        // Permitir un pequeño margen de diferencia por la ejecución
        long diffMillis = Math.abs(calOriginal.getTimeInMillis() - calResultado.getTimeInMillis());
        assertTrue(diffMillis < 1000 * 60); // Diferencia menor a un minuto
    }
    
    @Test
    public void testRenovarPrestamoNoEncontrado() {
        // Configurar comportamiento de los mocks
        when(mockPrestamoDAO.findById(999L)).thenReturn(Optional.empty());
        
        // Verificar que se lanza excepción
        assertThrows(PrestamoException.class, () -> {
            servicioPrestamo.renovarPrestamo(999L);
        });
    }
    
    @Test
    public void testRenovarPrestamoNoActivo() throws Exception {
        // Configurar préstamo no activo
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        
        // Configurar comportamiento de los mocks
        when(mockPrestamoDAO.findById(1L)).thenReturn(Optional.of(prestamo));
        
        // Verificar que se lanza excepción
        assertThrows(PrestamoException.class, () -> {
            servicioPrestamo.renovarPrestamo(1L);
        });
    }
    
    @Test
    public void testRenovarPrestamoExcedeLimiteRenovaciones() throws Exception {
        // Configurar comportamiento de los mocks
        when(mockPrestamoDAO.findById(1L)).thenReturn(Optional.of(prestamo));
        when(mockPrestamoDAO.contarRenovacionesPrestamo(1L)).thenReturn(2); // Ya ha renovado el máximo
        
        // Verificar que se lanza excepción
        assertThrows(PrestamoException.class, () -> {
            servicioPrestamo.renovarPrestamo(1L);
        });
    }
    
    @Test
    public void testRegistrarDevolucion() throws Exception {
        // Configurar préstamo con detalle
        List<DetallePrestamo> detalles = new ArrayList<>();
        detalles.add(detallePrestamo);
        prestamo.setDetalles(detalles);
        
        // Configurar comportamiento de los mocks
        when(mockPrestamoDAO.findById(1L)).thenReturn(Optional.of(prestamo));
        when(mockEjemplarDAO.update(any(Ejemplar.class))).thenReturn(ejemplar1);
        when(mockDetallePrestamDAO.update(any(DetallePrestamo.class))).thenReturn(detallePrestamo);
        when(mockPrestamoDAO.update(any(Prestamo.class))).thenReturn(prestamo);
        
        // Fecha de devolución no vencida (sin multa)
        Date fechaDevolucion = new Date();
        
        // Ejecutar método
        Prestamo resultado = servicioPrestamo.registrarDevolucion(1L, fechaDevolucion);
        
        // Verificar comportamiento y resultado
        verify(mockPrestamoDAO).findById(1L);
        verify(mockEjemplarDAO).update(ejemplar1);
        verify(mockDetallePrestamDAO).update(detallePrestamo);
        verify(mockPrestamoDAO).update(prestamo);
        verify(mockMultaDAO, never()).create(any(Multa.class)); // No debe crear multa
        
        assertNotNull(resultado);
        assertEquals(EstadoPrestamo.DEVUELTO, resultado.getEstado());
        assertEquals(EstadoEjemplar.DISPONIBLE, ejemplar1.getEstado());
        assertEquals(EstadoDetallePrestamo.DEVUELTO, detallePrestamo.getEstado());
        assertEquals(fechaDevolucion, detallePrestamo.getFechaDevolucionReal());
    }
    
    @Test
    public void testRegistrarDevolucionConRetraso() throws Exception {
        // Configurar préstamo con detalle y fecha vencida
        List<DetallePrestamo> detalles = new ArrayList<>();
        detalles.add(detallePrestamo);
        prestamo.setDetalles(detalles);
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -5); // 5 días de retraso
        prestamo.setFechaDevolucionPrevista(cal.getTime());
        
        // Configurar comportamiento de los mocks
        when(mockPrestamoDAO.findById(1L)).thenReturn(Optional.of(prestamo));
        when(mockEjemplarDAO.update(any(Ejemplar.class))).thenReturn(ejemplar1);
        when(mockDetallePrestamDAO.update(any(DetallePrestamo.class))).thenReturn(detallePrestamo);
        when(mockPrestamoDAO.update(any(Prestamo.class))).thenReturn(prestamo);
        when(mockMultaDAO.create(any(Multa.class))).thenAnswer(inv -> inv.getArgument(0));
        
        // Fecha de devolución actual
        Date fechaDevolucion = new Date();
        
        // Ejecutar método
        Prestamo resultado = servicioPrestamo.registrarDevolucion(1L, fechaDevolucion);
        
        // Verificar comportamiento y resultado
        verify(mockPrestamoDAO).findById(1L);
        verify(mockEjemplarDAO).update(ejemplar1);
        verify(mockDetallePrestamDAO).update(detallePrestamo);
        verify(mockPrestamoDAO).update(prestamo);
        verify(mockMultaDAO).create(any(Multa.class)); // Debe crear multa por retraso
        
        assertNotNull(resultado);
        assertEquals(EstadoPrestamo.DEVUELTO, resultado.getEstado());
    }
    
    @Test
    public void testRegistrarDevolucionPrestamoNoEncontrado() {
        // Configurar comportamiento de los mocks
        when(mockPrestamoDAO.findById(999L)).thenReturn(Optional.empty());
        
        // Verificar que se lanza excepción
        assertThrows(PrestamoException.class, () -> {
            servicioPrestamo.registrarDevolucion(999L, new Date());
        });
    }
    
    @Test
    public void testRegistrarDevolucionPrestamoYaDevuelto() throws Exception {
        // Configurar préstamo ya devuelto
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        
        // Configurar comportamiento de los mocks
        when(mockPrestamoDAO.findById(1L)).thenReturn(Optional.of(prestamo));
        
        // Verificar que se lanza excepción
        assertThrows(PrestamoException.class, () -> {
            servicioPrestamo.registrarDevolucion(1L, new Date());
        });
    }
}