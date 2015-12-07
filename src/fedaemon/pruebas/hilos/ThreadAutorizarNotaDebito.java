

package fedaemon.pruebas.hilos;

import fedaemon.pruebas.util.ConexionBD;
import fedaemon.pruebas.dao.NotaDebitoDAO;
import fedaemon.pruebas.frms.frmMonitor;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Michael Beltrán
 */
public final class ThreadAutorizarNotaDebito extends Thread{
    
    protected ConexionBD conexionBD;
    protected frmMonitor frmMonitor;
    public NotaDebitoDAO notaDebitoDAO;
    private final static Logger log=Logger.getLogger(ThreadAutorizarNotaDebito.class);
    
    @Override
    public void run(){

        ConexionBD con=null;
        int enviadas=0;
        int contar=0;
        long minutos;
        PropertyConfigurator.configure("log4j.properties");

        notaDebitoDAO=new NotaDebitoDAO();
        notaDebitoDAO.setMONITOR(frmMonitor);
        con=new ConexionBD(conexionBD.getUsr(),conexionBD.getPass(),conexionBD.getServer(),conexionBD.getBase(),conexionBD.isSid(),conexionBD.isServiceName());

        log.trace("Iniciando hilo para autorización de Notas de Débito... ");
        this.frmMonitor.setMensajeND("[info] - Iniciando hilo para autorización de Notas de Débito... ");
        
                     
        while(true)
        {
            this.frmMonitor.cambiaEstadoPanel("jPND", "Notas de Dédito [EJECUTANDO]");
            this.frmMonitor.limpiaND();
            try{
                log.info("Estableciendo conexión con la Base de Datos... ");
                this.frmMonitor.setMensajeND("[info] - Estableciendo conexión con la Base de Datos... ");
                con.conectar();
                log.info("Conexión para hilo notas de débito exitosa");
                this.frmMonitor.setMensajeND("[info] - Conexión para hilo notas de débito exitosa");
                
                log.info("Verificando Notas de Débito pendientes de autorización...");
                this.frmMonitor.setMensajeND("[info] - Verificando Notas de Débito pendientes de autorización...");

                contar=notaDebitoDAO.consultarNotaDebitoPendiente(con);
//                contar=notaDebitoDAO.consultarNotaDebitoPendiente(con,"05",frmMonitor.getServicio().getAmbiente());
                notaDebitoDAO.cambiaEstado(con, "EJECUTANDO", contar);
                
                if(contar==0)
                { 
                    log.info("No hay Notas de Débito pendientes de autorización.");
                    this.frmMonitor.setMensajeND("[info] - No hay Notas de Débito pendientes de autorización.");
                }
                else
                {
                    log.debug("Petición de autorización para: "+contar+" notas de dédito");
                    this.frmMonitor.setMensajeND("[info] - Petición de autorización para: "+contar+" notas de dédito");
                    enviadas=notaDebitoDAO.enviarNotasDebito(con);
                    log.debug("Se han enviado: "+enviadas+" notas de débito para autorización del SRI.");
                    this.frmMonitor.setMensajeND("[info] - Se han enviado: "+enviadas+" notas de débito para autorización del SRI.");

                }
            }
            catch(SQLException sqlex)
            {
                log.error("Error al conectar con la base de datos. "+sqlex.getMessage());
                this.frmMonitor.setMensajeND("[Error] - Error al conectar con la base de datos. "+sqlex.getMessage());
            }
            catch(ClassNotFoundException cnfe)
            {
                log.error("Error al conectar con la base de datos. "+cnfe.getMessage());
                this.frmMonitor.setMensajeND("[Error] - Error al conectar con la base de datos. "+cnfe.getMessage());
            }
            finally
            {
                try
                {
                    log.info("Cerrando conexión con la Base de Datos... ");
                    this.frmMonitor.setMensajeND("[info] - Cerrando conexión con la Base de Datos... ");
                    notaDebitoDAO.cambiaEstado(con,"EN ESPERA", 0);
                    con.desconectar();
                    log.info("Se ha cerrado la conexión con la base de datos");
                    this.frmMonitor.setMensajeND("[info] - Se ha cerrado la conexión con la base de datos");
                }
                catch(SQLException sqle)
                {
                    log.error("Error al cerrar la conexión con la base de datos. "+sqle.getMessage());
                    this.frmMonitor.setMensajeND("[Error] - Error al cerrar la conexión con la base de datos. "+sqle.getMessage());
                }
                finally
                {
                    log.trace("Continuando...");
                    this.frmMonitor.setMensajeND("[info] - Continuando...");
                }
            }
            
            try 
            {
                minutos=frmMonitor.getServicio().getTiempoEspera()/60000;
                log.debug("Pausando el Hilo Notas Debito por "+minutos+" minuto(s)");
                this.frmMonitor.setMensajeND("[info] - Pausando el Hilo Notas Debito por "+minutos+" minuto(s)");
                this.frmMonitor.cambiaEstadoPanel("jPND", "Notas Dedito [EN ESPERA]");
                this.sleep(frmMonitor.getServicio().getTiempoEspera()); 
                
            } 
            catch (Exception ex)
            {
                log.error("Error al pausar el hilo");
                this.frmMonitor.setMensajeND("[error] - Error al pausar el hilo");
            }
            finally
            {
                log.trace("Continuando...");
                this.frmMonitor.setMensajeND("[info] - Continuando...");
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
