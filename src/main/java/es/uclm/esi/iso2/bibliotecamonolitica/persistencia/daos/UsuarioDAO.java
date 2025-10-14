package es.uclm.esi.iso2.bibliotecamonolitica.persistencia.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import es.uclm.esi.iso2.bibliotecamonolitica.modelo.entidades.EstadoUsuario;
import es.uclm.esi.iso2.bibliotecamonolitica.modelo.entidades.TipoUsuario;
import es.uclm.esi.iso2.bibliotecamonolitica.modelo.entidades.Usuario;
import es.uclm.esi.iso2.bibliotecamonolitica.persistencia.agentes.Agent;
import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.DAOException;
import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.DatabaseConnectionException;
import es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones.NotNullValueAllowedException;
import es.uclm.esi.iso2.bibliotecamonolitica.util.helpers.LogHelper;

/**
 * Implementación del DAO para la entidad Usuario.
 * Gestiona todas las operaciones de persistencia relacionadas con usuarios.
 * 
 * @author Sistema de Gestión de Bibliotecas
 * @version 1.0
 */
public class UsuarioDAO implements GenericDAO<Usuario, Long> {
    
    private static final LogHelper logger = LogHelper.getLogger(UsuarioDAO.class);
    private final Agent dbAgent;
    
    /**
     * Constructor que inicializa el agente de base de datos.
     * 
     * @throws DAOException Si hay un error al inicializar el agente
     */
    public UsuarioDAO() throws DAOException {
        try {
            this.dbAgent = Agent.getInstance("mysql");
        } catch (DatabaseConnectionException e) {
            logger.error("Error al inicializar UsuarioDAO", e);
            throw new DAOException("No se pudo inicializar UsuarioDAO", e);
        }
    }
    
    @Override
    public Usuario create(Usuario usuario) throws DAOException {
        String sql = "INSERT INTO usuarios (nombre, apellidos, dni_nif, direccion, telefono, email, " +
                     "tipo_usuario, estado, fecha_alta) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            int id = dbAgent.executeUpdate(sql, 
                    usuario.getNombre(), 
                    usuario.getApellidos(),
                    usuario.getDniNif(), 
                    usuario.getDireccion(),
                    usuario.getTelefono(),
                    usuario.getEmail(),
                    usuario.getTipoUsuario().toString(),
                    usuario.getEstado().toString(),
                    new java.sql.Date(usuario.getFechaAlta().getTime()));
            
            usuario.setId((long) id);
            logger.info("Usuario creado con ID: {}", id);
            return usuario;
            
        } catch (SQLException | DatabaseConnectionException e) {
            logger.error("Error al crear usuario", e);
            throw new DAOException("No se pudo crear el usuario en la base de datos", e);
        }
    }
    
    @Override
    public Optional<Usuario> findById(Long id) throws DAOException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        
        try (ResultSet rs = dbAgent.executeQuery(sql, id)) {
            if (rs.next()) {
                Usuario usuario = mapResultSetToUsuario(rs);
                return Optional.of(usuario);
            } else {
                return Optional.empty();
            }
        } catch (SQLException | DatabaseConnectionException | NotNullValueAllowedException e) {
            logger.error("Error al buscar usuario por ID: {}", id, e);
            throw new DAOException("No se pudo recuperar el usuario con ID: " + id, e);
        }
    }
    
    @Override
    public Usuario update(Usuario usuario) throws DAOException {
        String sql = "UPDATE usuarios SET nombre = ?, apellidos = ?, dni_nif = ?, direccion = ?, " +
                     "telefono = ?, email = ?, tipo_usuario = ?, estado = ? WHERE id = ?";
        
        try {
            dbAgent.executeUpdate(sql, 
                    usuario.getNombre(), 
                    usuario.getApellidos(),
                    usuario.getDniNif(), 
                    usuario.getDireccion(),
                    usuario.getTelefono(),
                    usuario.getEmail(),
                    usuario.getTipoUsuario().toString(),
                    usuario.getEstado().toString(),
                    usuario.getId());
            
            logger.info("Usuario actualizado con ID: {}", usuario.getId());
            return usuario;
            
        } catch (SQLException | DatabaseConnectionException e) {
            logger.error("Error al actualizar usuario con ID: {}", usuario.getId(), e);
            throw new DAOException("No se pudo actualizar el usuario", e);
        }
    }
    
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try {
            int affectedRows = dbAgent.executeUpdate(sql, id);
            if (affectedRows == 0) {
                throw new DAOException("No se pudo eliminar el usuario, ID no encontrado: " + id);
            }
            logger.info("Usuario eliminado con ID: {}", id);
            
        } catch (SQLException | DatabaseConnectionException e) {
            logger.error("Error al eliminar usuario con ID: {}", id, e);
            throw new DAOException("No se pudo eliminar el usuario", e);
        }
    }
    
    @Override
    public List<Usuario> findAll() throws DAOException {
        String sql = "SELECT * FROM usuarios";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (ResultSet rs = dbAgent.executeQuery(sql)) {
            while (rs.next()) {
                Usuario usuario = mapResultSetToUsuario(rs);
                usuarios.add(usuario);
            }
            return usuarios;
            
        } catch (SQLException | DatabaseConnectionException | NotNullValueAllowedException e) {
            logger.error("Error al recuperar todos los usuarios", e);
            throw new DAOException("No se pudieron recuperar los usuarios", e);
        }
    }
    
    /**
     * Busca un usuario por su DNI/NIF.
     * 
     * @param dniNif DNI/NIF del usuario
     * @return Optional que contiene el usuario si existe
     * @throws DAOException Si ocurre un error en la operación
     */
    public Optional<Usuario> findByDniNif(String dniNif) throws DAOException {
        String sql = "SELECT * FROM usuarios WHERE dni_nif = ?";
        
        try (ResultSet rs = dbAgent.executeQuery(sql, dniNif)) {
            if (rs.next()) {
                Usuario usuario = mapResultSetToUsuario(rs);
                return Optional.of(usuario);
            } else {
                return Optional.empty();
            }
        } catch (SQLException | DatabaseConnectionException | NotNullValueAllowedException e) {
            logger.error("Error al buscar usuario por DNI/NIF: {}", dniNif, e);
            throw new DAOException("No se pudo buscar el usuario por DNI/NIF", e);
        }
    }
    
    /**
     * Busca usuarios por nombre y/o apellidos (búsqueda parcial).
     * 
     * @param texto Texto a buscar en nombre o apellidos
     * @return Lista de usuarios que coinciden
     * @throws DAOException Si ocurre un error en la operación
     */
    public List<Usuario> findByNombreOrApellidos(String texto) throws DAOException {
        String sql = "SELECT * FROM usuarios WHERE nombre LIKE ? OR apellidos LIKE ?";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (ResultSet rs = dbAgent.executeQuery(sql, "%" + texto + "%", "%" + texto + "%")) {
            while (rs.next()) {
                Usuario usuario = mapResultSetToUsuario(rs);
                usuarios.add(usuario);
            }
            return usuarios;
            
        } catch (SQLException | DatabaseConnectionException | NotNullValueAllowedException e) {
            logger.error("Error al buscar usuarios por nombre/apellidos: {}", texto, e);
            throw new DAOException("No se pudieron buscar los usuarios por nombre/apellidos", e);
        }
    }
    
    /**
     * Convierte un ResultSet en un objeto Usuario.
     * 
     * @param rs ResultSet con los datos del usuario
     * @return Objeto Usuario construido
     * @throws SQLException Si ocurre un error al acceder al ResultSet
     * @throws NotNullValueAllowedException Si algún valor obligatorio es nulo
     */
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException, NotNullValueAllowedException {
        Usuario usuario = new Usuario();
        
        usuario.setId(rs.getLong("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellidos(rs.getString("apellidos"));
        usuario.setDniNif(rs.getString("dni_nif"));
        usuario.setDireccion(rs.getString("direccion"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setEmail(rs.getString("email"));
        usuario.setTipoUsuario(TipoUsuario.valueOf(rs.getString("tipo_usuario")));
        usuario.setEstado(EstadoUsuario.valueOf(rs.getString("estado")));
        usuario.setFechaAlta(new Date(rs.getDate("fecha_alta").getTime()));
        
        return usuario;
    }
}