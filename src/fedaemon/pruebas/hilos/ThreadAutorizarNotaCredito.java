
package fedaemon.pruebas.hilos;

import fedaemon.pruebas.util.ConexionBD;
import fedaemon.pruebas.dao.NotaCreditoDAO;
import fedaemon.pruebas.frms.frmMonitor;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Mike
 */
public final class ThreadAutorizarNotaCredito extends Thread{
    
    protected ConexionBD conexionBD;
    protected frmMonitor frmMonitor;
    public  NotaCreditoDAO notaCreditoDAO;
    private final static Logger log=Logger.getLogger(ThreadAutorizarNotaCredito.class);
    
    @Override
    public void run(){

        ConexionBD con=null; 
        int enviadas=0;
        int contar=0;
        long minutos=0;
        PropertyConfigurator.configure("log4j.properties");
        
        notaCreditoDAO=new NotaCreditoDAO();
        notaCreditoDAO.setMonitor(frmMonitor);
        con=new ConexionBD(conexionBD.getUsr(),conexionBD.getPass(),conexionBD.getServer(),conexionBD.getBase(),conexionBD.isSid(),conexionBD.isServiceName());
        log.trace("Iniciando hilo para autorización de Notas de Crédito... ");
        this.frmMonitor.setMensajeNC("[info] - Iniciando hilo para autorización de Notas de Crédito... ");
        
        while(true)
        {
            this.frmMonitor.cambiaEstadoPanel("jPNC", "Notas de Crédito [EJECUTANDO]");
            this.frmMonitor.limpiaNC();
            try{
                log.info("Estableciendo conexión con la Base de Datos... ");
                this.frmMonitor.setMensajeNC("[info] - Estableciendo conexión con la Base de Datos... ");
                con.conectar();

                log.info("Conexión para hilo notas de crédito exitosa");
                this.frmMonitor.setMensajeNC("[info] - Conexión para hilo notas de crédito exitosa");
                log.info("Verificando Notas de Crédito pendientes de autorización...");
                this.frmMonitor.setMensajeNC("[info] - Verificando Notas de Crédito pendientes de autorización...");

                contar=notaCreditoDAO.consultarNotaCreditoPendiente(con);
//                contar=notaCreditoDAO.consultarNotaCreditoPendiente(con,"04",frmMonitor.getServicio().getAmbiente());
                notaCreditoDAO.cambiaEstado(con,"EJECUTANDO", contar);
        
                if(contar==0)
                { 
                    log.info("No hay Notas de Crédito pendientes de autorización.");
                    this.frmMonitor.setMensajeNC("[info] - No hay Notas de Crédito pendientes de autorización.");
                }
                else
                {
                    log.info("Petición de autorización para: "+contar+" notas de crédito");
                    this.frmMonitor.setMensajeNC("[info] - Petición de autorización para: "+contar+" notas de crédito");
                    enviadas=notaCreditoDAO.enviarNotasCredito(con);
                    log.debug("Se han enviado: "+enviadas+" notas de crédito para autorización del SRI.");
                    this.frmMonitor.setMensajeNC("[info] - Se han enviado: "+enviadas+" notas de crédito para autorización del SRI.");

                }
            }
            catch(SQLException sqlex)
            {
                log.error("Error al conectar con la base de datos\n"+sqlex.getMessage());
                this.frmMonitor.setMensajeNC("[Error] - Error al conectar con la base de datos\n"+sqlex.getMessage());
            }
            catch(ClassNotFoundException cnfe)
            {
                log.error("Error al conectar con la base de datos\n"+cnfe.getMessage());
                this.frmMonitor.setMensajeNC("[Error] - Error al conectar con la base de datos\n"+cnfe.getMessage());
            }
            finally
            {
                try
                {
                    log.info("Cerrando conexión con la Base de Datos... ");
                    this.frmMonitor.setMensajeNC("[info] - Cerrando conexión con la Base de Datos... ");
                    notaCreditoDAO.cambiaEstado(con,"EN ESPERA", 0);
                    con.desconectar();
                    log.info("Se ha cerrado la conexión con la base de datos");
                    this.frmMonitor.setMensajeNC("[info] - Se ha cerrado la conexión con la base de datos");
                }
                catch(SQLException sqle)
                {
                    log.error("Error al cerrar la conexión con la base de datos\n"+sqle.getMessage());
                    this.frmMonitor.setMensajeNC("[Error] - Error al cerrar la conexión con la base de datos\n"+sqle.getMessage());
                }
                finally
                {
                    log.trace("Continuando...");
                    this.frmMonitor.setMensajeNC("[info] - Continuando...");
                }
            }
        
            try
            {
                minutos=frmMonitor.getServicio().getTiempoEspera()/60000;
                log.debug("Pausando el Hilo Notas de Crédito por "+minutos+" minuto(s)");
                this.frmMonitor.setMensajeNC("[info] - Pausando el Hilo Notas de Crédito por "+minutos+" minuto(s)");
                this.frmMonitor.cambiaEstadoPanel("jPNC", "Notas de Crédito [EN ESPERA]");
                this.sleep(frmMonitor.getServicio().getTiempoEspera());
            }
            catch (Exception ex)
            {
                log.error("Error al pausar el hilo");
                this.frmMonitor.setMensajeNC("[error] - Error al pausar el hilo");
            }
            finally
            {
                log.trace("Continuando...");
                this.frmMonitor.setMensajeNC("[info] - Continuando...");
            }
        
        }//final del while
    
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

    public void setMonitor(frmMonitor monitor) {
        this.frmMonitor = monitor;
    }
}
