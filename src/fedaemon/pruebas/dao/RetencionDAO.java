

package fedaemon.pruebas.dao;


import fedaemon.pruebas.frms.frmMonitor;
import fedaemon.infact.pruebas.ArrayOfImpuestosRetencion;
import fedaemon.infact.pruebas.ArrayOfInfoAdicional;
import fedaemon.infact.pruebas.AutorizarComprobanteRetencion;
import fedaemon.infact.pruebas.CloudAutorizarComprobante;
import fedaemon.infact.pruebas.IcloudAutorizarComprobante;
import fedaemon.infact.pruebas.ImpuestosRetencion;
import fedaemon.infact.pruebas.InfoAdicional;
import fedaemon.infact.pruebas.InfoCompRetencion;
import fedaemon.infact.pruebas.InfoTributaria;
import fedaemon.infact.pruebas.ObjectFactory;
import fedaemon.infact.pruebas.Response;
import fedaemon.pruebas.util.ConexionBD;
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
 * @author Michael Beltrán
 */
public final class RetencionDAO {
    
    protected frmMonitor frmMonitor;
    private final static Logger log = Logger.getLogger(FacturaDAO.class);
    
    public RetencionDAO()
    {
        PropertyConfigurator.configure("log4j.properties");
    }
    
    //metodo para escanear la base
    public int consultarRetencionPendiente(ConexionBD con){
    int result=0;
    String select="SELECT COUNT(*) "
            + "FROM INVE_RETENCIONES_FE_DAT "
            + "WHERE NUME_AUTO_INVE_RETE IS NULL "
            + "AND CODDOC='07' "
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
    catch(SQLException ex){log.error("error de ResultSet de consultarRetencionesPendientes");}
    finally
        {
            
          try
          {
             if(rs!=null)
                rs.close();
          }catch(SQLException se2)
          {
              log.error("error de cerrar ResultSet de consultarRetencionPendiente");
          } 
        }
     return result;
    }

    public int consultarRetencionPendiente(ConexionBD con,String coddoc,String ambiente) {
        int result=0;
        String sentencia="{call SP_FACTCONSULTAPENDIENTES(?,?,?)}";
        CallableStatement cs=null;
        try
        {
            cs=con.getCon().prepareCall(sentencia);

            cs.setString(1, coddoc);
            cs.setString(2,ambiente);
            cs.registerOutParameter(3, java.sql.Types.NUMERIC);

            cs.execute();

            result=cs.getInt(3);
        }
        catch(SQLException ex)
        {log.error("error de CallableStatement de consultarRetencionPendiente");}
        finally
        {
            try{
             if(cs!=null)
                cs.close();
          }
            catch(SQLException se2)
          {
              log.error("error de cerrar CAllableStatement de consultarRetencionesPendientes");
          }
        }

        return result;
    
    }
    
    public int enviarRetenciones(ConexionBD con){
        
        int enviadas=0;
        ObjectFactory factory=new ObjectFactory();
        String marco="============================================================================";
        //OJO que al consultar data de la base se recuperará info como estaba hasta el ultimo COMMIT ejecutado
        String select="SELECT COUNT(*),ESTAB,PTOEMI,SECUENCIAL,FECHAEMISION "
                + "FROM INVE_RETENCIONES_FE_DAT "
                + "WHERE CODDOC='07' "
                + "AND NUME_AUTO_INVE_RETE IS NULL "
                + "AND AMBIENTE=2 "
                + "GROUP BY ESTAB,PTOEMI,SECUENCIAL,FECHAEMISION "
                + "ORDER BY FECHAEMISION ASC,SECUENCIAL ASC";
        String filtro=null;
        Statement st= null;
        ResultSet rs=null;
        InfoTributaria infoTrib=null;
        ArrayList<InfoTributaria> arrayInfoTrib=null;
        ArrayList<AutorizarComprobanteRetencion> arrayAutorizaComprobante=null;
        InfoTributaria info_t=null;
        InfoCompRetencion info_comp=null;
        ArrayOfImpuestosRetencion array_impuestos=null;
        ArrayOfInfoAdicional array_info_a=null;
        long start=0;
        long stop = 0;
        Response respuesta=null;
        try{
            
            arrayInfoTrib=new ArrayList<>();
            arrayAutorizaComprobante=new ArrayList<>();
            st= con.getCon().createStatement();
            rs=st.executeQuery(select);
            while(rs.next())
            {   
                infoTrib=new InfoTributaria();
                infoTrib.setEstab(rs.getString("ESTAB"));
                infoTrib.setPtoEmi(rs.getString("PTOEMI"));
                infoTrib.setSecuencial(rs.getString("SECUENCIAL"));
                arrayInfoTrib.add(infoTrib);
            }
            rs.close();
            st.close();
        
            for(int i=0;i<arrayInfoTrib.size();i++){
            this.frmMonitor.limpiaRetenciones();
            System.out.println("[info] - Registro #"+(i+1)+ " de "+arrayInfoTrib.size());
            this.frmMonitor.setMensajeRetenciones("[info] - Registro #"+(i+1)+ " de "+arrayInfoTrib.size());
            info_t=new InfoTributaria();
            info_comp=new InfoCompRetencion();
            array_impuestos=new ArrayOfImpuestosRetencion();
            array_info_a=new ArrayOfInfoAdicional();
            
            int band=0;
            try{
                    st=con.getCon().createStatement();
                    filtro="SELECT * FROM INVE_RETENCIONES_FE_DAT WHERE NUME_AUTO_INVE_RETE IS NULL AND CODDOC='07' AND AMBIENTE=2 "
//                            + "AND CODI_ADMI_EMPR_FINA='00001' AND CODI_ADMI_PUNT_VENT='101'""SELECT * FROM INVE_RETENCIONES_FE_DAT WHERE NUME_AUTO_INVE_RETE IS NULL AND CODDOC='07' AND AMBIENTE=2 "
//                            + "AND CODI_ADMI_EMPR_FINA='00001' AND CODI_ADMI_PUNT_VENT='101'"
                                +" AND ESTAB="+arrayInfoTrib.get(i).getEstab()
                                +" AND PTOEMI="+arrayInfoTrib.get(i).getPtoEmi()
                                +" AND SECUENCIAL="+arrayInfoTrib.get(i).getSecuencial();
                    rs=st.executeQuery(filtro);
                    while(rs.next())
                    {
                        //inicio de la cabecera
                        if(band==0)
                        {
                            //======================  INFORMACION TRIBUTARIA ===========================================
                            info_t.setAmbiente(rs.getInt("AMBIENTE"));
                            info_t.setCodDoc(rs.getString("CODDOC"));
                            info_t.setDirMatriz(rs.getString("DIRMATRIZ"));
                            info_t.setEstab(rs.getString("ESTAB"));
                            JAXBElement<String> mailCliente=factory.createInfoTributariaMailCliente(rs.getString("MAILCLIENTE"));
                            info_t.setMailCliente(mailCliente);
                            JAXBElement<String> nombreComercial=factory.createInfoTributariaNombreComercial(rs.getString("NOMBRECOMERCIAL"));
                            info_t.setNombreComercial(nombreComercial);
                            JAXBElement<String> origen=factory.createInfoTributariaOrigen(rs.getString("ORIGEN"));
                            info_t.setOrigen((JAXBElement<String>) (origen==null?"":origen));
                            info_t.setPtoEmi(rs.getString("PTOEMI"));
                            info_t.setRazonSocial(rs.getString("RAZONSOCIAL"));
                            info_t.setRuc(rs.getString("RUC"));
                            info_t.setSecuencial(rs.getString("SECUENCIAL"));
                            info_t.setTipoEmision(rs.getInt("TIPOEMISION"));

                            //======================= INFORMACION DEL COMPROBANTE ==========================================
                            JAXBElement<String> contribuyenteEspecial=factory.createInfoCompRetencionContribuyenteEspecial(rs.getString("CONTRIBUYENTEESPECIAL"));
                            info_comp.setContribuyenteEspecial(contribuyenteEspecial);
                            JAXBElement<String> dirEstablecimiento=factory.createInfoCompRetencionDirEstablecimiento(rs.getString("DIRESTABLECIMIENTO"));
                            info_comp.setDirEstablecimiento(dirEstablecimiento);
                            info_comp.setFechaEmision(rs.getString("FECHAEMISION"));
                            info_comp.setIdentificacionSujetoRetenido(rs.getString("IDENTIFICACIONSUJETORETENIDO"));
                            JAXBElement<String> obligadoContabilidad=factory.createInfoCompRetencionObligadoContabilidad(rs.getString("OBLIGADOCONTABILIDAD"));
                            info_comp.setObligadoContabilidad(obligadoContabilidad);
                            info_comp.setPeriodoFiscal(rs.getString("PERIODOFISCAL"));
                            info_comp.setRazonSocialSujetoRetenido(rs.getString("RAZONSOCIALSUJETORETENIDO"));
                            info_comp.setTipoIdentificacionSujetoRetenido(rs.getString("TIPOIDENTIFICACIONSUJETORETE"));
                            
                            
                            //======================= INFORMACION ADICIONAL==========================================
                            InfoAdicional info_a1=new InfoAdicional();
                            JAXBElement<String> nombre=factory.createInfoAdicionalNombre("OBSERVACION");
                            info_a1.setNombre(nombre);
                            String obs=rs.getString("OBSERVACION")==null?"NO REGISTRADO":rs.getString("OBSERVACION").toUpperCase().trim();
                            if(obs!=null)
                            {
                                obs=obs.replace('Á','A');
                                obs=obs.replace('É','E');
                                obs=obs.replace('Í','I');
                                obs=obs.replace('Ó','O');
                                obs=obs.replace('Ú','U');
    //                            obs=obs.replace(".", "");
    //                            obs=obs.replace(",", "");
                                obs=obs.replace("\n", "");
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
                            }
                            text=factory.createInfoAdicionalText(contacto);
                            info_a2.setText(text);
                        
                            InfoAdicional info_a3=new InfoAdicional();
                            nombre=factory.createInfoAdicionalNombre("DIRECCION");
                            info_a3.setNombre(nombre);
                            text=factory.createInfoAdicionalText(rs.getString("DIRECCION").toUpperCase().trim());
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
                            }
                            text=factory.createInfoAdicionalText(fono);
                            info_a5.setText(text);
                        
                            InfoAdicional info_a6=new InfoAdicional();
                            nombre=factory.createInfoAdicionalNombre("FONO_ESTAB");
                            info_a6.setNombre(nombre);
                            text=factory.createInfoAdicionalText(rs.getString("FONO_ESTAB").trim());
                            info_a6.setText((JAXBElement<String>) (text==null?"NO REGISTRADO":text));
                            
                            if(rs.getString("OBSERVACION")!=null)
                                array_info_a.getInfoAdicional().add(info_a1);
                            if(rs.getString("CONTACTO")!=null)
                                array_info_a.getInfoAdicional().add(info_a2);
                            if(rs.getString("DIRECCION")!=null)
                                array_info_a.getInfoAdicional().add(info_a3);
                            if(rs.getString("MAILCLIENTE")!=null)
                                array_info_a.getInfoAdicional().add(info_a4);
                            if(rs.getString("FONO")!=null)
                                array_info_a.getInfoAdicional().add(info_a5);
                            if(rs.getString("FONO_ESTAB")!=null)
                                array_info_a.getInfoAdicional().add(info_a6);
                        
                        }

                            //========================== INFORMACION DE IMPUESTOS =======================================
                             ImpuestosRetencion impuestos=new ImpuestosRetencion();
                             impuestos.setBaseImponible(BigDecimal.valueOf(rs.getDouble("BASEIMPONIBLE")));
                             impuestos.setCodDocSustento(rs.getString("CODDOCSUSTENTO"));
                             impuestos.setCodigo(Integer.parseInt(rs.getString("CODIGO")));
                             impuestos.setCodigoRetencion(rs.getString("CODIGORETENCION"));
                             JAXBElement<String> fechaEmisionDocSustento=factory.createImpuestosRetencionFechaEmisionDocSustento(rs.getString("FECHAEMISIONDOCSUSTENTO"));
                             impuestos.setFechaEmisionDocSustento(fechaEmisionDocSustento);
                             JAXBElement<String> numDocSustento=factory.createImpuestosRetencionNumDocSustento(rs.getString("NUMDOCSUSTENTO"));
                             impuestos.setNumDocSustento(numDocSustento);
                             impuestos.setPorcentajeRetener(rs.getInt("PORCENTAJERETENER"));
                             impuestos.setValorRetenido(BigDecimal.valueOf(rs.getDouble("VALORRETENIDO")));
                            
                            array_impuestos.getImpuestosRetencion().add(impuestos);

                        band++;
                    }//final del while
                }
                catch(SQLException e)
                {
                    log.error("Error al empaquetar el documento. "+e.getMessage());
                    this.frmMonitor.setMensajeRetenciones("[error] - Error al empaquetar el documento. "+e.getMessage());
                }
                finally{
                    rs.close();
                    st.close();
                }
         
                 //=============================Formando el xml... ====================================
                System.out.println("[info] - COMPROBANTE RETENCION "+arrayInfoTrib.get(i).getEstab()+"-"+arrayInfoTrib.get(i).getPtoEmi()+"-"+arrayInfoTrib.get(i).getSecuencial());
                this.frmMonitor.setMensajeRetenciones("[info] - COMPROBANTE RETENCION "+arrayInfoTrib.get(i).getEstab()+"-"+arrayInfoTrib.get(i).getPtoEmi()+"-"+arrayInfoTrib.get(i).getSecuencial());
                AutorizarComprobanteRetencion autorizar=new AutorizarComprobanteRetencion();
                JAXBElement<InfoTributaria> jbInfoTributaria=factory.createAutorizarComprobanteRetencionInfoTributaria(info_t);
                autorizar.setInfoTributaria(jbInfoTributaria);
                JAXBElement<InfoCompRetencion> jbInfoComprobanteRetencion=factory.createAutorizarComprobanteRetencionInfoCompRetencion(info_comp);
                autorizar.setInfoCompRetencion(jbInfoComprobanteRetencion);
                JAXBElement<ArrayOfImpuestosRetencion> jbImpuestosRetencion=factory.createAutorizarComprobanteRetencionImpuestos(array_impuestos);
                autorizar.setImpuestos(jbImpuestosRetencion);
                JAXBElement<ArrayOfInfoAdicional> jbInfoAdicional=factory.createAutorizarComprobanteRetencionInfoAdicional(array_info_a);
                autorizar.setInfoAdicional(jbInfoAdicional);

                generarXML(autorizar,arrayInfoTrib.get(i).getEstab(),arrayInfoTrib.get(i).getPtoEmi(),arrayInfoTrib.get(i).getSecuencial());
                
                arrayAutorizaComprobante.add(autorizar);
            
            }//final del for de empaquetado
        
            start=0;
            stop = 0;
            respuesta=null;
             //Enviar documento empaquetado al webservice de SRI para autorizar
            for(int i=0;i<arrayAutorizaComprobante.size();i++){
                System.out.println("[info] - Registro #"+(i+1)+ " de "+arrayAutorizaComprobante.size());
                this.frmMonitor.setMensajeRetenciones("[info] - Registro #"+(i+1)+ " de "+arrayAutorizaComprobante.size());
            
                System.out.println("[info] - Enviando petición de autorización al WS...");
                this.frmMonitor.setMensajeRetenciones("[info] - Enviando petición de autorización al WS...");
                //obteniendo el tiempo inicial para el tiempo de espera estimado
                start = Calendar.getInstance().getTimeInMillis();
                
                //Instancia del servicio de INTEME
                //El objeto Response encapsula la información del documento autorizado o no autorizado
                
                respuesta=autorizarComprobanteRetencion(arrayAutorizaComprobante.get(i).getInfoTributaria().getValue()
                        ,arrayAutorizaComprobante.get(i).getInfoCompRetencion().getValue()
                        ,arrayAutorizaComprobante.get(i).getImpuestos().getValue()
                        ,arrayAutorizaComprobante.get(i).getInfoAdicional().getValue());
                
                 //obteniendo el tiempo final para el tiempo de espera estimado
                stop = Calendar.getInstance().getTimeInMillis();
                System.out.println("[info] - Tiempo de respuesta: "+(stop-start)+" miliseg");
//                this.frmMonitor.setMensajeRetenciones("Tiempo de respuesta: "+d.getSeconds()+" seg");
                this.frmMonitor.setMensajeRetenciones("[info] - Tiempo de respuesta: "+(stop-start)+" miliseg");
                
                enviadas++;

                System.out.println(marco);
                this.frmMonitor.setMensajeRetenciones(marco);
                System.out.println("No. de autorización: "+respuesta.getAutorizacion().getValue());
                this.frmMonitor.setMensajeRetenciones("No. de autorización: "+respuesta.getAutorizacion().getValue());
                System.out.println("Clave de acceso: "+respuesta.getClaveAcceso().getValue());
                this.frmMonitor.setMensajeRetenciones("Clave de acceso: "+respuesta.getClaveAcceso().getValue());
                System.out.println("Fecha Autorización: "+respuesta.getFechaAutorizacion().getValue());
                this.frmMonitor.setMensajeRetenciones("Fecha Autorización: "+respuesta.getFechaAutorizacion().getValue());
                System.out.println("Id. Error: "+respuesta.getIdError().getValue());
                this.frmMonitor.setMensajeRetenciones("Id. Error: "+respuesta.getIdError().getValue());
                System.out.println("Origen: "+respuesta.getOrigen().getValue());
                this.frmMonitor.setMensajeRetenciones("Origen: "+respuesta.getOrigen().getValue());
                System.out.println("Result: "+respuesta.getResult().getValue());
                this.frmMonitor.setMensajeRetenciones("Result: "+respuesta.getResult().getValue());
                System.out.println("Result Data: "+respuesta.getResultData().getValue());
                this.frmMonitor.setMensajeRetenciones("Result Data: "+respuesta.getResultData().getValue());
                System.out.println(marco);
                this.frmMonitor.setMensajeRetenciones(marco);
                
                if(respuesta.getAutorizacion().getValue()!=null)
                {
                    this.frmMonitor.setMensajeRetenciones("[info] - Actualizando registros...");
                    System.out.println("[info] - Actualizando registros...");
                    //llamada del metodo para actualizar registro
                    int reg=actualizarRetencion(con, respuesta,info_t);
                    System.out.println("[info] - Registros actualizados : "+reg);
                    this.frmMonitor.setMensajeRetenciones("[info] - Registros actualizados : "+reg);
                     
                }
                this.frmMonitor.setMensajeRetenciones("[info] - Registrando en el log...");
                System.out.println("[info] - Registrando en el log...");
                //llamada del metodo para el registro del log
                notificarResultado(con, respuesta,info_t,String.valueOf((stop-start)));
                this.frmMonitor.setMensajeRetenciones("[info] - Evento capturado en el historial");
                System.out.println("[info] - Evento capturado en el historial"); 

            }//Final del FOR de envío
        }
        catch(SQLException | NumberFormatException ex)
        {
            this.frmMonitor.setMensajeRetenciones("[error] - Error general al enviar a autorizar");
            log.error("Error general al enviar a autorizar");
        }
        finally
        {
            this.frmMonitor.setMensajeRetenciones("[info] - Cancelando envío...");
            System.out.println("[info] - Cancelando envío...");
        }
        return enviadas;
    }
    
    public void generarXML(AutorizarComprobanteRetencion autorizar,String estab,String ptoEmi,String secuencial){

        Marshaller m;
        String rutaXml=null;
        JAXBElement<AutorizarComprobanteRetencion> jaxb_autoriza=null;
        JAXBContext jaxb_context=null;

        System.out.println("[info] - Generando xml...");  
        this.frmMonitor.setMensajeRetenciones("[info] - Generando xml...");
        try{
        jaxb_autoriza=new JAXBElement(new QName(AutorizarComprobanteRetencion.class.getSimpleName()),AutorizarComprobanteRetencion.class,autorizar);
        jaxb_context=JAXBContext.newInstance(AutorizarComprobanteRetencion.class);
        m=jaxb_context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
        rutaXml=this.frmMonitor.getServicio().getDirectorioRetenciones()+"AutorizarComprobanteRetencion"+estab+"-"+ptoEmi+"-"+secuencial+".xml";
        m.marshal(jaxb_autoriza, new File (rutaXml));

        System.out.println("[info] - xml generado "+rutaXml);  
        this.frmMonitor.setMensajeRetenciones("[info] - xml generado "+rutaXml);
        }
        catch(JAXBException ex){}
        finally{}
    }
    
    public int actualizarRetencion(ConexionBD con,Response autorizacion,InfoTributaria info){
        
        int result=0;
        String update="UPDATE INVE_RETENCIONES_FE_DAT SET NUME_AUTO_INVE_RETE=? "
                +"WHERE CODDOC='07' "
                +"AND AMBIENTE="+frmMonitor.getServicio().getAmbiente()
                +" AND ESTAB="+info.getEstab()
                +" AND PTOEMI="+info.getPtoEmi()
                +" AND SECUENCIAL="+info.getSecuencial();
        PreparedStatement ps=null;
        try
        {
            ps=con.getCon().prepareStatement(update);
            ps.setString(1, autorizacion.getAutorizacion().getValue());
            result=ps.executeUpdate();
            
        }
        catch(SQLException sqle)
        {
            this.frmMonitor.setMensajeRetenciones("[error] - Error al actualizar registros");
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
    
    private int notificarError(ConexionBD con, String ex,InfoTributaria info,String ts)throws SQLException{
    String insert="INSERT INTO INVE_LOG_FE_DAT(COD_DOC,NUM_DOC,MENSAJE_DEVUELTO,TIEMPO_RESPUESTA,AMBIENTE)"+
            "VALUES(?,?,?,?,?)";
        PreparedStatement ps=con.getCon().prepareStatement(insert);
        
        ps.setString(1,info.getCodDoc());
        ps.setString(2,info.getEstab()+"-"+info.getPtoEmi()+"-"+info.getSecuencial());
        ps.setString(3,ex);
        ps.setString(4,ts);
        ps.setInt(5,2);

        int n=ps.executeUpdate();
        ps.close();
        return n;

    }
    
    public int cambiaEstado(ConexionBD con,String estado,int atendiendo){
    
        int result=0;
        String update="UPDATE INVE_INFO_FE_DAT SET ESTATUS=?,USUARIO_ACT=?,ULT_EJECUCION=?,HOST_ACT=?,ATENDIENDO=? WHERE NOMBRE='HILO RETENCIONES'";
        PreparedStatement ps = null;
        InetAddress localHost=null;
        try
        {
            ps = con.getCon().prepareStatement(update);
            ps.setString(1, estado);
            ps.setString(2, System.getProperty("user.name"));
            java.util.Date now = new java.util.Date();
            ps.setDate(3,new java.sql.Date(now.getTime()));
            localHost = InetAddress.getLocalHost();
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

    private static Response autorizarComprobanteRetencion(InfoTributaria infoTributaria, InfoCompRetencion infoCompRetencion, ArrayOfImpuestosRetencion impuestos, ArrayOfInfoAdicional infoAdicional) {
        Response respuesta = null;
        CloudAutorizarComprobante service = new CloudAutorizarComprobante();
        IcloudAutorizarComprobante port = service.getBasicHttpBindingIcloudAutorizarComprobante();
        try 
        {            
            respuesta = new Response();
            respuesta=port.autorizarComprobanteRetencion(infoTributaria, infoCompRetencion, impuestos, infoAdicional);
             } 
        catch (Exception e) 
        {
            System.err.println("Error al invocar: " + e.getMessage());
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
