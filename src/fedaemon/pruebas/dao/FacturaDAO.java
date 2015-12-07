
package fedaemon.pruebas.dao;


import fedaemon.pruebas.frms.frmMonitor;
import fedaemon.infact.pruebas.ArrayOfDetAdicional;
import fedaemon.infact.pruebas.ArrayOfDetalle;
import fedaemon.infact.pruebas.ArrayOfImpuesto;
import fedaemon.infact.pruebas.ArrayOfInfoAdicional;
import fedaemon.infact.pruebas.ArrayOfTotalImpuesto;
import fedaemon.infact.pruebas.AutorizarFactura;
import fedaemon.infact.pruebas.CloudAutorizarComprobante;
import fedaemon.infact.pruebas.DetAdicional;
import fedaemon.infact.pruebas.Detalle;
import fedaemon.infact.pruebas.IcloudAutorizarComprobante;
import fedaemon.infact.pruebas.Impuesto;
import fedaemon.infact.pruebas.InfoAdicional;
import fedaemon.infact.pruebas.InfoFactura;
import fedaemon.infact.pruebas.InfoTributaria;
import fedaemon.infact.pruebas.ObjectFactory;
import fedaemon.infact.pruebas.Response;
import fedaemon.infact.pruebas.TotalImpuesto;
import fedaemon.pruebas.util.ConexionBD;
import fedaemon.pruebas.util.InfoDocumento;
import java.io.File;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Michael Beltran
 */
public final class FacturaDAO {
    
    private frmMonitor frmMonitor;
    private final static Logger log = Logger.getLogger(FacturaDAO.class);
    
    public FacturaDAO()
    {
        PropertyConfigurator.configure("log4j.properties");
    }
    
    public int consultarFacturaPendiente(ConexionBD con){
        int result=0;
        String select="SELECT COUNT(*),ESTAB,PTOEMI,SECUENCIAL "
                + "FROM INVE_DOCUMENTOS_FE_DAT "
                + "WHERE NUME_AUTO_INVE_DOCU IS NULL "
                + "AND CODDOC='01' "
                + "AND AMBIENTE="+frmMonitor.getServicio().getAmbiente()
                + " GROUP BY ESTAB,PTOEMI,SECUENCIAL";
        Statement st=null;
        ResultSet rs=null;
        
        try
        {
            st= con.getCon().createStatement();
            rs=st.executeQuery(select);
            while(rs.next())
            {
            result++;
            }
        }
        catch(Exception ex)
        {
             log.error("error de ResultSet de consultaFacturasPendientes");
        }
        
        finally
        {
            
          try
          {
             if(rs!=null)
                rs.close();
          }catch(SQLException se2)
          {
              log.error("error de cerrar ResultSet de consultaFacturaPendiente");
          } 
        }

    return result;
    }
    
    public int consultarFacturaPendiente(ConexionBD con,String coddoc,String ambiente) {
        int result=0;
        String sentencia="{call SP_FACTCONSULTAPENDIENTES(?,?,?)}";
        CallableStatement cs=null;
        try
        {
            cs=con.getCon().prepareCall(sentencia);

            cs.setString(1, coddoc);
            cs.setString(2, ambiente);
            cs.registerOutParameter(3, java.sql.Types.NUMERIC);

            cs.execute();

            result=cs.getInt(3);
        }catch(SQLException ex)
        {
            log.error("error de CallableStatement de consoultaFacturaPendiente");
        }
        finally
        {
            try{
             if(cs!=null)
                cs.close();
            }
            catch(SQLException se2)
            {
                log.error("error al cerrar CallableStatement");
            }
        }

        return result;

    }
    
    public int enviarFacturas(ConexionBD con){

        int enviadas=0;
        ObjectFactory factory =null;
        InfoDocumento fra=null;
        ArrayList<InfoDocumento> arrayInfoDoc=null;
        ArrayList<AutorizarFactura> arrayAutorizarFactura=null;
        InfoTributaria infoTributaria=null;
        InfoFactura infoFactura=null;
        ArrayOfInfoAdicional arrayInfoAdicional=null;
        ArrayOfDetalle arrayDetalle=null;
        ArrayOfTotalImpuesto arrayTotalImpuestos=null;
        String marco="============================================================================";
        //OJO que al consultar data de la base se recuperará info como estaba hasta el ultimo COMMIT ejecutado
        String select="SELECT COUNT(*) LINEAS,TOTALSINIMPUESTOS,TOTALDESCUENTO,ESTAB,PTOEMI,SECUENCIAL,FECHAEMISION "
                + "FROM INVE_DOCUMENTOS_FE_DAT "
                + "WHERE NUME_AUTO_INVE_DOCU IS NULL "
                + "AND CODDOC='01' "
                + "AND AMBIENTE="+frmMonitor.getServicio().getAmbiente()
                + " GROUP BY TOTALSINIMPUESTOS,TOTALDESCUENTO,ESTAB,PTOEMI,SECUENCIAL,FECHAEMISION "
                + "ORDER BY FECHAEMISION ASC";
        String filtro=null;
        Statement st= null;
        ResultSet rs=null;  
        
        long start = 0;
        long stop = 0;
        Response respuesta=null;
        
        
        try{
        factory = new ObjectFactory();
        arrayInfoDoc=new ArrayList<>();
        arrayAutorizarFactura=new ArrayList<>();
        st= con.getCon().createStatement();
        rs=st.executeQuery(select);
        //Almacenando en el ArrayList los documentos que se van a enviar
        while(rs.next())
        {   
            fra=new InfoDocumento();
            fra.setLineas(rs.getString("LINEAS"));
            fra.setEstab(rs.getString("ESTAB"));
            fra.setPtoEmi(rs.getString("PTOEMI"));
            fra.setSecuencial(rs.getString("SECUENCIAL"));
            fra.setTotalSinImpuesto(rs.getDouble("TOTALSINIMPUESTOS"));
            fra.setTotalDescuento(rs.getDouble("TOTALDESCUENTO"));
            arrayInfoDoc.add(fra);
        }
        rs.close();
        st.close();
        
            //Recorriendo el arreglo que contiene documentos listos para enviar
            for(int i=0;i<arrayInfoDoc.size();i++){
            this.frmMonitor.limpiaFacturas();
            System.out.println("[info] - Registro #"+(i+1)+ " de "+arrayInfoDoc.size());
            this.frmMonitor.setMensajeFacturas("[info] - Registro #"+(i+1)+ " de "+arrayInfoDoc.size());
            infoTributaria=new InfoTributaria();
            infoFactura=new InfoFactura();
            arrayInfoAdicional=new ArrayOfInfoAdicional();
            arrayDetalle=new ArrayOfDetalle();
            arrayTotalImpuestos=new ArrayOfTotalImpuesto();
            
            int band=0;
            try{
                
                filtro="SELECT * FROM INVE_DOCUMENTOS_FE_DAT WHERE NUME_AUTO_INVE_DOCU IS NULL AND CODDOC='01' AND AMBIENTE=2 "
                        +" AND ESTAB="+arrayInfoDoc.get(i).getEstab()
                        +" AND PTOEMI="+arrayInfoDoc.get(i).getPtoEmi()
                        +" AND SECUENCIAL="+arrayInfoDoc.get(i).getSecuencial();
                st=con.getCon().createStatement();
                rs=st.executeQuery(filtro);
                while(rs.next()){
                    //inicio de cabecera
                    if(band==0){

                        //==================================== INFORMACION TRIBUTARIA ===========================
                        infoTributaria.setAmbiente(rs.getInt("AMBIENTE"));
                        infoTributaria.setCodDoc(rs.getString("CODDOC"));
                        infoTributaria.setDirMatriz(rs.getString("DIRMATRIZ"));
                        infoTributaria.setEstab(rs.getString("ESTAB"));
                        JAXBElement<String> mailCliente=factory.createInfoTributariaMailCliente(rs.getString("MAILCLIENTE"));
                        infoTributaria.setMailCliente((JAXBElement<String>) (mailCliente==null?"":mailCliente));
                        JAXBElement<String> nombreComercial = factory.createInfoTributariaNombreComercial(rs.getString("NOMBRECOMERCIAL"));
                        infoTributaria.setNombreComercial((JAXBElement<String>) (nombreComercial==null?" ":nombreComercial));
                        JAXBElement<String> origen=factory.createInfoTributariaOrigen(rs.getString("ORIGEN"));
                        infoTributaria.setOrigen((JAXBElement<String>) (origen==null?"":origen));
                        infoTributaria.setPtoEmi(rs.getString("PTOEMI"));
                        infoTributaria.setRazonSocial(rs.getString("RAZONSOCIAL"));
                        infoTributaria.setRuc(rs.getString("RUC"));
                        infoTributaria.setSecuencial(rs.getString("SECUENCIAL"));
                        infoTributaria.setTipoEmision(rs.getInt("TIPOEMISION"));
                        
                        
                        //====================================== INFORMACION DE FACTURA ===========================
                        JAXBElement<String> contribuyenteEspecial = factory.createInfoFacturaContribuyenteEspecial(rs.getString("CONTRIBUYENTEESPECIAL"));
                        infoFactura.setContribuyenteEspecial(contribuyenteEspecial);
                        infoFactura.setDirEstablecimiento(rs.getString("DIRESTABLECIMIENTO").replace(",", " "));
                        infoFactura.setFechaEmision(rs.getString("FECHAEMISION"));
                        JAXBElement<String> guiaRemision=factory.createInfoFacturaGuiaRemision(rs.getString("GUIAREMISION"));
                        infoFactura.setGuiaRemision((JAXBElement<String>) (guiaRemision==null?"":guiaRemision));
                        infoFactura.setIdentificacionComprador(rs.getString("IDENTIFICACIONCOMPRADOR"));
                        infoFactura.setImporteTotal(BigDecimal.valueOf(rs.getDouble("IMPORTETOTAL")));
                        JAXBElement<String> moneda=factory.createInfoFacturaMoneda(rs.getString("MONEDA"));
                        infoFactura.setMoneda((JAXBElement<String>) (moneda==null?"":moneda));
                        JAXBElement<String> obligadoContabilidad = factory.createInfoFacturaObligadoContabilidad(rs.getString("OBLIGADOCONTABILIDAD"));
                        infoFactura.setObligadoContabilidad((JAXBElement<String>) (obligadoContabilidad==null?"":obligadoContabilidad));
                        infoFactura.setPropina(BigDecimal.valueOf(rs.getDouble("PROPINA")));
                        infoFactura.setRazonSocialComprador(rs.getString("RAZONSOCIALCOMPRADOR").trim());
                        
                        String tipo_id_comp="";
                        if(rs.getInt("TIPOIDENTIFICACIONCOMPRADOR")<10)
                            tipo_id_comp="0"+rs.getString("TIPOIDENTIFICACIONCOMPRADOR");
                        else
                            tipo_id_comp=rs.getString("TIPOIDENTIFICACIONCOMPRADOR");
                        infoFactura.setTipoIdentificacionComprador(tipo_id_comp);
                        
                        //============================ TOTAL DE IMPUESTO DE LA FACTURA =================================
                        TotalImpuesto total_imp=new TotalImpuesto();
                        total_imp.setBaseImponible(BigDecimal.valueOf(arrayInfoDoc.get(i).getTotalSinImpuesto()));
                        total_imp.setCodigo(rs.getInt("CODIGO"));
                        total_imp.setCodigoPorcentaje(rs.getInt("CODIGOPORCENTAJE"));
                        JAXBElement<String> tarifa=factory.createTotalImpuestoTarifa(rs.getString("TARIFA"));
                        total_imp.setTarifa(tarifa);
                        //OJO CON ESTE CAMPO
                        //REPRESENTA EL VALOR DEL TOTAL DE LOS IMPUESTOS,
                        //EL SRI RETORNA ERROR SI SE ENVIA CON MAS DE 2 DECIMALES

                        total_imp.setValor(BigDecimal.valueOf(rs.getDouble("TOTALIVA")));
                        arrayTotalImpuestos.getTotalImpuesto().add(total_imp);

                        infoFactura.setTotalConImpuestos(arrayTotalImpuestos);
                        
                        infoFactura.setTotalDescuento(BigDecimal.valueOf(arrayInfoDoc.get(i).getTotalDescuento()));
                        infoFactura.setTotalSinImpuestos(BigDecimal.valueOf(arrayInfoDoc.get(i).getTotalSinImpuesto()));


                        //==================================== INFORMACION ADICIONAL =============================
                        InfoAdicional info_a1=new InfoAdicional();
                        JAXBElement<String> nombre=factory.createInfoAdicionalNombre("OBSERVACION");
                        info_a1.setNombre(nombre);
                        String obs=rs.getString("OBSERVACION")==null?"NO REGISTRADO":rs.getString("OBSERVACION").toUpperCase().trim();
                        if(obs!=null)
                        {
                            obs=obs.replaceAll("Á","A");
                            obs=obs.replaceAll("É","E");
                            obs=obs.replaceAll("Í","I");
                            obs=obs.replaceAll("Ó","O");
                            obs=obs.replaceAll("Ú","U");
                            obs=obs.replace("."," ");
//                            obs=obs.replace(",", "");
                            obs=obs.replaceAll("\n", " ");
//                            obs=obs.replace(":", "");
                            obs=obs.replaceAll("ñ", "n");
                            obs=obs.replaceAll("Ñ", "N");
                            obs=obs.replaceAll(",", " ");
                            obs=obs.replaceAll("-", " ");
//                            obs=obs.replace("\",  "");
                            obs=obs.replace("/",  " ");
                        }
                        JAXBElement<String> text=factory.createInfoAdicionalText(obs);
                        info_a1.setText(text);
                        
                        
                        InfoAdicional info_a2=new InfoAdicional();
                        nombre=factory.createInfoAdicionalNombre("CONTACTO");
                        info_a2.setNombre(nombre);
                        String contacto=rs.getString("CONTACTO")==null?"NO REGISTRADO":rs.getString("CONTACTO").toUpperCase().trim();
                        if(contacto!=null)
                        {
                            contacto=contacto.replace('Á','A');
                            contacto=contacto.replace('É','E');
                            contacto=contacto.replace('Í','I');
                            contacto=contacto.replace('Ó','O');
                            contacto=contacto.replace('Ú','U');
                            contacto=contacto.replace(".", "");
                            contacto=contacto.replace("-", " O ");
                            
                        }
                        text=factory.createInfoAdicionalText(contacto);
                        info_a2.setText(text);
                        
                        InfoAdicional info_a3=new InfoAdicional();
                        nombre=factory.createInfoAdicionalNombre("DIRECCION");
                        info_a3.setNombre(nombre);
                        text=factory.createInfoAdicionalText(rs.getString("DIRECCION").toUpperCase().replace(".", " ").replace("(", " ").replace(")", " ").trim());
                        info_a3.setText((JAXBElement<String>) (text==null?"NO REGISTRADO":text));
                        
                        InfoAdicional info_a4=new InfoAdicional();
                        nombre=factory.createInfoAdicionalNombre("EMAIL");
                        info_a4.setNombre(nombre);
                        text=factory.createInfoAdicionalText(rs.getString("MAILCLIENTE"));
                        info_a4.setText((JAXBElement<String>) (text==null?"NO REGISTRADO":text));
                        
                        InfoAdicional info_a5=new InfoAdicional();
                        nombre=factory.createInfoAdicionalNombre("FONO");
                        info_a5.setNombre(nombre);
                        String fono=rs.getString("FONO")==null?"NO REGISTRADO":rs.getString("FONO").trim();
                        if(fono!=null)
                        {
                            fono=fono.replace("(","");
                            fono=fono.replace(")","");
                            fono=fono.replaceAll("-","");
                        }
                        text=factory.createInfoAdicionalText(fono); 
                        info_a5.setText(text);
                        nombre=factory.createInfoAdicionalNombre("FONO_ESTAB");
                        InfoAdicional info_a6=new InfoAdicional();
                        info_a6.setNombre(nombre);
                        text=factory.createInfoAdicionalText(rs.getString("FONO_ESTAB").replaceAll("-","").trim());
                        info_a6.setText((JAXBElement<String>) (text==null?"NO REGISTRADO":text));
                        
                        
                        arrayInfoAdicional.getInfoAdicional().add(info_a1);
                        arrayInfoAdicional.getInfoAdicional().add(info_a2);
                        arrayInfoAdicional.getInfoAdicional().add(info_a3);
                        arrayInfoAdicional.getInfoAdicional().add(info_a4);
                        arrayInfoAdicional.getInfoAdicional().add(info_a5);
                        arrayInfoAdicional.getInfoAdicional().add(info_a6);

                    }                    
                      
                    //========================= DATOS DEL DETALLE DE LA FACTURA ============================
                    Detalle detalle=new Detalle();
                    detalle.setCantidad(BigDecimal.valueOf(rs.getDouble("CANTIDAD")));
                    JAXBElement<String> codigoAuxiliar=factory.createDetalleCodigoAuxiliar(rs.getString("CODIGOAUXILIAR").replace("-",""));
                    detalle.setCodigoAuxiliar((JAXBElement<String>) (codigoAuxiliar==null?"":codigoAuxiliar));
                    detalle.setCodigoPrincipal(rs.getString("CODIGOPRINCIPAL")==null?"":rs.getString("CODIGOPRINCIPAL"));
                    detalle.setDescripcion(rs.getString("DESCRIPCION")==null?"":rs.getString("DESCRIPCION"));
                    detalle.setDescuento(BigDecimal.valueOf(rs.getDouble("DESCUENTO")));

                        Impuesto imp=new Impuesto();
                        imp.setBaseImponible(BigDecimal.valueOf(rs.getDouble("BASEIMPONIBLE_IMP")));
                        imp.setCodigo(rs.getInt("CODIGO_IMP"));
                        imp.setCodigoPorcentaje(rs.getInt("CODIGOPORCENTAJE_IMP"));
                        imp.setTarifa(rs.getInt("TARIFA_IMP"));
                        imp.setValor(BigDecimal.valueOf(rs.getDouble("VALOR_IMP")));

                        ArrayOfImpuesto array_of_impuesto=new ArrayOfImpuesto();
                        array_of_impuesto.getImpuesto().add(imp);

                    detalle.setImpuestos(array_of_impuesto);
                    detalle.setPrecioTotalSinImpuesto(BigDecimal.valueOf(rs.getDouble("PRECIOTOTALSINIMPUESTO")));
                    detalle.setPrecioUnitario(BigDecimal.valueOf(rs.getDouble("PRECIOUNITARIO")));
                    
                    DetAdicional detAdicional=new DetAdicional();
                    JAXBElement<String> nombre=factory.createDetAdicionalNombre(rs.getString("NOMBRE_ADIC"));
                    detAdicional.setNombre((JAXBElement<String>) (nombre==null?"NINGUNO":nombre));
                    JAXBElement<String> valor=factory.createDetAdicionalValor(rs.getString("VALOR_ADIC"));
                    detAdicional.setValor((JAXBElement<String>) (valor==null?"NINGUNO":valor));
                    
                    ArrayOfDetAdicional arrayDetalleAdicional=new ArrayOfDetAdicional();
                    arrayDetalleAdicional.getDetAdicional().add(detAdicional);
                    JAXBElement<ArrayOfDetAdicional> detallesAdicionales=factory.createArrayOfDetAdicional(arrayDetalleAdicional);
                    detalle.setDetallesAdicionales(detallesAdicionales);
                    
                    arrayDetalle.getDetalle().add(detalle);

                    band++;
                }
            }
            catch(SQLException e)
            {
                log.error("Error al empaquetar el documento. "+e.getMessage());
                this.frmMonitor.setMensajeFacturas("[error] - Error al empaquetar el documento. "+e.getMessage());}
            finally
            {
                rs.close();
                st.close();
            }
            
            System.out.println("[info] - FACTURA "+arrayInfoDoc.get(i).getEstab()+"-"+arrayInfoDoc.get(i).getPtoEmi()+"-"+arrayInfoDoc.get(i).getSecuencial());
            this.frmMonitor.setMensajeFacturas("[info] - FACTURA "+arrayInfoDoc.get(i).getEstab()+"-"+arrayInfoDoc.get(i).getPtoEmi()+"-"+arrayInfoDoc.get(i).getSecuencial());
            AutorizarFactura autorizar=new AutorizarFactura();
            JAXBElement<InfoTributaria> jbInfoTrib=factory.createAutorizarFacturaInfoTributaria(infoTributaria);
            autorizar.setInfoTributaria(jbInfoTrib);
            JAXBElement<InfoFactura> jbInfoFactura=factory.createAutorizarFacturaInfoFactura(infoFactura);
            autorizar.setInfoFactura(jbInfoFactura);
            JAXBElement<ArrayOfDetalle> jbDetalle=factory.createArrayOfDetalle(arrayDetalle);
            autorizar.setDetalle(jbDetalle);
            JAXBElement<ArrayOfInfoAdicional> jbInfoAdicional=factory.createArrayOfInfoAdicional(arrayInfoAdicional);
            autorizar.setInfoAdicional(jbInfoAdicional);

            //generando el xml
            generarXML(autorizar,arrayInfoDoc.get(i).getEstab(),arrayInfoDoc.get(i).getPtoEmi(),arrayInfoDoc.get(i).getSecuencial());
            
            
            arrayAutorizarFactura.add(autorizar);
            }//final del for de empaquetado
            
            start = 0;
            stop = 0;
            respuesta=null;
            //Enviar documento empaquetado al webservice de SRI para autorizar
            for(int i=0;i<arrayAutorizarFactura.size();i++){
                System.out.println("[info] - Registro #"+(i+1)+ " de "+arrayAutorizarFactura.size());
                this.frmMonitor.setMensajeFacturas("[info] - Registro #"+(i+1)+ " de "+arrayAutorizarFactura.size());
            
                System.out.println("[info] - No. Lineas : "+arrayInfoDoc.get(i).getLineas());
                this.frmMonitor.setMensajeFacturas("[info] - No. Lineas : "+arrayInfoDoc.get(i).getLineas());
                System.out.println("[info] - Enviando petición de autorización al WS...");
                this.frmMonitor.setMensajeFacturas("[info] - Enviando petición de autorización al WS...");
                
                //obteniendo el tiempo inicial para el tiempo de espera estimado
                start = Calendar.getInstance().getTimeInMillis();      
                
                //Instancia del servicio de INTEME
                //El objeto Response encapsula la información del documento autorizado o no autorizado
                respuesta=autorizarFactura(arrayAutorizarFactura.get(i).getInfoTributaria().getValue()
                        ,arrayAutorizarFactura.get(i).getInfoFactura().getValue()
                        ,arrayAutorizarFactura.get(i).getDetalle().getValue()
                        ,arrayAutorizarFactura.get(i).getInfoAdicional().getValue());
                
                //obteniendo el tiempo final para el tiempo de espera estimado
                stop = Calendar.getInstance().getTimeInMillis();
                System.out.println("[info] - Tiempo de respuesta: "+(stop-start)+" miliseg");
                this.frmMonitor.setMensajeFacturas("[info] - Tiempo de respuesta: "+(stop-start)+" miliseg");
                
                enviadas++;
                
                System.out.println(marco);
                this.frmMonitor.setMensajeFacturas(marco);
                System.out.println("No. de autorización: "+respuesta.getAutorizacion().getValue());
                this.frmMonitor.setMensajeFacturas("No. de autorización: "+respuesta.getAutorizacion().getValue());
                System.out.println("Clave de acceso: "+respuesta.getClaveAcceso().getValue());
                this.frmMonitor.setMensajeFacturas("Clave de acceso: "+respuesta.getClaveAcceso().getValue());
                System.out.println("Fecha Autorización: "+respuesta.getFechaAutorizacion().getValue());
                this.frmMonitor.setMensajeFacturas("Fecha Autorización: "+respuesta.getFechaAutorizacion().getValue());
                System.out.println("Id. Error: "+respuesta.getIdError().getValue());
                this.frmMonitor.setMensajeFacturas("Id. Error: "+respuesta.getIdError().getValue());
                System.out.println("Origen: "+respuesta.getOrigen().getValue());
                this.frmMonitor.setMensajeFacturas("Origen: "+respuesta.getOrigen().getValue());
                System.out.println("Result: "+respuesta.getResult().getValue());
                this.frmMonitor.setMensajeFacturas("Result: "+respuesta.getResult().getValue());
                System.out.println("Result Data: "+respuesta.getResultData().getValue());
                this.frmMonitor.setMensajeFacturas("Result Data: "+respuesta.getResultData().getValue());
                System.out.println(marco);
                this.frmMonitor.setMensajeFacturas(marco);
                
                    if(respuesta.getAutorizacion().getValue()!=null)
                    {               
                        //Llamada del metodo para actualizar registro
                        this.frmMonitor.setMensajeFacturas("[info] - Actualizando registros...");
                        System.out.println("[info] - Actualizando registros...");
                        int reg=actualizarFactura(con, respuesta,arrayAutorizarFactura.get(i).getInfoTributaria().getValue());
                        System.out.println("[info] - Registros actualizados : "+reg);
                        this.frmMonitor.setMensajeFacturas("[info] - Registros actualizados : "+reg);
                    }
                this.frmMonitor.setMensajeFacturas("[info] - Registrando en el log...");
                System.out.println("[info] - Registrando en el log...");
                //llamada del metodo para el registro del log
                notificarResultado(con, respuesta,arrayAutorizarFactura.get(i).getInfoTributaria().getValue(),String.valueOf((stop-start)));
                this.frmMonitor.setMensajeFacturas("[info] - Evento capturado en el historial");
                System.out.println("[info] - Evento capturado en el historial");
            
            }//final del FOR de envío
        }
        catch(Exception ex)
        {
            this.frmMonitor.setMensajeFacturas("[error] - Error general al enviar a autorizar");
            log.error("Error general al enviar a autorizar");
        }
        finally
        {
            this.frmMonitor.setMensajeFacturas("[info] - Cancelando envío...");
            System.out.println("[info] - Cancelando envío...");
        }
        return enviadas;
    }

    public int actualizarFactura(ConexionBD con,Response autorizacion,InfoTributaria info ){
       
        int result=0;
        String update="UPDATE INVE_DOCUMENTOS_FE_DAT SET NUME_AUTO_INVE_DOCU=? "
                +"WHERE CODDOC='01' "
                +"AND AMBIENTE="+frmMonitor.getServicio().getAmbiente()
                +" AND ESTAB="+info.getEstab()
                +" AND PTOEMI="+info.getPtoEmi()
                +" AND SECUENCIAL="+info.getSecuencial() ;
        PreparedStatement ps =null;
       
        try
        {
            ps = con.getCon().prepareStatement(update);
            ps.setString(1, autorizacion.getAutorizacion().getValue());
            result=ps.executeUpdate();
        
        }
        catch(SQLException sqle)
        {
            this.frmMonitor.setMensajeFacturas("[error] - Error al actualizar registros");
            log.error("Error al actualizar registros");
        }
        finally
        {
            if(ps!=null)
            {
                try
                {
                    ps.close();
                }
                catch(SQLException e){
                log.error("Error al cerrar PreparedStatement");}
            }
        }
        
        
    return result;
    
    }
    
    public void generarXML(AutorizarFactura autorizar,String estab, String ptoEmi,String secuencial){
    
        Marshaller m=null;
        String rutaXml=null;
        JAXBElement<AutorizarFactura> jaxb_autoriza=null;
        JAXBContext jaxbContext=null;
        try{
            System.out.println("[info] - Generando xml...");  
            this.frmMonitor.setMensajeFacturas("[info] - Generando xml...");
 
            jaxb_autoriza=new JAXBElement(new QName(AutorizarFactura.class.getSimpleName()),AutorizarFactura.class,autorizar);
            jaxbContext=JAXBContext.newInstance(AutorizarFactura.class);
            m=jaxbContext.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
            rutaXml=this.frmMonitor.getServicio().getDirectorioFacturas()+"AutorizarFactura"+estab+"-"+ptoEmi+"-"+secuencial+".xml";
            m.marshal(jaxb_autoriza, new File (rutaXml)); 

            System.out.println("[info] - xml generado "+rutaXml);  
            this.frmMonitor.setMensajeFacturas("[info] - xml generado "+rutaXml);
    
    }
    catch(JAXBException ex){
        log.error("Error al generar xml");  
            this.frmMonitor.setMensajeFacturas("[error] - Error al generar xml");}
    finally{}
    
    }
    
    private int notificarResultado(ConexionBD con,Response resp,InfoTributaria info,String ts){
        
        int n=0;
        String insert="INSERT INTO INVE_LOG_FE_DAT(COD_DOC,NUM_DOC,NUM_AUTORIZACION,CLAVE_ACCESO,MENSAJE_DEVUELTO,TIEMPO_RESPUESTA,AMBIENTE)"+
            "VALUES(?,?,?,?,?,?,?)";
        PreparedStatement ps=null;
        try
        {
            ps=con.getCon().prepareStatement(insert);
            ps.setString(1,info.getCodDoc());
            ps.setString(2,info.getEstab()+"-"+info.getPtoEmi()+"-"+info.getSecuencial());
            ps.setString(3,resp.getAutorizacion().getValue());
            ps.setString(4,resp.getClaveAcceso().getValue());
    //        ps.setDate(5, new java.sql.Date( new java.util.Date().getTime()));
            ps.setString(5,resp.getResult().getValue()+"\n"+resp.getResultData().getValue());
            ps.setString(6,ts);
            ps.setInt(7,2);

    //        String sp="CALL MAIL_FILES(administrador.tevcol,michael.beltran@tevcol.com.ec,?,null,null,null,null)";
    //        CallableStatement call=con.getCon().prepareCall(sp);
    //        String  mensaje="Se ha generado Autorización del SRI:\nNo. Autorización: "+respuesta.getAutorizacion().getValue()
    //                +"\nClave de Acceso: "+respuesta.getClaveAcceso().getValue()
    //                +"\nFecha Autorización: "+respuesta.getFechaAutorizacion().getValue()
    //                +"\nResult: "+respuesta.getResult().getValue()
    //                +"\nResult Data: "+respuesta.getResultData().getValue();
    //        
    //        call.setString(1,mensaje);
    //  
    //        int n=call.executeUpdate();
            n=ps.executeUpdate();
        }
        catch(SQLException ex){log.error("Error al insertar registros");}
        finally
        {
            if(ps!=null)
            {
                try
                {
                    ps.close();
                }
                catch(SQLException e){
                log.error("Error al cerrar PreparedStatement");}
            }
        }
        return n;
    }
    
    private int notificarError(ConexionBD con, String ex,InfoTributaria info,String ts){
        
        int n=0;
        String insert="INSERT INTO INVE_LOG_FE_DAT(COD_DOC,NUM_DOC,MENSAJE_DEVUELTO,TIEMPO_RESPUESTA,AMBIENTE)"+
            "VALUES(?,?,?,?,?)";
        PreparedStatement ps=null;
        
        try
        {
        ps=con.getCon().prepareStatement(insert);
        ps.setString(1,info.getCodDoc());
        ps.setString(2,info.getEstab()+"-"+info.getPtoEmi()+"-"+info.getSecuencial());
        ps.setString(3,ex);
        ps.setString(4,ts);
        ps.setInt(5,2);

        n=ps.executeUpdate();
        }
        catch(SQLException sqle)
        {
            this.frmMonitor.setMensajeFacturas("[error] - Error al insertar registros");
            log.error("Error al insertar registros");
        }
        finally
        {
            if(ps!=null)
            {
                try
                {
                    ps.close();
                }
                catch(SQLException e){
                log.error("Error al cerrar PreparedStatement");}
            }
        }
        
        return n;

    }
    
    public int cambiaEstado(ConexionBD con,String estado,int atendiendo) {
    
        int result=0;
        String update="UPDATE INVE_INFO_FE_DAT SET ESTATUS=?,USUARIO_ACT=?,ULT_EJECUCION=?,HOST_ACT=?,ATENDIENDO=? WHERE NOMBRE='HILO FACTURAS'";
        PreparedStatement ps = null;
        InetAddress localHost =null;
        try
        {
            ps = con.getCon().prepareStatement(update);
            ps.setString(1, estado);
            ps.setString(2, System.getProperty("user.name"));

            java.util.Date now = new java.util.Date();
            ps.setDate(3,new java.sql.Date(now.getTime()));
            localHost= InetAddress.getLocalHost();
            ps.setString(4,localHost.getHostName());
            ps.setInt(5, atendiendo);

            result=ps.executeUpdate();
            
        }
        catch(SQLException sqle)
        {
            log.error("Error al actualizar estado del proceso");
        }
        catch(UnknownHostException uhe)
        {
            log.error("Error al recuperar InetAddress");
        }
        finally
        {
            if(ps!=null)
            {
                try
                {
                    ps.close();
                }
                catch(SQLException e){
                log.error("Error al cerrar PreparedStatement");}
            }
        }
    return result;
    }

    private Response autorizarFactura(InfoTributaria infoTributaria, InfoFactura infoFactura, ArrayOfDetalle detalle,ArrayOfInfoAdicional infoAdicional) {
        Response respuesta = null;
        CloudAutorizarComprobante service = null;
        IcloudAutorizarComprobante port = null;
        try 
        {            
            service = new CloudAutorizarComprobante();
            port = service.getBasicHttpBindingIcloudAutorizarComprobante();           
            respuesta = port.autorizarFactura(infoTributaria, infoFactura, detalle, infoAdicional);            
        } 
        catch (Exception e) 
        {
            System.err.println("[error] - Error al invocar webservice");
            this.frmMonitor.setMensajeFacturas("[error] - Error al invocar webservice");
        }
        finally
        {
            service = null;
            port = null;      
        }

        return respuesta;
        
    }
    
    public frmMonitor getMonitor() {
        return frmMonitor;
    }

    public void setMonitor(frmMonitor monitor) {
        this.frmMonitor = monitor;
    }
    
}
