
package fedaemon.pruebas.util;

/**
 *
 * @author Michael Beltrán
 */
public final class InfoDocumento {
    
    private String estab;
    private String lineas;
    private String secuencial;
    private String ptoEmi;
    private Double importeTotal;
    private Double totalSinImpuesto;
    private Double totalDescuento;
    private Double totalModificacion;
    
    public InfoDocumento()
    {
        
    }

    /**
     * @return the estab
     */
    public String getEstab() {
        return estab;
    }

    /**
     * @param estab the estab to set
     */
    public void setEstab(String estab) {
        this.estab = estab;
    }

    /**
     * @return the lineas
     */
    public String getLineas() {
        return lineas;
    }

    /**
     * @param lineas the lineas to set
     */
    public void setLineas(String lineas) {
        this.lineas = lineas;
    }

    /**
     * @return the secuencial
     */
    public String getSecuencial() {
        return secuencial;
    }

    /**
     * @param secuencial the secuencial to set
     */
    public void setSecuencial(String secuencial) {
        this.secuencial = secuencial;
    }

    /**
     * @return the ptoEmi
     */
    public String getPtoEmi() {
        return ptoEmi;
    }

    /**
     * @param ptoEmi the ptoEmi to set
     */
    public void setPtoEmi(String ptoEmi) {
        this.ptoEmi = ptoEmi;
    }

    /**
     * @return the importeTotal
     */
    public Double getImporteTotal() {
        return importeTotal;
    }

    /**
     * @param importeTotal the importeTotal to set
     */
    public void setImporteTotal(Double importeTotal) {
        this.importeTotal = importeTotal;
    }

    /**
     * @return the totalSinImpuesto
     */
    public Double getTotalSinImpuesto() {
        return totalSinImpuesto;
    }

    /**
     * @param totalSinImpuesto the totalSinImpuesto to set
     */
    public void setTotalSinImpuesto(Double totalSinImpuesto) {
        this.totalSinImpuesto = totalSinImpuesto;
    }

    /**
     * @return the totalDescuento
     */
    public Double getTotalDescuento() {
        return totalDescuento;
    }

    /**
     * @param totalDescuento the totalDescuento to set
     */
    public void setTotalDescuento(Double totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    /**
     * @return the totalModificacion
     */
    public Double getTotalModificacion() {
        return totalModificacion;
    }

    /**
     * @param totalModificacion the totalModificacion to set
     */
    public void setTotalModificacion(Double totalModificacion) {
        this.totalModificacion = totalModificacion;
    }
    
    
    
}
