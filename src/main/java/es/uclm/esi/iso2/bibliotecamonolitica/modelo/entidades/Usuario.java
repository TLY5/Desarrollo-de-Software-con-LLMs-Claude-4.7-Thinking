package es.uclm.esi.iso2.bibliotecamonolitica.modelo.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.*;

import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.NotNullValueAllowedException;

/**
 * Entidad que representa a un usuario de la biblioteca.
 * 
 * @author Sistema de Gestión de Bibliotecas
 * @version 1.0
 */
@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {
    
    /** Serial version UID */
    private static final long serialVersionUID = 1L;
    
    /** Identificador único del usuario */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Nombre del usuario */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    /** Apellidos del usuario */
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    @Column(name = "apellidos", nullable = false)
    private String apellidos;
    
    /** DNI/NIF del usuario */
    @NotBlank(message = "El DNI/NIF es obligatorio")
    @Pattern(regexp = "[0-9]{8}[A-Z]", message = "El formato del DNI/NIF no es válido")
    @Column(name = "dni_nif", nullable = false, unique = true)
    private String dniNif;
    
    /** Dirección del usuario */
    @Column(name = "direccion")
    private String direccion;
    
    /** Teléfono del usuario */
    @Pattern(regexp = "[0-9]{9}", message = "El formato del teléfono no es válido")
    @Column(name = "telefono")
    private String telefono;
    
    /** Email del usuario */
    @Email(message = "El formato del email no es válido")
    @Column(name = "email")
    private String email;
    
    /** Tipo de usuario (ESTUDIANTE, PROFESOR, PERSONAL, etc.) */
    @NotNull(message = "El tipo de usuario es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario;
    
    /** Estado del usuario (ACTIVO, INACTIVO, BLOQUEADO) */
    @NotNull(message = "El estado del usuario es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoUsuario estado;
    
    /** Fecha de alta del usuario */
    @Column(name = "fecha_alta", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaAlta;
    
    /** Lista de préstamos realizados por el usuario */
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Prestamo> prestamos = new ArrayList<>();
    
    /** Lista de multas asociadas al usuario */
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Multa> multas = new ArrayList<>();
    
    /**
     * Constructor por defecto necesario para JPA
     */
    public Usuario() {
        this.fechaAlta = new Date();
        this.estado = EstadoUsuario.ACTIVO;
    }
    
    /**
     * Constructor con parámetros básicos
     * 
     * @param nombre Nombre del usuario
     * @param apellidos Apellidos del usuario
     * @param dniNif DNI/NIF del usuario
     * @param tipoUsuario Tipo de usuario
     * @throws NotNullValueAllowedException si algún valor String es null o vacío
     */
    public Usuario(String nombre, String apellidos, String dniNif, TipoUsuario tipoUsuario) 
            throws NotNullValueAllowedException {
        this();
        this.setNombre(nombre);
        this.setApellidos(apellidos);
        this.setDniNif(dniNif);
        this.setTipoUsuario(tipoUsuario);
    }
    
    /**
     * Obtiene el ID del usuario
     * 
     * @return ID del usuario
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Establece el ID del usuario
     * 
     * @param id ID a establecer
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Obtiene el nombre del usuario
     * 
     * @return Nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Establece el nombre del usuario
     * 
     * @param nombre Nombre a establecer
     * @throws NotNullValueAllowedException si el nombre es null o vacío
     */
    public void setNombre(String nombre) throws NotNullValueAllowedException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new NotNullValueAllowedException("El nombre no puede ser nulo o vacío");
        }
        this.nombre = nombre;
    }
    
    /**
     * Obtiene los apellidos del usuario
     * 
     * @return Apellidos del usuario
     */
    public String getApellidos() {
        return apellidos;
    }
    
    /**
     * Establece los apellidos del usuario
     * 
     * @param apellidos Apellidos a establecer
     * @throws NotNullValueAllowedException si los apellidos son null o vacíos
     */
    public void setApellidos(String apellidos) throws NotNullValueAllowedException {
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new NotNullValueAllowedException("Los apellidos no pueden ser nulos o vacíos");
        }
        this.apellidos = apellidos;
    }
    
    /**
     * Obtiene el DNI/NIF del usuario
     * 
     * @return DNI/NIF del usuario
     */
    public String getDniNif() {
        return dniNif;
    }
    
    /**
     * Establece el DNI/NIF del usuario
     * 
     * @param dniNif DNI/NIF a establecer
     * @throws NotNullValueAllowedException si el DNI/NIF es null o vacío
     */
    public void setDniNif(String dniNif) throws NotNullValueAllowedException {
        if (dniNif == null || dniNif.trim().isEmpty()) {
            throw new NotNullValueAllowedException("El DNI/NIF no puede ser nulo o vacío");
        }
        this.dniNif = dniNif;
    }
    
    /**
     * Obtiene la dirección del usuario
     * 
     * @return Dirección del usuario
     */
    public String getDireccion() {
        return direccion;
    }
    
    /**
     * Establece la dirección del usuario
     * 
     * @param direccion Dirección a establecer
     * @throws NotNullValueAllowedException si la dirección es null o vacía
     */
    public void setDireccion(String direccion) throws NotNullValueAllowedException {
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new NotNullValueAllowedException("La dirección no puede ser nula o vacía");
        }
        this.direccion = direccion;
    }

    // Resto de getters y setters siguiendo el mismo patrón
}