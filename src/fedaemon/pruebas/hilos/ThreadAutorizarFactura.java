
package fedaemon.pruebas.hilos;

import fedaemon.pruebas.dao.FacturaDAO;
import fedaemon.pruebas.frms.frmMonitor;
import fedaemon.pruebas.util.ConexionBD;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Michael Beltrán
 */
public final class ThreadAutorizarFactura extends Thread{
    
    protected ConexionBD conexionBD;
    protected frmMonitor frmMonitor;
    public FacturaDAO facturaDAO;
    private final static Logger log=Logger.getLogger(ThreadAutorizarFactura.class);

    
    @Override
    public void run(){
        
        ConexionBD con=null;
        int enviadas=0;
        int contar=0;
        long minutos=0;
        PropertyConfigurator.configure("log4j.properties");
        
        facturaDAO=new FacturaDAO(); 
        facturaDAO.setMonitor(frmMonitor);
        con=new ConexionBD(conexionBD.getUsr(),conexionBD.getPass(),conexionBD.getServer(),conexionBD.getBase(),conexionBD.isSid(),conexionBD.isServiceName());
        log.info("Iniciando hilo para autorización de Facturas... ");
        this.frmMonitor.setMensajeFacturas("[info] - Iniciando hilo para autorización de Facturas... ");
                     
        while(true)
        {
            this.frmMonitor.cambiaEstadoPanel("jPFacturas", "Facturas [EJECUTANDO]");
            this.frmMonitor.limpiaFacturas();
            try
            {
                log.info("Estableciendo conexión con la Base de Datos... ");
                this.frmMonitor.setMensajeFacturas("[info] - Estableciendo conexión con la Base de Datos... ");
                con.conectar();
                log.info("Conexión para hilo facturas exitosa");
                this.frmMonitor.setMensajeFacturas("[info] - Conexión para hilo facturas exitosa");
                
                log.info("Verificando Facturas pendientes de autorización...");
                this.frmMonitor.setMensajeFacturas("[info] - Verificando Facturas pendientes de autorización...");
                
                contar=facturaDAO.consultarFacturaPendiente(con);
//                contar=facturaDAO.consultarFacturaPendiente(con,"01",frmMonitor.getServicio().getAmbiente());
                facturaDAO.cambiaEstado(con, "EJECUTANDO",contar);
                
                if(contar==0)
                { 
                    log.info("No hay faturas pendientes de autorización.");
                    this.frmMonitor.setMensajeFacturas("[info] - No hay faturas pendientes de autorización.");
                }
                else
                {
                    log.debug("Petición de autorización para: "+contar+" facturas");
                    this.frmMonitor.setMensajeFacturas("[info] - Petición de autorización para: "+contar+" facturas");
                    enviadas=facturaDAO.enviarFacturas(con);
                    log.debug("Se han enviado: "+enviadas+" facturas para autorización del SRI.");
                    this.frmMonitor.setMensajeFacturas("[info] - Se han enviado: "+enviadas+" facturas para autorización del SRI.");

                }
            
            }
            catch(SQLException sqlex)
            {
                log.error("Error al conectar con la base de datos\n"+sqlex.getMessage());
                this.frmMonitor.setMensajeFacturas("[Error] - Error al conectar con la base de datos\n"+sqlex.getMessage());
            }
            catch(ClassNotFoundException cnfe)
            {
                log.error("Error al conectar con la base de datos\n"+cnfe.getMessage());
                this.frmMonitor.setMensajeFacturas("[Error] - Error al conectar con la base de datos\n"+cnfe.getMessage());
            }
            finally
            {
                try
                {
                    log.info("Cerrando conexión con la Base de Datos... ");
                    this.frmMonitor.setMensajeFacturas("[info] - Cerrando conexión con la Base de Datos... ");
                    facturaDAO.cambiaEstado(con,"EN ESPERA", 0);
                    con.desconectar();
                    log.info("Se ha cerrado la conexión con la base de datos");
                    this.frmMonitor.setMensajeFacturas("[info] - Se ha cerrado la conexión con la base de datos");
                }
                catch(SQLException sqle)
                {
                    log.error("Error al cerrar la conexión con la base de datos\n"+sqle.getMessage());
                    this.frmMonitor.setMensajeFacturas("[Error] - Error al cerrar la conexión con la base de datos\n"+sqle.getMessage());
                }
                finally
                {
                    log.trace("Continuando...");
                    this.frmMonitor.setMensajeFacturas("[info] - Continuando...");
                }
            }
            
            try 
            {
                minutos=frmMonitor.getServicio().getTiempoEspera()/60000;
                log.debug("Pausando el Hilo Facturas por "+minutos+" minuto(s)");
                this.frmMonitor.setMensajeFacturas("[info] - Pausando el Hilo Facturas por "+minutos+" minuto(s)");
                this.frmMonitor.cambiaEstadoPanel("jPFacturas", "Facturas [EN ESPERA]");
                this.sleep(frmMonitor.getServicio().getTiempoEspera()); 
                
            } 
            catch (Exception ex)
            {
                log.error("Error al pausar el hilo");
                this.frmMonitor.setMensajeFacturas("[error] - Error al pausar el hilo");
            }
            finally
            {
                log.trace("Continuando...");
                this.frmMonitor.setMensajeFacturas("[info] - Continuando...");
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

    public void setMonitor(frmMonitor monitor) {
        this.frmMonitor = monitor;
    }
    
    

}
