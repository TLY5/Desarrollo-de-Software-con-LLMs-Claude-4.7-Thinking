package es.uclm.esi.iso2.bibliotecamonolitica;

import es.uclm.esi.iso2.bibliotecamonolitica.config.app.ConfigManager;
import es.uclm.esi.iso2.bibliotecamonolitica.vista.ui.VistaLogin;
import es.uclm.esi.iso2.bibliotecamonolitica.util.helpers.LogHelper;

/**
 * Punto de entrada principal para la aplicación de gestión de biblioteca.
 */
public class App {
    private static final LogHelper logger = LogHelper.getLogger(App.class);

    public static void main(String[] args) {
        try {
            logger.info("Iniciando aplicación de gestión de biblioteca...");
            
            // Cargar configuraciones
            ConfigManager.getInstance().cargarConfiguracion();
            
            logger.info("Configuración cargada correctamente");
            
            // Iniciar interfaz de usuario
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    VistaLogin login = new VistaLogin();
                    login.setVisible(true);
                } catch (Exception e) {
                    logger.error("Error al iniciar interfaz de usuario", e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error fatal durante el inicio de la aplicación", e);
            System.exit(1);
        }
    }
}