/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fedaemon.pruebas.util;

/**
 *
 * @author Michael Beltran
 */
public final class Servicio {
    
    private String ambiente;
    private String version;
    private String so;
    private String arquitectura;
    private String pid;
    private String directorioRaiz;
    private String directorioFacturas;
    private String directorioRetenciones;
    private String directorioNotasCredito;
    private String directorioNotasDebito;
    private long tiempoEspera;

    public Servicio(){}
    
    /**
     * @return the ambiente
     */
    public String getAmbiente() {
        return ambiente;
    }

    /**
     * @param ambiente the ambiente to set
     */
    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the so
     */
    public String getSo() {
        return so;
    }

    /**
     * @param so the so to set
     */
    public void setSo(String so) {
        this.so = so;
    }

    /**
     * @return the arquitectura
     */
    public String getArquitectura() {
        return arquitectura;
    }

    /**
     * @param arquitectura the arquitectura to set
     */
    public void setArquitectura(String arquitectura) {
        this.arquitectura = arquitectura;
    }


    /**
     * @return the pid
     */
    public String getPid() {
        return pid;
    }

    /**
     * @param pid the pid to set
     */
    public void setPid(String pid) {
        this.pid = pid;
    }

    /**
     * @return the directorioRaiz
     */
    public String getDirectorioRaiz() {
        return directorioRaiz;
    }

    /**
     * @param directorioRaiz the directorioRaiz to set
     */
    public void setDirectorioRaiz(String directorioRaiz) {
        this.directorioRaiz = directorioRaiz;
    }

    /**
     * @return the directorioFacturas
     */
    public String getDirectorioFacturas() {
        return directorioFacturas;
    }

    /**
     * @param directorioFacturas the directorioFacturas to set
     */
    public void setDirectorioFacturas(String directorioFacturas) {
        this.directorioFacturas = directorioFacturas;
    }

    /**
     * @return the directorioRetenciones
     */
    public String getDirectorioRetenciones() {
        return directorioRetenciones;
    }

    /**
     * @param directorioRetenciones the directorioRetenciones to set
     */
    public void setDirectorioRetenciones(String directorioRetenciones) {
        this.directorioRetenciones = directorioRetenciones;
    }

    /**
     * @return the directorioNotasCredito
     */
    public String getDirectorioNotasCredito() {
        return directorioNotasCredito;
    }

    /**
     * @param directorioNotasCredito the directorioNotasCredito to set
     */
    public void setDirectorioNotasCredito(String directorioNotasCredito) {
        this.directorioNotasCredito = directorioNotasCredito;
    }

    /**
     * @return the directorioNotasDebito
     */
    public String getDirectorioNotasDebito() {
        return directorioNotasDebito;
    }

    /**
     * @param directorioNotasDebito the directorioNotasDebito to set
     */
    public void setDirectorioNotasDebito(String directorioNotasDebito) {
        this.directorioNotasDebito = directorioNotasDebito;
    }

    /**
     * @return the tiempoEspera
     */
    public long getTiempoEspera() {
        return tiempoEspera;
    }

    /**
     * @param tiempoEspera the tiempoEspera to set
     */
    public void setTiempoEspera(long tiempoEspera) {
        this.tiempoEspera = tiempoEspera;
    }
    
}
