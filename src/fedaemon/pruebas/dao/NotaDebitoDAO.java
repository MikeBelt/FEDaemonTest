
package fedaemon.pruebas.dao;


import fedaemon.pruebas.frms.frmMonitor;
import fedaemon.infact.pruebas.ArrayOfImpuesto;
import fedaemon.infact.pruebas.ArrayOfInfoAdicional;
import fedaemon.infact.pruebas.ArrayOfMotivo;
import fedaemon.infact.pruebas.AutorizarNotaDebito;
import fedaemon.infact.pruebas.CloudAutorizarComprobante;
import fedaemon.infact.pruebas.IcloudAutorizarComprobante;
import fedaemon.infact.pruebas.Impuesto;
import fedaemon.infact.pruebas.InfoAdicional;
import fedaemon.infact.pruebas.InfoNotaDebito;
import fedaemon.infact.pruebas.InfoTributaria;
import fedaemon.infact.pruebas.Motivo;
import fedaemon.infact.pruebas.ObjectFactory;
import fedaemon.infact.pruebas.Response;
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


/**
 *
 * @author Michael Beltrán
 */
public final class NotaDebitoDAO {
    
    private frmMonitor frmMonitor;
    
    public int consultarNotaDebitoPendiente(ConexionBD con){
        int result=0;
        String select="SELECT COUNT(*) "
                + "FROM INVE_NCND_FE_DAT "
                + "WHERE CODDOC='05' "
                + "AND NUME_AUTO_INVE_DOCU IS NULL "
                + "AND AMBIENTE="+frmMonitor.getServicio().getAmbiente()
                + " GROUP BY ESTAB,PTOEMI,SECUENCIAL";
        ResultSet rs=null;
        Statement st=null;
        try
        {
            st= con.getCon().createStatement();
            rs=st.executeQuery(select);
            while(rs.next())
            {
               result++;
            }
        }
        catch(SQLException ex){System.out.println("[error] - error de ResultSet de consultaNotasDebitoPendientes");}
        finally
        {
            
          try
          {
             if(rs!=null)
                rs.close();
          }catch(SQLException se2)
          {
              System.out.println("[error] - error de cerrar ResultSet de consultaNotasDebitoPendientes");
          } 
        }

        return result;
    }
    
    public int consultarNotaDebitoPendiente(ConexionBD con,String coddoc,String ambiente) {
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
        catch(SQLException ex){ex.printStackTrace();}
        finally
        {
         try
         {
            if(cs!=null)
                cs.close();
         }
         catch(SQLException se2)
         {}
    }
    
    return result;
    
    }
     
     public int enviarNotasDebito(ConexionBD con){
        
        int enviadas=0;
        ObjectFactory factory = null;
        String marco="============================================================================";
        //OJO que al consultar data de la base se recuperará info como estaba hasta el ultimo COMMIT ejecutado
        String select="SELECT COUNT(*) LINEAS,TOTALSINIMPUESTO,DESCUENTO,VALORMODIFICACION,ESTAB,PTOEMI,SECUENCIAL,FECHAEMISION "
                + "FROM INVE_NCND_FE_DAT "
                + "WHERE CODDOC='05' "
                + "AND NUME_AUTO_INVE_DOCU IS NULL "
                + "AND AMBIENTE="+frmMonitor.getServicio().getAmbiente()
                + " GROUP BY TOTALSINIMPUESTO,DESCUENTO,VALORMODIFICACION,ESTAB,PTOEMI,SECUENCIAL,FECHAEMISION "
                + "ORDER BY FECHAEMISION ASC,SECUENCIAL ASC";
        Statement st=null;
        ResultSet rs=null;
        InfoDocumento ND=null;
        ArrayList<InfoDocumento> arrayInfoDoc=null;
        ArrayList<AutorizarNotaDebito> arrayAutorizarNotaDebito=null;
        int band=0;
        long start=0;
        long stop = 0;
        Response respuesta=null;
        
        try{
            factory = new ObjectFactory();
            arrayInfoDoc=new ArrayList<>();
            arrayAutorizarNotaDebito=new ArrayList<>();
            st= con.getCon().createStatement();
            rs=st.executeQuery(select);
            
        while(rs.next())
        {   
            ND=new InfoDocumento();
            ND.setLineas(rs.getString("LINEAS"));
            ND.setEstab(rs.getString("ESTAB"));
            ND.setPtoEmi(rs.getString("PTOEMI"));
            ND.setSecuencial(rs.getString("SECUENCIAL"));
            ND.setTotalSinImpuesto(rs.getDouble("TOTALSINIMPUESTO"));
            ND.setTotalDescuento(rs.getDouble("DESCUENTO"));
            ND.setTotalModificacion(rs.getDouble("VALORMODIFICACION"));
            arrayInfoDoc.add(ND);
        }
        rs.close();
        st.close();
        
        for(int i=0;i<arrayInfoDoc.size();i++){
            this.frmMonitor.limpiaND();
            System.out.println("[info] - Registro #"+(i+1)+ " de "+arrayInfoDoc.size());
            this.frmMonitor.setMensajeND("[info] - Registro #"+(i+1)+ " de "+arrayInfoDoc.size());
            InfoTributaria info_t=new InfoTributaria();
            InfoNotaDebito info_nd=new InfoNotaDebito();
            ArrayOfMotivo array_motivo=new ArrayOfMotivo();
            ArrayOfInfoAdicional array_info_a=new ArrayOfInfoAdicional();
        band=0;
        try{
             String filtro="SELECT * FROM INVE_NCND_FE_DAT WHERE NUME_AUTO_INVE_DOCU IS NULL AND CODDOC='05' AND AMBIENTE=2 "
    //                         + "AND CODI_ADMI_EMPR_FINA='00001' AND CODI_ADMI_PUNT_VENT='101' "
                    +" AND ESTAB="+arrayInfoDoc.get(i).getEstab()
                    +" AND PTOEMI="+arrayInfoDoc.get(i).getPtoEmi()
                    +" AND SECUENCIAL="+arrayInfoDoc.get(i).getSecuencial();
             st=con.getCon().createStatement();
             rs=st.executeQuery(filtro);
                 
            while(rs.next())
            {
                if(band==0)
                {
                    //===================== INFORMACION TRIBUTARIA ====================================
                    info_t.setAmbiente(Integer.parseInt(rs.getString("AMBIENTE")));
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
                    

                    //===================== INFORMACION NOTA DE DEBITO ========================================
                    info_nd.setCodDocModificado(rs.getString("CODDOCMODIFICADO"));
                    JAXBElement<String> contribuyenteEspecial=factory.createInfoNotaDebitoContribuyenteEspecial(rs.getString("CONTRIBUYENTEESPECIAL"));
                    info_nd.setContribuyenteEspecial(contribuyenteEspecial);
                    JAXBElement<String> dirEstablecimiento=factory.createInfoNotaDebitoDirEstablecimiento(rs.getString("DIRESTABLECIMIENTO"));
                    info_nd.setDirEstablecimiento(dirEstablecimiento);
                    info_nd.setFechaEmision(rs.getString("FECHAEMISION"));
                    info_nd.setFechaEmisionDocSustento(rs.getString("FECHAEMISIONDOCSUSTENTO"));
                    info_nd.setIdentificacionComprador(rs.getString("IDENTIFICACIONCOMPRADOR"));
                    
                        Impuesto imp=new Impuesto();
                        imp.setBaseImponible(BigDecimal.valueOf(rs.getDouble("BASEIMPONIBLE_IMP")));
                        imp.setCodigo(rs.getInt("CODIGO_IMP"));
                        imp.setCodigoPorcentaje(rs.getInt("CODIGOPORCENTAJE_IMP"));
                        imp.setTarifa(rs.getInt("TARIFA_IMP"));
                        imp.setValor(rs.getBigDecimal("TOTALIVA"));
//                        imp.setValor(BigDecimal.valueOf(rs.getDouble("BASEIMPONIBLE_IMP")*rs.getDouble("TARIFA_IMP")/100));

                        ArrayOfImpuesto array_of_impuesto=new ArrayOfImpuesto();
                        array_of_impuesto.getImpuesto().add(imp);
                    
                    info_nd.setImpuestos(array_of_impuesto);
                    info_nd.setNumDocModificado(rs.getString("NUMDOCMODIFICADO"));
                    JAXBElement<String> obligadoContabilidad=factory.createInfoNotaDebitoObligadoContabilidad(rs.getString("OBLIGADOCONTABILIDAD"));
                    info_nd.setObligadoContabilidad(obligadoContabilidad);
                    info_nd.setRazonSocialComprador(rs.getString("RAZONSOCIALCOMPRADOR"));
                    JAXBElement<String> rise=factory.createInfoNotaDebitoRise(rs.getString("RISE"));
                    info_nd.setRise((JAXBElement<String>) (rise==null?"NO REGISTRADO":rise));
                    info_nd.setTipoIdentificacionComprador(rs.getString("TIPOIDENTIFICACIONCOMPRADOR"));
                    info_nd.setTotalSinImpuestos(BigDecimal.valueOf(rs.getDouble("TOTALSINIMPUESTO")));
                    info_nd.setValorTotal(BigDecimal.valueOf(rs.getDouble("VALORMODIFICACION")));

                    
                    //============================ INFORMACION ADICIONAL =====================================
                    InfoAdicional info_a1=new InfoAdicional();
                    JAXBElement<String> nombre=factory.createInfoAdicionalNombre("OBSERVACION");
                    info_a1.setNombre(nombre);
                    String observacion=rs.getString("OBSERVACION")==null?"NO REGISTRADO":rs.getString("OBSERVACION").toUpperCase().trim();
                    if(observacion!=null)
                    {
                        observacion=observacion.replace('Á','A');
                        observacion=observacion.replace('É','E');
                        observacion=observacion.replace('Í','I');
                        observacion=observacion.replace('Ó','O');
                        observacion=observacion.replace('Ú','U');
//                            observacion=observacion.replace(".", "");
//                            observacion=observacion.replace(",", "");
                        observacion=observacion.replace("\n", "");
                        observacion=observacion.replace("Ñ", "N");
                        observacion=observacion.replace("ñ", "n");
                    }
                    JAXBElement<String> text=factory.createInfoAdicionalText(observacion);
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

                    InfoAdicional info_a7=new InfoAdicional();
                    nombre=factory.createInfoAdicionalNombre("MOTIVO");
                    info_a7.setNombre(nombre);
                    text=factory.createInfoAdicionalText(rs.getString("MOTIVO").toUpperCase().trim());
                    info_a7.setText((JAXBElement<String>) (text==null?"NO REGISTRADO":text));


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
                    if(rs.getString("MOTIVO")!=null)
                        array_info_a.getInfoAdicional().add(info_a7);
                }
                
                //=========================== MOTIVOS ======================================
                    Motivo motivo=new Motivo();
                    motivo.setRazon(rs.getString("MOTIVO"));
                    motivo.setValor(BigDecimal.valueOf(rs.getDouble("VALORMODIFICACION")));

                    array_motivo.getMotivo().add(motivo);
                
            }
        }
        catch(SQLException e)
        {
            System.out.println("[error] - Error al empaquetar el documento. "+e.getMessage());
            this.frmMonitor.setMensajeND("[error] - Error al empaquetar el documento. "+e.getMessage());
        }
        finally
        {
            rs.close();
            st.close();
        }

        AutorizarNotaDebito autorizar=new AutorizarNotaDebito();
        JAXBElement<InfoTributaria> jbInfoTributaria=factory.createAutorizarNotaDebitoInfoTributaria(info_t);
        autorizar.setInfoTributaria( jbInfoTributaria);
        JAXBElement<InfoNotaDebito> jbInfoNotaDebito=factory.createAutorizarNotaDebitoInfoNotaDebito(info_nd);
        autorizar.setInfoNotaDebito(jbInfoNotaDebito);
        JAXBElement<ArrayOfMotivo> jbArrayOfMotivo=factory.createAutorizarNotaDebitoMotivos(array_motivo);
        autorizar.setMotivos(jbArrayOfMotivo);
        JAXBElement<ArrayOfInfoAdicional> jbArrayOfInfoAdicional=factory.createArrayOfInfoAdicional(array_info_a);
        autorizar.setInfoAdicional(jbArrayOfInfoAdicional);

        //generando el xml
        generarXML(autorizar,arrayInfoDoc.get(i).getEstab(),arrayInfoDoc.get(i).getPtoEmi(),arrayInfoDoc.get(i).getSecuencial() );
        
        arrayAutorizarNotaDebito.add(autorizar);
        }  //final de for de empaquetado      
            start=0;
            stop = 0;
            respuesta=null;
                
            //Enviar documento empaquetado al webservice de SRI para autorizar
            for(int i=0;i<arrayAutorizarNotaDebito.size();i++){
                System.out.println("[info] - Registro #"+(i+1)+ " de "+arrayAutorizarNotaDebito.size());
                this.frmMonitor.setMensajeND("[info] - Registro #"+(i+1)+ " de "+arrayAutorizarNotaDebito.size());
            
                this.frmMonitor.setMensajeND("[info] - No. Lineas : "+arrayInfoDoc.get(i).getLineas());
                System.out.println("[info] - Enviando petición de autorización al WS...");
                this.frmMonitor.setMensajeND("[info] - Enviando petición de autorización al WS...");
                //obteniendo el tiempo inicial para el tiempo de espera estimado
                start = Calendar.getInstance().getTimeInMillis();
                //Instancia del servicio de INTEME
                //El objeto Response encapsula la información del documento autorizado o no autorizado
                respuesta=autorizarNotaDebito(arrayAutorizarNotaDebito.get(i).getInfoTributaria().getValue()
                        ,arrayAutorizarNotaDebito.get(i).getInfoNotaDebito().getValue()
                        ,arrayAutorizarNotaDebito.get(i).getMotivos().getValue()
                        ,arrayAutorizarNotaDebito.get(i).getInfoAdicional().getValue()); 
                //obteniendo el tiempo final para el tiempo de espera estimado
                stop = Calendar.getInstance().getTimeInMillis();

                System.out.println("[info] - Tiempo de respuesta: "+(stop-start)+" seg");
                this.frmMonitor.setMensajeND("[info] - Tiempo de respuesta: "+(stop-start)+" seg");       
                enviadas++;
                System.out.println(marco);
                this.frmMonitor.setMensajeND(marco);
                System.out.println("No. de autorización: "+respuesta.getAutorizacion().getValue());
                this.frmMonitor.setMensajeND("No. de autorización: "+respuesta.getAutorizacion().getValue()); 
                System.out.println("Clave de acceso: "+respuesta.getClaveAcceso().getValue());
                this.frmMonitor.setMensajeND("Clave de acceso: "+respuesta.getClaveAcceso().getValue()); 
                System.out.println("Fecha Autorización: "+respuesta.getFechaAutorizacion().getValue());
                this.frmMonitor.setMensajeND("Fecha Autorización: "+respuesta.getFechaAutorizacion().getValue()); 
                System.out.println("Id. Error: "+respuesta.getIdError().getValue());
                this.frmMonitor.setMensajeND("Id. Error: "+respuesta.getIdError().getValue()); 
                System.out.println("Origen: "+respuesta.getOrigen().getValue());
                this.frmMonitor.setMensajeND("Origen: "+respuesta.getOrigen().getValue()); 
                System.out.println("Result: "+respuesta.getResult().getValue());
                this.frmMonitor.setMensajeND("Result: "+respuesta.getResult().getValue()); 
                System.out.println("Result Data: "+respuesta.getResultData().getValue());
                this.frmMonitor.setMensajeND("Result Data: "+respuesta.getResultData().getValue());
                System.out.println(marco);
                this.frmMonitor.setMensajeND(marco);

                    if(respuesta.getAutorizacion().getValue()!=null)
                    {
                        this.frmMonitor.setMensajeND("[info] - Actualizando registros...");
                        System.out.println("[info] - Actualizando registros...");
                        //llamada del metodo para actualizar registro
                        int reg=actualizarND(con, respuesta,arrayAutorizarNotaDebito.get(i).getInfoTributaria().getValue());
                        System.out.println("[info] - Registros actualizados : "+reg);
                        this.frmMonitor.setMensajeND("[info] - Registros actualizados : "+reg); 
                    }
                this.frmMonitor.setMensajeND("[info] - Registrando en el log...");
                System.out.println("[info] - Registrando en el log...");
                //llamada del metodo para el registro del log
                notificarResultado(con, respuesta,arrayAutorizarNotaDebito.get(i).getInfoTributaria().getValue(),String.valueOf((stop-start)));
                this.frmMonitor.setMensajeND("[info] - Evento capturado en el historial");
                System.out.println("[info] - Evento capturado en el historial"); 
               
            }//final del FOR de envío
        }
        catch(SQLException | NumberFormatException ex)
        {
            this.frmMonitor.setMensajeND("[error] - Error general al enviar a autorizar");
            System.out.println("[error] - Error general al enviar a autorizar");
        }
        finally
        {
            this.frmMonitor.setMensajeND("[info] - Cancelando envío...");
            System.out.println("[info] - Cancelando envío...");
        }
        
        
        return enviadas;
    }

     public void generarXML(AutorizarNotaDebito autoriza,String estab,String ptoEmi,String secuencial){

                Marshaller m=null;
                String rutaXml;
                JAXBElement<AutorizarNotaDebito> jaxb_autoriza=null;
                JAXBContext jaxbContext=null;
                try
                {
                    System.out.println("[info] - Generando xml...");  
                    this.frmMonitor.setMensajeND("[info] - Generando xml...");
                    jaxb_autoriza=new JAXBElement(new QName(AutorizarNotaDebito.class.getSimpleName()),AutorizarNotaDebito.class,autoriza);
                    jaxbContext=JAXBContext.newInstance(AutorizarNotaDebito.class);
                    m=jaxbContext.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
                    rutaXml=this.frmMonitor.getServicio().getDirectorioNotasDebito()+"AutorizarNotaDebito"+estab+"-"+ptoEmi+"-"+secuencial+".xml";
                    m.marshal(jaxb_autoriza, new File (rutaXml)); 

                    System.out.println("[info] - xml generado "+rutaXml);  
                    this.frmMonitor.setMensajeND("[info] - xml generado "+rutaXml);
                }
                catch(JAXBException ex)
                {
                    System.out.println("[error] - error al generar xml");  
                    this.frmMonitor.setMensajeND("[error] - error al generar xml");}
                finally{}
     }
     
     private int actualizarND(ConexionBD con,Response resp,InfoTributaria info) {

         int result=0;
         String update="UPDATE INVE_NCND_FE_DAT SET NUME_AUTO_INVE_DOCU=? "
                 +" WHERE CODDOC='05' "
                 + "AND AMBIENTE="+frmMonitor.getServicio().getAmbiente()
                 + " AND ESTAB="+info.getEstab()
                 +" AND PTOEMI="+info.getPtoEmi()
                 +" AND SECUENCIAL="+info.getSecuencial();
         PreparedStatement ps=null;
         try
         {
             ps=con.getCon().prepareStatement(update);
             ps.setString(1, resp.getAutorizacion().getValue());
             result=ps.executeUpdate();
         }
         catch(SQLException sqle)
        {
            this.frmMonitor.setMensajeND("[error] - Error al actualizar registros");
            System.out.println("[error] - Error al actualizar registros");
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
                System.out.println("[error] - Error al cerrar PreparedStatement");}
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
        catch(SQLException ex){System.out.println("[error] - Error al insertar registros de notificacion");}
        finally
        {
            if(ps!=null)
            {
                try
                {
                    ps.close();
                }
                catch(SQLException e){
                System.out.println("[error] - Error al cerrar PreparedStatement de notificacion");}
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
        String update="UPDATE INVE_INFO_FE_DAT SET ESTATUS=?,USUARIO_ACT=?,ULT_EJECUCION=?,HOST_ACT=?,ATENDIENDO=? WHERE NOMBRE='HILO NOTAS DEBITO'";
        PreparedStatement ps =null;
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
            System.out.println("[error] - Error al actualizar estado del proceso");
        }
        catch(UnknownHostException uhe)
        {
            System.out.println("[error] - Error al recuperar InetAddress");
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
                System.out.println("[error] - Error al cerrar PreparedStatement");}
            }
        }
    return result;
    }

    private static Response autorizarNotaDebito(InfoTributaria infoTributaria, InfoNotaDebito infoNotaDebito, ArrayOfMotivo motivos,ArrayOfInfoAdicional infoAdicional) {
        Response respuesta = null;
        CloudAutorizarComprobante service = new CloudAutorizarComprobante();
        IcloudAutorizarComprobante port = service.getBasicHttpBindingIcloudAutorizarComprobante();
        try 
        {            
            respuesta = new Response();
            respuesta=port.autorizarNotaDebito(infoTributaria, infoNotaDebito, motivos, infoAdicional);
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
    
    public frmMonitor getMONITOR() {
        return frmMonitor;
    }

    public void setMONITOR(frmMonitor monitor) {
        this.frmMonitor = monitor;
    } 
     
}
