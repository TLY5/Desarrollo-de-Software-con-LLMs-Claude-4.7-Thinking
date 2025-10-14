package es.uclm.esi.iso2.bibliotecamonolitica.util.excepciones;

/**
 * Excepción lanzada cuando se intenta asignar un valor nulo o vacío a un atributo
 * que no permite dichos valores.
 * 
 * @author Sistema de Gestión de Bibliotecas
 * @version 1.0
 */
public class NotNullValueAllowedException extends Exception {
    
    /** Serial version UID */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor que recibe el mensaje de error
     * 
     * @param message Mensaje que describe la razón de la excepción
     */
    public NotNullValueAllowedException(String message) {
        super(message);
    }
    
    /**
     * Constructor que recibe el mensaje de error y la causa original
     * 
     * @param message Mensaje que describe la razón de la excepción
     * @param cause Causa original de la excepción
     */
    public NotNullValueAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}