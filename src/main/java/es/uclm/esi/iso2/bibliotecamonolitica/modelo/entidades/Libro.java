package es.uclm.esi.iso2.bibliotecamonolitica.modelo.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * Entidad que representa un libro en el catálogo de la biblioteca.
 */
@Entity
@Table(name = "libros")
public class Libro implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 1, max = 255, message = "El título debe tener entre 1 y 255 caracteres")
    @Column(name = "titulo", nullable = false)
    private String titulo;
    
    @NotBlank(message = "El autor es obligatorio")
    @Column(name = "autor", nullable = false)
    private String autor;
    
    @Column(name = "editorial")
    private String editorial;
    
    @Column(name = "anio_publicacion")
    private Integer anioPublicacion;
    
    @Pattern(regexp = "^(?:ISBN(?:-13)?:?\\s)?(?=[0-9X]{13}$|(?=(?:[0-9]+[-\\s]){4})[-\\s0-9X]{17}$)97[89][-\\s]?[0-9]{1,5}[-\\s]?[0-9]+[-\\s]?[0-9]+[-\\s]?[0-9X]$", 
            message = "El formato del ISBN no es válido")
    @Column(name = "isbn", unique = true)
    private String isbn;
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "fecha_alta")
    @Temporal(TemporalType.DATE)
    private Date fechaAlta;
    
    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ejemplar> ejemplares = new ArrayList<>();

    // Constructores
    public Libro() {
        // Constructor por defecto necesario para JPA
        this.fechaAlta = new Date();
    }
    
    public Libro(String titulo, String autor) {
        this();
        this.titulo = titulo;
        this.autor = autor;
    }

    // Getters y setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public Integer getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(Integer anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public List<Ejemplar> getEjemplares() {
        return ejemplares;
    }

    public void setEjemplares(List<Ejemplar> ejemplares) {
        this.ejemplares = ejemplares;
    }
    
    // Métodos de negocio
    
    /**
     * Añade un nuevo ejemplar al libro
     * @param ubicacion Ubicación física del ejemplar
     * @return El ejemplar creado
     */
    public Ejemplar agregarEjemplar(String ubicacion) {
        Ejemplar ejemplar = new Ejemplar();
        ejemplar.setLibro(this);
        ejemplar.setEstado(EstadoEjemplar.DISPONIBLE);
        ejemplar.setUbicacion(ubicacion);
        ejemplar.setFechaAdquisicion(new Date());
        ejemplar.setCodigoBarras(generarCodigoBarras());
        this.ejemplares.add(ejemplar);
        return ejemplar;
    }
    
    /**
     * Elimina un ejemplar del libro
     * @param ejemplar El ejemplar a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarEjemplar(Ejemplar ejemplar) {
        return this.ejemplares.remove(ejemplar);
    }
    
    /**
     * Cuenta cuántos ejemplares disponibles tiene el libro
     * @return número de ejemplares disponibles
     */
    public int contarEjemplaresDisponibles() {
        return (int) this.ejemplares.stream()
                .filter(e -> e.getEstado() == EstadoEjemplar.DISPONIBLE)
                .count();
    }
    
    private String generarCodigoBarras() {
        // Lógica para generar un código de barras único
        return "LIB-" + System.currentTimeMillis() + "-" + (ejemplares.size() + 1);
    }
    
    @Override
    public String toString() {
        return "Libro [id=" + id + ", titulo=" + titulo + ", autor=" + autor + ", isbn=" + isbn + "]";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Libro libro = (Libro) obj;
        
        if (isbn != null) {
            return isbn.equals(libro.isbn);
        }
        return id != null && id.equals(libro.id);
    }
    
    @Override
    public int hashCode() {
        return isbn != null ? isbn.hashCode() : (id != null ? id.hashCode() : 0);
    }
}