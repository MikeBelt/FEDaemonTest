/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fedaemon.pruebas.util;

/**
 *
 * @author Michael Beltrán
 */
public final class Empresa {

    
    private String nombre;
    private String servidor;
    private String base;
    private String usuario;
    private String pasword;
    private boolean sid;
    private boolean serviceName;
    
    public Empresa()
    {
    
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

   

    /**
     * @return the servidor
     */
    public String getServidor() {
        return servidor;
    }

    /**
     * @param servidor the servidor to set
     */
    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    /**
     * @return the base
     */
    public String getBase() {
        return base;
    }

    /**
     * @param base the base to set
     */
    public void setBase(String base) {
        this.base = base;
    }

    /**
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the pasword
     */
    public String getPasword() {
        return pasword;
    }

    /**
     * @param pasword the pasword to set
     */
    public void setPasword(String pasword) {
        this.pasword = pasword;
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
