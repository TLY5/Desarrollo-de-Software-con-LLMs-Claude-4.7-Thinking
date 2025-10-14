package es.uclm.esi.iso2.bibliotecamonolitica.persistencia.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import es.uclm.esi.iso2.bibliotecamonolitica.modelo.entidades.Categoria;
import es.uclm.esi.iso2.bibliotecamonolitica.modelo.entidades.Libro;
import es.uclm.esi.iso2.bibliotecamonolitica.persistencia.agentes.Agent;
import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.DAOException;
import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.DatabaseConnectionException;
import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.NotNullValueAllowedException;
import es.uclm.esi.iso2.bibliotecamonolitica.util.helpers.LogHelper;

/**
 * Implementación del DAO para la entidad Libro.
 * Gestiona todas las operaciones de persistencia relacionadas con libros.
 * 
 * @author Sistema de Gestión de Bibliotecas
 * @version 1.0
 */
public class LibroDAO implements GenericDAO<Libro, Long> {
    
    private static final LogHelper logger = LogHelper.getLogger(LibroDAO.class);
    private final Agent dbAgent;
    private final CategoriaDAO categoriaDAO;
    
    /**
     * Constructor que inicializa el agente de base de datos.
     * 
     * @throws DAOException Si hay un error al inicializar el agente
     */
    public LibroDAO() throws DAOException {
        try {
            this.dbAgent = Agent.getInstance("mysql");
            this.categoriaDAO = new CategoriaDAO();
        } catch (DatabaseConnectionException e) {
            logger.error("Error al inicializar LibroDAO", e);
            throw new DAOException("No se pudo inicializar LibroDAO", e);
        }
    }
    
    @Override
    public Libro create(Libro libro) throws DAOException {
        String sql = "INSERT INTO libros (titulo, autor, editorial, anio_publicacion, isbn, " +
                     "categoria_id, descripcion, fecha_alta) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            Long categoriaId = null;
            if (libro.getCategoria() != null) {
                categoriaId = libro.getCategoria().getId();
            }
            
            int id = dbAgent.executeUpdate(sql, 
                    libro.getTitulo(), 
                    libro.getAutor(),
                    libro.getEditorial(), 
                    libro.getAnioPublicacion(),
                    libro.getIsbn(),
                    categoriaId,
                    libro.getDescripcion(),
                    new java.sql.Date(libro.getFechaAlta().getTime()));
            
            libro.setId((long) id);
            logger.info("Libro creado con ID: {}", id);
            return libro;
            
        } catch (SQLException | DatabaseConnectionException e) {
            logger.error("Error al crear libro", e);
            throw new DAOException("No se pudo crear el libro en la base de datos", e);
        }
    }
    
    @Override
    public Optional<Libro> findById(Long id) throws DAOException {
        String sql = "SELECT * FROM libros WHERE id = ?";
        
        try (ResultSet rs = dbAgent.executeQuery(sql, id)) {
            if (rs.next()) {
                Libro libro = mapResultSetToLibro(rs);
                return Optional.of(libro);
            } else {
                return Optional.empty();
            }
        } catch (SQLException | DatabaseConnectionException | NotNullValueAllowedException e) {
            logger.error("Error al buscar libro por ID: {}", id, e);
            throw new DAOException("No se pudo recuperar el libro con ID: " + id, e);
        }
    }
    
    @Override
    public Libro update(Libro libro) throws DAOException {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, editorial = ?, anio_publicacion = ?, " +
                     "isbn = ?, categoria_id = ?, descripcion = ? WHERE id = ?";
        
        try {
            Long categoriaId = null;
            if (libro.getCategoria() != null) {
                categoriaId = libro.getCategoria().getId();
            }
            
            dbAgent.executeUpdate(sql, 
                    libro.getTitulo(), 
                    libro.getAutor(),
                    libro.getEditorial(), 
                    libro.getAnioPublicacion(),
                    libro.getIsbn(),
                    categoriaId,
                    libro.getDescripcion(),
                    libro.getId());
            
            logger.info("Libro actualizado con ID: {}", libro.getId());
            return libro;
            
        } catch (SQLException | DatabaseConnectionException e) {
            logger.error("Error al actualizar libro con ID: {}", libro.getId(), e);
            throw new DAOException("No se pudo actualizar el libro", e);
        }
    }
    
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM libros WHERE id = ?";
        
        try {
            int affectedRows = dbAgent.executeUpdate(sql, id);
            if (affectedRows == 0) {
                throw new DAOException("No se pudo eliminar el libro, ID no encontrado: " + id);
            }
            logger.info("Libro eliminado con ID: {}", id);
            
        } catch (SQLException | DatabaseConnectionException e) {
            logger.error("Error al eliminar libro con ID: {}", id, e);
            throw new DAOException("No se pudo eliminar el libro", e);
        }
    }
    
    @Override
    public List<Libro> findAll() throws DAOException {
        String sql = "SELECT * FROM libros";
        List<Libro> libros = new ArrayList<>();
        
        try (ResultSet rs = dbAgent.executeQuery(sql)) {
            while (rs.next()) {
                Libro libro = mapResultSetToLibro(rs);
                libros.add(libro);
            }
            return libros;
            
        } catch (SQLException | DatabaseConnectionException | NotNullValueAllowedException e) {
            logger.error("Error al recuperar todos los libros", e);
            throw new DAOException("No se pudieron recuperar los libros", e);
        }
    }
    
    /**
     * Busca libros por título (búsqueda parcial).
     * 
     * @param titulo Texto a buscar en el título
     * @return Lista de libros que coinciden
     * @throws DAOException Si ocurre un error en la operación
     */
    public List<Libro> findByTitulo(String titulo) throws DAOException {
        String sql = "SELECT * FROM libros WHERE titulo LIKE ?";
        List<Libro> libros = new ArrayList<>();
        
        try (ResultSet rs = dbAgent.executeQuery(sql, "%" + titulo + "%")) {
            while (rs.next()) {
                Libro libro = mapResultSetToLibro(rs);
                libros.add(libro);
            }
            return libros;
            
        } catch (SQLException | DatabaseConnectionException | NotNullValueAllowedException e) {
            logger.error("Error al buscar libros por título: {}", titulo, e);
            throw new DAOException("No se pudieron buscar los libros por título", e);
        }
    }
    
    /**
     * Busca un libro por su ISBN.
     * 
     * @param isbn ISBN del libro
     * @return Optional que contiene el libro si existe
     * @throws DAOException Si ocurre un error en la operación
     */
    public Optional<Libro> findByIsbn(String isbn) throws DAOException {
        String sql = "SELECT * FROM libros WHERE isbn = ?";
        
        try (ResultSet rs = dbAgent.executeQuery(sql, isbn)) {
            if (rs.next()) {
                Libro libro = mapResultSetToLibro(rs);
                return Optional.of(libro);
            } else {
                return Optional.empty();
            }
        } catch (SQLException | DatabaseConnectionException | NotNullValueAllowedException e) {
            logger.error("Error al buscar libro por ISBN: {}", isbn, e);
            throw new DAOException("No se pudo buscar el libro por ISBN", e);
        }
    }
    
    /**
     * Busca libros por categoría.
     * 
     * @param categoriaId ID de la categoría
     * @return Lista de libros que pertenecen a la categoría
     * @throws DAOException Si ocurre un error en la operación
     */
    public List<Libro> findByCategoria(Long categoriaId) throws DAOException {
        String sql = "SELECT * FROM libros WHERE categoria_id = ?";
        List<Libro> libros = new ArrayList<>();
        
        try (ResultSet rs = dbAgent.executeQuery(sql, categoriaId)) {
            while (rs.next()) {
                Libro libro = mapResultSetToLibro(rs);
                libros.add(libro);
            }
            return libros;
            
        } catch (SQLException | DatabaseConnectionException | NotNullValueAllowedException e) {
            logger.error("Error al buscar libros por categoría: {}", categoriaId, e);
            throw new DAOException("No se pudieron buscar los libros por categoría", e);
        }
    }
    
    /**
     * Convierte un ResultSet en un objeto Libro.
     * 
     * @param rs ResultSet con los datos del libro
     * @return Objeto Libro construido
     * @throws SQLException Si ocurre un error al acceder al ResultSet
     * @throws DAOException Si ocurre un error al recuperar la categoría
     * @throws NotNullValueAllowedException Si algún valor obligatorio es nulo
     */
    private Libro mapResultSetToLibro(ResultSet rs) throws SQLException, DAOException, NotNullValueAllowedException {
        Libro libro = new Libro();
        
        libro.setId(rs.getLong("id"));
        libro.setTitulo(rs.getString("titulo"));
        libro.setAutor(rs.getString("autor"));
        libro.setEditorial(rs.getString("editorial"));
        libro.setAnioPublicacion(rs.getInt("anio_publicacion"));
        libro.setIsbn(rs.getString("isbn"));
        libro.setDescripcion(rs.getString("descripcion"));
        libro.setFechaAlta(new Date(rs.getDate("fecha_alta").getTime()));
        
        // Recuperar la categoría si existe
        Long categoriaId = rs.getLong("categoria_id");
        if (!rs.wasNull()) {
            Optional<Categoria> categoria = categoriaDAO.findById(categoriaId);
            categoria.ifPresent(libro::setCategoria);
        }
        
        return libro;
    }
}