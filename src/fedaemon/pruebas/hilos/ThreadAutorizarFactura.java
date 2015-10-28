
package fedaemon.pruebas.hilos;

import fedaemon.pruebas.util.ConexionBD;
import fedaemon.pruebas.dao.FacturaDAO;
import fedaemon.pruebas.frms.frmMonitor;
import java.sql.SQLException;

/**
 *
 * @author Michael Beltrán
 */
public final class ThreadAutorizarFactura extends Thread{
    
    protected ConexionBD conexionBD;
    protected frmMonitor frmMonitor;
    public FacturaDAO facturaDAO;

    
    @Override
    public void run(){
        
        ConexionBD con=null;
        int enviadas=0;
        int contar=0;
        long minutos=0;
        
        facturaDAO=new FacturaDAO(); 
        facturaDAO.setMonitor(frmMonitor);
        con=new ConexionBD(conexionBD.getUsr(),conexionBD.getPass(),conexionBD.getServer(),conexionBD.getBase(),conexionBD.isSid(),conexionBD.isServiceName());
        System.out.println("[info] - Iniciando hilo para autorización de Facturas... ");
        this.frmMonitor.setMensajeFacturas("[info] - Iniciando hilo para autorización de Facturas... ");
                     
        while(true)
        {
            this.frmMonitor.cambiaEstadoPanel("jPFacturas", "Facturas [EJECUTANDO]");
            this.frmMonitor.limpiaFacturas();
            try
            {
                System.out.println("[info] - Estableciendo conexión con la Base de Datos... ");
                this.frmMonitor.setMensajeFacturas("[info] - Estableciendo conexión con la Base de Datos... ");
                con.conectar();
                System.out.println("[info] - Conexión para hilo facturas exitosa");
                this.frmMonitor.setMensajeFacturas("[info] - Conexión para hilo facturas exitosa");
                
                System.out.println("[info] - Verificando Facturas pendientes de autorización...");
                this.frmMonitor.setMensajeFacturas("[info] - Verificando Facturas pendientes de autorización...");
                
                contar=facturaDAO.consultarFacturaPendiente(con);
//                contar=facturaDAO.consultarFacturaPendiente(con,"01",frmMonitor.getServicio().getAmbiente());
                facturaDAO.cambiaEstado(con, "EJECUTANDO",contar);
                
                if(contar==0)
                { 
                    System.out.println("[info] - No hay faturas pendientes de autorización.");
                    this.frmMonitor.setMensajeFacturas("[info] - No hay faturas pendientes de autorización.");
                }
                else
                {
                    System.out.println("[info] - Petición de autorización para: "+contar+" facturas");
                    this.frmMonitor.setMensajeFacturas("[info] - Petición de autorización para: "+contar+" facturas");
                    enviadas=facturaDAO.enviarFacturas(con);
                    System.out.println("[info] - Se han enviado: "+enviadas+" facturas para autorización del SRI.");
                    this.frmMonitor.setMensajeFacturas("[info] - Se han enviado: "+enviadas+" facturas para autorización del SRI.");

                }
            
            }
            catch(SQLException sqlex)
            {
                System.out.println("[error] - Error al conectar con la base de datos\n"+sqlex.getMessage());
                this.frmMonitor.setMensajeFacturas("[Error] - Error al conectar con la base de datos\n"+sqlex.getMessage());
            }
            catch(ClassNotFoundException cnfe)
            {
                System.out.println("[error] - Error al conectar con la base de datos\n"+cnfe.getMessage());
                this.frmMonitor.setMensajeFacturas("[Error] - Error al conectar con la base de datos\n"+cnfe.getMessage());
            }
            finally
            {
                try
                {
                    System.out.println("[info] - Cerrando conexión con la Base de Datos... ");
                    this.frmMonitor.setMensajeFacturas("[info] - Cerrando conexión con la Base de Datos... ");
                    facturaDAO.cambiaEstado(con,"EN ESPERA", 0);
                    con.desconectar();
                    System.out.println("[info] - Se ha cerrado la conexión con la base de datos");
                    this.frmMonitor.setMensajeFacturas("[info] - Se ha cerrado la conexión con la base de datos");
                }
                catch(SQLException sqle)
                {
                    System.out.println("[error] - Error al cerrar la conexión con la base de datos\n"+sqle.getMessage());
                    this.frmMonitor.setMensajeFacturas("[Error] - Error al cerrar la conexión con la base de datos\n"+sqle.getMessage());
                }
                finally
                {
                    System.out.println("[info] - Continuando...");
                    this.frmMonitor.setMensajeFacturas("[info] - Continuando...");
                }
            }
            
            try 
            {
                minutos=frmMonitor.getServicio().getTiempoEspera()/60000;
                System.out.println("[info] - Pausando el Hilo Facturas por "+minutos+" minuto(s)");
                this.frmMonitor.setMensajeFacturas("[info] - Pausando el Hilo Facturas por "+minutos+" minuto(s)");
                this.frmMonitor.cambiaEstadoPanel("jPFacturas", "Facturas [EN ESPERA]");
                this.sleep(frmMonitor.getServicio().getTiempoEspera()); 
                
            } 
            catch (Exception ex)
            {
                System.out.println("[error] - Error al pausar el hilo");
                this.frmMonitor.setMensajeFacturas("[error] - Error al pausar el hilo");
            }
            finally
            {
                System.out.println("[info] - Continuando...");
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
