package es.uclm.esi.iso2.bibliotecamonolitica.modelo.interfaces;

import es.uclm.esi.iso2.bibliotecamonolitica.modelo.entidades.Libro;
import es.uclm.esi.iso2.bibliotecamonolitica.modelo.entidades.Categoria;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de persistencia para la entidad Libro.
 */
public interface IRepositorioLibro {
    
    /**
     * Guarda un libro en la base de datos
     * @param libro El libro a guardar
     * @return El libro guardado con su ID asignado
     */
    Libro guardar(Libro libro);
    
    /**
     * Actualiza un libro existente
     * @param libro El libro con los datos actualizados
     * @return El libro actualizado
     */
    Libro actualizar(Libro libro);
    
    /**
     * Elimina un libro del sistema
     * @param id ID del libro a eliminar
     */
    void eliminar(Long id);
    
    /**
     * Busca un libro por su ID
     * @param id ID del libro
     * @return Optional conteniendo el libro o vacío si no existe
     */
    Optional<Libro> buscarPorId(Long id);
    
    /**
     * Busca un libro por su ISBN
     * @param isbn ISBN del libro
     * @return Optional conteniendo el libro o vacío si no existe
     */
    Optional<Libro> buscarPorIsbn(String isbn);
    
    /**
     * Busca libros por título (búsqueda parcial)
     * @param titulo Texto a buscar en el título
     * @return Lista de libros que coinciden
     */
    List<Libro> buscarPorTitulo(String titulo);
    
    /**
     * Busca libros por autor (búsqueda parcial)
     * @param autor Nombre del autor a buscar
     * @return Lista de libros que coinciden
     */
    List<Libro> buscarPorAutor(String autor);
    
    /**
     * Busca libros por categoría
     * @param categoria La categoría a buscar
     * @return Lista de libros de esa categoría
     */
    List<Libro> buscarPorCategoria(Categoria categoria);
    
    /**
     * Obtiene todos los libros del sistema
     * @return Lista completa de libros
     */
    List<Libro> buscarTodos();
    
    /**
     * Busca libros con criterios avanzados
     * @param titulo Título parcial (opcional)
     * @param autor Autor parcial (opcional)
     * @param editorial Editorial parcial (opcional)
     * @param anioPublicacion Año de publicación (opcional)
     * @param categoriaId ID de categoría (opcional)
     * @return Lista de libros que coinciden con los criterios
     */
    List<Libro> busquedaAvanzada(String titulo, String autor, String editorial, 
            Integer anioPublicacion, Long categoriaId);
    
    /**
     * Cuenta el número total de libros en el sistema
     * @return Número total de libros
     */
    long contarTotal();
    
    /**
     * Verifica si un libro tiene ejemplares prestados
     * @param id ID del libro
     * @return true si hay ejemplares prestados, false en caso contrario
     */
    boolean tieneEjemplaresPrestados(Long id);
}