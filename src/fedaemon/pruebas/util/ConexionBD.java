package fedaemon.pruebas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Michael Beltrán
 * Desarrollado para TEVCOL
 */
public final class ConexionBD {
    private Connection con;
    private String usr;
    private String pass;
    private String server;
    private String base;
    private boolean sid;
    private boolean serviceName;

    public ConexionBD(String usr,String pass,String server,String base,boolean sid,boolean serviceName){
        con=null;
        setUsr(usr);
        setPass(pass);
        setServer(server);
        setBase(base);
        setSid(sid);
        setServiceName(serviceName);
    }

    public void conectar()throws ClassNotFoundException,SQLException{
         
        String url=null;
        
        Class.forName("oracle.jdbc.driver.OracleDriver");
        //en esta parte OJO con el tipo de conexion:
        //si es SID se pone ":" despues del numero de puerto
        //si es SERVICE_NAME se pone "/" despues del numero de puerto
        if(sid)
        { url="jdbc:oracle:thin:@"+server+":1521:"+base;}
        if(serviceName)
        { url="jdbc:oracle:thin:@"+server+":1521/"+base;}
        
        setCon(DriverManager.getConnection(url, usr, pass));
        
            
    }

    public void desconectar()throws SQLException{
        
        if( getCon()!=null)
            getCon().close();
        
        
    }
    
     public String getUsr() {
        return usr;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    /**
     * @return the sid
     */
    public boolean isSid() {
        return sid;
    }

    /**
     * @param sid the sid to set
     */
    public void setSid(boolean sid) {
        this.sid = sid;
    }

    /**
     * @return the serviceName
     */
    public boolean isServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(boolean serviceName) {
        this.serviceName = serviceName;
    }
}
