

package fedaemon.pruebas.hilos;

import fedaemon.pruebas.util.ConexionBD;
import fedaemon.pruebas.dao.NotaDebitoDAO;
import fedaemon.pruebas.frms.frmMonitor;
import java.sql.SQLException;

/**
 *
 * @author Michael Beltrán
 */
public final class ThreadAutorizarNotaDebito extends Thread{
    
    protected ConexionBD conexionBD;
    protected frmMonitor frmMonitor;
    public NotaDebitoDAO notaDebitoDAO;
    
    @Override
    public void run(){

        ConexionBD con=null;
        int enviadas=0;
        int contar=0;
        long minutos;

        notaDebitoDAO=new NotaDebitoDAO();
        notaDebitoDAO.setMONITOR(frmMonitor);
        con=new ConexionBD(conexionBD.getUsr(),conexionBD.getPass(),conexionBD.getServer(),conexionBD.getBase(),conexionBD.isSid(),conexionBD.isServiceName());

        System.out.println("[info] - Iniciando hilo para autorización de Notas de Débito... ");
        this.frmMonitor.setMensajeND("[info] - Iniciando hilo para autorización de Notas de Débito... ");
        
                     
        while(true)
        {
            this.frmMonitor.cambiaEstadoPanel("jPND", "Notas de Dédito [EJECUTANDO]");
            this.frmMonitor.limpiaND();
            try{
                System.out.println("[info] - Estableciendo conexión con la Base de Datos... ");
                this.frmMonitor.setMensajeND("[info] - Estableciendo conexión con la Base de Datos... ");
                con.conectar();
                System.out.println("[info] - Conexión para hilo notas de débito exitosa");
                this.frmMonitor.setMensajeND("[info] - Conexión para hilo notas de débito exitosa");
                
                System.out.println("[info] - Verificando Notas de Débito pendientes de autorización...");
                this.frmMonitor.setMensajeND("[info] - Verificando Notas de Débito pendientes de autorización...");

                contar=notaDebitoDAO.consultarNotaDebitoPendiente(con);
//                contar=notaDebitoDAO.consultarNotaDebitoPendiente(con,"05",frmMonitor.getServicio().getAmbiente());
                notaDebitoDAO.cambiaEstado(con, "EJECUTANDO", contar);
                
                if(contar==0)
                { 
                    System.out.println("[info] - No hay Notas de Débito pendientes de autorización.");
                    this.frmMonitor.setMensajeND("[info] - No hay Notas de Débito pendientes de autorización.");
                }
                else
                {
                    System.out.println("[info] - Petición de autorización para: "+contar+" notas de dédito");
                    this.frmMonitor.setMensajeND("[info] - Petición de autorización para: "+contar+" notas de dédito");
                    enviadas=notaDebitoDAO.enviarNotasDebito(con);
                    System.out.println("[info] - Se han enviado: "+enviadas+" notas de débito para autorización del SRI.");
                    this.frmMonitor.setMensajeND("[info] - Se han enviado: "+enviadas+" notas de débito para autorización del SRI.");

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
                    this.frmMonitor.setMensajeND("[info] - Cerrando conexión con la Base de Datos... ");
                    notaDebitoDAO.cambiaEstado(con,"EN ESPERA", 0);
                    con.desconectar();
                    System.out.println("[info] - Se ha cerrado la conexión con la base de datos");
                    this.frmMonitor.setMensajeND("[info] - Se ha cerrado la conexión con la base de datos");
                }
                catch(SQLException sqle)
                {
                    System.out.println("[error] - Error al cerrar la conexión con la base de datos\n"+sqle.getMessage());
                    this.frmMonitor.setMensajeND("[Error] - Error al cerrar la conexión con la base de datos\n"+sqle.getMessage());
                }
                finally
                {
                    System.out.println("[info] - Continuando...");
                    this.frmMonitor.setMensajeND("[info] - Continuando...");
                }
            }
            
            try 
            {
                minutos=frmMonitor.getServicio().getTiempoEspera()/60000;
                System.out.println("[info] - Pausando el Hilo Notas Debito por "+minutos+" minuto(s)");
                this.frmMonitor.setMensajeND("[info] - Pausando el Hilo Notas Debito por "+minutos+" minuto(s)");
                this.frmMonitor.cambiaEstadoPanel("jPND", "Notas Dedito [EN ESPERA]");
                this.sleep(frmMonitor.getServicio().getTiempoEspera()); 
                
            } 
            catch (Exception ex)
            {
                System.out.println("[error] - Error al pausar el hilo");
                this.frmMonitor.setMensajeND("[error] - Error al pausar el hilo");
            }
            finally
            {
                System.out.println("[info] - Continuando...");
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
