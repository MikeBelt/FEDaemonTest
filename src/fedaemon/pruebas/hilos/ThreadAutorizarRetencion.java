
package fedaemon.pruebas.hilos;

import fedaemon.pruebas.dao.RetencionDAO;
import fedaemon.pruebas.frms.frmMonitor;
import fedaemon.pruebas.util.ConexionBD;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 *
 * @author Mike
 */
public final class ThreadAutorizarRetencion extends Thread{
    
    protected ConexionBD conexionBD;
    protected frmMonitor frmMonitor;
    public RetencionDAO retencionDAO;
    private final static Logger log=Logger.getLogger(ThreadAutorizarRetencion.class);
    
    @Override
    public void run(){

       int enviadas=0;
       int contar=0;
       long minutos=0;
       PropertyConfigurator.configure("log4j.properties");
       
       ConexionBD con=new ConexionBD(conexionBD.getUsr(),conexionBD.getPass(),conexionBD.getServer(),conexionBD.getBase(),conexionBD.isSid(),conexionBD.isServiceName());
       retencionDAO=new RetencionDAO();
       retencionDAO.setMonitor(frmMonitor);
    
        log.trace("Iniciando hilo para autorización de Retenciones... ");
        this.frmMonitor.setMensajeRetenciones("[info] - Iniciando hilo para autorización de Retenciones... ");            
        while(true)
        {
            this.frmMonitor.cambiaEstadoPanel("jPRetencion", "Retenciones [EJECUTANDO]");
            this.frmMonitor.limpiaRetenciones();
            try
            {
                log.info("Estableciendo conexión con la Base de Datos... ");
                this.frmMonitor.setMensajeRetenciones("[info] - Estableciendo conexión con la Base de Datos... ");
                con.conectar();

                log.info("Conexión para hilo retenciones exitosa");
                this.frmMonitor.setMensajeRetenciones("[info] - Conexión para hilo retenciones exitosa");

                log.info("Verificando Comprobantes de Retención pendientes de autorización...");
                this.frmMonitor.setMensajeRetenciones("[info] - Verificando Comprobantes de Retención pendientes de autorización...");
        
                contar=retencionDAO.consultarRetencionPendiente(con);
//                contar=retencionDAO.consultarRetencionPendiente(con,"07",frmMonitor.getServicio().getAmbiente());
                retencionDAO.cambiaEstado(con, "EJECUTANDO", contar);
                
                if(contar==0)
                { 
                    log.info("No hay retenciones pendientes de autorización.");
                    this.frmMonitor.setMensajeRetenciones("[info] - No hay retenciones pendientes de autorización.");

                }
                else
                {
                    log.debug("Petición de autorización para: "+contar+" retenciones");
                    this.frmMonitor.setMensajeRetenciones("[info] - Petición de autorización para: "+contar+" retenciones");
                    enviadas=retencionDAO.enviarRetenciones(con);
                    log.debug("Se han enviado: "+enviadas+" comprobantes de retención para autorización del SRI.");
                    this.frmMonitor.setMensajeRetenciones("[info] - Se han enviado: "+enviadas+" comprobantes de retención para autorización del SRI.");

                }
            }
            catch(SQLException sqlex)
            {
                log.error("Error al conectar con la base de datos\n"+sqlex.getMessage());
                this.frmMonitor.setMensajeRetenciones("[Error] - Error al conectar con la base de datos\n"+sqlex.getMessage());
            }
            catch(ClassNotFoundException cnfe)
            {
                log.error("Error al conectar con la base de datos. "+cnfe.getMessage());
                this.frmMonitor.setMensajeRetenciones("[Error] - Error al conectar con la base de datos. "+cnfe.getMessage());
            }
            finally
            {
                try
                {
                    log.info("Cerrando conexión con la Base de Datos... ");
                    this.frmMonitor.setMensajeRetenciones("[info] - Cerrando conexión con la Base de Datos... ");
                    retencionDAO.cambiaEstado(con,"EN ESPERA", 0);
                    con.desconectar();
                    log.info("Se ha cerrado la conexión con la base de datos");
                    this.frmMonitor.setMensajeRetenciones("[info] - Se ha cerrado la conexión con la base de datos");
                }
                catch(SQLException sqle)
                {
                    log.error("Error al cerrar la conexión con la base de datos. "+sqle.getMessage());
                    this.frmMonitor.setMensajeRetenciones("[Error] - Error al cerrar la conexión con la base de datos. "+sqle.getMessage());
                }
                finally
                {
                    log.trace("Continuando...");
                    this.frmMonitor.setMensajeRetenciones("[trace] - Continuando...");
                }
            }
        
            try
            {
                minutos=frmMonitor.getServicio().getTiempoEspera()/60000;
                log.debug("Pausando el Hilo Retenciones por "+minutos+" minuto(s)");
                this.frmMonitor.setMensajeRetenciones("[info] - Pausando el Hilo Retenciones por "+minutos+" minuto(s)");
                this.frmMonitor.cambiaEstadoPanel("jPRetencion", "Retenciones [EN ESPERA]");
                this.sleep(frmMonitor.getServicio().getTiempoEspera());
                
            } 
            catch (Exception ex)
            {
                log.error("Error al pausar el hilo");
                this.frmMonitor.setMensajeRetenciones("[error] - Error al pausar el hilo");
            }
            finally
            {
                log.trace("Continuando...");
                this.frmMonitor.setMensajeRetenciones("[trace] - Continuando...");
            }
        }
    
    }
    
    public ConexionBD getConexion() {
        return conexionBD;
    }

    public void setConexion(ConexionBD conexion) {
        this.conexionBD = conexion;
    }
    
    public frmMonitor getMonitor() {
        return frmMonitor;
    }

    public void setMonitor(frmMonitor MONITOR) {
        this.frmMonitor = MONITOR;
    }
      
}
