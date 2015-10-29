

package fedaemon.pruebas.frms;

import fedaemon.pruebas.hilos.ThreadAutorizarComprobanteRetencion;
import fedaemon.pruebas.hilos.ThreadAutorizarFactura;
import fedaemon.pruebas.hilos.ThreadAutorizarNotaCredito;
import fedaemon.pruebas.hilos.ThreadAutorizarNotaDebito;
import fedaemon.pruebas.util.ConexionBD;
import fedaemon.pruebas.util.Servicio;
import java.awt.Dimension;
import java.awt.Point;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mike
 */
public final class frmMonitor extends javax.swing.JFrame {

    /**
     * Creates new form frmMonitor
     */
    public frmMonitor() {
        initComponents();
        this.setExtendedState(frmMonitor.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(0);

    }
    
    public void limpiaFacturas(){
    this.jTextArea1.setText(null);
    }
    
    public void limpiaNC(){
    this.jTextArea3.setText(null);
    }
    
    public void limpiaND(){
    this.jTextArea4.setText(null);
    }
    
    public void limpiaRetenciones(){
    this.jTextArea2.setText(null);
    }
    
    public void cambiaEstadoPanel(String nombrePanel,String mensajePanel){
    
        if(nombrePanel.equals("jPFacturas"))
            jPFacturas.setBorder(javax.swing.BorderFactory.createTitledBorder(null, mensajePanel, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(null, 1, 11), new java.awt.Color(255, 255, 255)));
        if(nombrePanel.equals("jPRetencion"))
            jPRetencion.setBorder(javax.swing.BorderFactory.createTitledBorder(null, mensajePanel, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(null, 1, 11), new java.awt.Color(255, 255, 255)));
        if(nombrePanel.equals("jPNC"))
            jPNC.setBorder(javax.swing.BorderFactory.createTitledBorder(null, mensajePanel, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(null, 1, 11), new java.awt.Color(255, 255, 255)));
        if(nombrePanel.equals("jPND"))
            jPND.setBorder(javax.swing.BorderFactory.createTitledBorder(null, mensajePanel, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(null, 1, 11), new java.awt.Color(255, 255, 255)));
    }
    
    public void setMensajeFacturas(String mens){
    
    this.jTextArea1.append(mens+"\n");
    Dimension tamanhoTextArea = jTextArea1.getSize();
         Point p = new Point(
            0,
            tamanhoTextArea.height
         );
         jScrollPane1.getViewport().setViewPosition(p);
    }
    
    public void setMensajeRetenciones(String mens){
    
    this.jTextArea2.append(mens+"\n");
    Dimension tamanhoTextArea = jTextArea2.getSize();
         Point p = new Point(
            0,
            tamanhoTextArea.height
         );
         jScrollPane2.getViewport().setViewPosition(p);
    }
    
    public void setMensajeNC(String mens){
    
    this.jTextArea3.append(mens+"\n");
    Dimension tamanhoTextArea = jTextArea3.getSize();
         Point p = new Point(
            0,
            tamanhoTextArea.height
         );
         jScrollPane3.getViewport().setViewPosition(p);
    }
    
    public void setMensajeND(String mens){
    
    this.jTextArea4.append(mens+"\n");
    Dimension tamanhoTextArea = jTextArea4.getSize();
         Point p = new Point(
            0,
            tamanhoTextArea.height
         );
         jScrollPane4.getViewport().setViewPosition(p);
    }
    
    public void lanzarHilos(){
    System.out.println("Iniciando el proceso demonio... ");
        
            threadAutorizarFacturas=new ThreadAutorizarFactura();
            threadAutorizarRetenciones=new ThreadAutorizarComprobanteRetencion();
            threadAutorizarNotaCredito=new ThreadAutorizarNotaCredito();
            threadAutorizarNotaDebito=new ThreadAutorizarNotaDebito(); 
            
            threadAutorizarFacturas.setConexion(conexionBD);
            threadAutorizarFacturas.setMonitor(this);
            threadAutorizarRetenciones.setConexion(conexionBD);
            threadAutorizarRetenciones.setMonitor(this);
            threadAutorizarNotaCredito.setConexion(conexionBD);
            threadAutorizarNotaCredito.setMonitor(this);
            threadAutorizarNotaDebito.setConexion(conexionBD);
            threadAutorizarNotaDebito.setMonitor(this);
            
            this.setTitle(this.getTitle().concat(" - "+conexionBD.getBase()+" - "+conexionBD.getUsr()));

            threadAutorizarFacturas.start();
            this.cambiaEstadoPanel("jPFacturas", "Facturas [EJECUTANDO]");
            threadAutorizarRetenciones.start();
            this.cambiaEstadoPanel("jPRetencion", "Retenciones [EJECUTANDO]");
            threadAutorizarNotaCredito.start();
            this.cambiaEstadoPanel("jPNC", "Notas de Crédito [EJECUTANDO]");
            threadAutorizarNotaDebito.start();
            this.cambiaEstadoPanel("jPND", "Notas de Débito [EJECUTANDO]");
    
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPFacturas = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPRetencion = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPNC = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jPND = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Autorización de Documentos Electrónicos FEDaemonTEVCOL");
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPFacturas.setBackground(new java.awt.Color(51, 51, 51));
        jPFacturas.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Facturas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, java.awt.Color.lightGray));

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(0, 0, 0));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Consolas", 0, 11)); // NOI18N
        jTextArea1.setForeground(new java.awt.Color(0, 153, 0));
        jTextArea1.setRows(5);
        jTextArea1.setText("Ejecutando Monitoreo de Facturas...");
        jTextArea1.setName("jta_facturas"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPFacturasLayout = new javax.swing.GroupLayout(jPFacturas);
        jPFacturas.setLayout(jPFacturasLayout);
        jPFacturasLayout.setHorizontalGroup(
            jPFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPFacturasLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE))
        );
        jPFacturasLayout.setVerticalGroup(
            jPFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPFacturasLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE))
        );

        jPRetencion.setBackground(new java.awt.Color(51, 51, 51));
        jPRetencion.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Comprobantes de Retención", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, java.awt.Color.lightGray));

        jTextArea2.setEditable(false);
        jTextArea2.setBackground(new java.awt.Color(0, 0, 0));
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Consolas", 0, 11)); // NOI18N
        jTextArea2.setForeground(new java.awt.Color(0, 153, 0));
        jTextArea2.setRows(5);
        jTextArea2.setText("Ejecutando Monitoreo de Comp. Retención...");
        jTextArea2.setName("jta_retencion"); // NOI18N
        jScrollPane2.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPRetencionLayout = new javax.swing.GroupLayout(jPRetencion);
        jPRetencion.setLayout(jPRetencionLayout);
        jPRetencionLayout.setHorizontalGroup(
            jPRetencionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
        );
        jPRetencionLayout.setVerticalGroup(
            jPRetencionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jPNC.setBackground(new java.awt.Color(51, 51, 51));
        jPNC.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Notas de Crédito", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, java.awt.Color.lightGray));

        jTextArea3.setEditable(false);
        jTextArea3.setBackground(new java.awt.Color(0, 0, 0));
        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Consolas", 0, 11)); // NOI18N
        jTextArea3.setForeground(new java.awt.Color(0, 153, 0));
        jTextArea3.setRows(5);
        jTextArea3.setText("Ejecutando Monitoreo de Notas Crédito...");
        jTextArea3.setName("jta_nc"); // NOI18N
        jScrollPane3.setViewportView(jTextArea3);

        javax.swing.GroupLayout jPNCLayout = new javax.swing.GroupLayout(jPNC);
        jPNC.setLayout(jPNCLayout);
        jPNCLayout.setHorizontalGroup(
            jPNCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPNCLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane3))
        );
        jPNCLayout.setVerticalGroup(
            jPNCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
        );

        jPND.setBackground(new java.awt.Color(51, 51, 51));
        jPND.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Notas de Débito", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, java.awt.Color.lightGray));

        jTextArea4.setEditable(false);
        jTextArea4.setBackground(new java.awt.Color(0, 0, 0));
        jTextArea4.setColumns(20);
        jTextArea4.setFont(new java.awt.Font("Consolas", 0, 11)); // NOI18N
        jTextArea4.setForeground(new java.awt.Color(0, 153, 0));
        jTextArea4.setRows(5);
        jTextArea4.setText("Ejecutando Monitoreo de Notas Débito...");
        jTextArea4.setName("jta_nb"); // NOI18N
        jScrollPane4.setViewportView(jTextArea4);

        javax.swing.GroupLayout jPNDLayout = new javax.swing.GroupLayout(jPND);
        jPND.setLayout(jPNDLayout);
        jPNDLayout.setHorizontalGroup(
            jPNDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPNDLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane4))
        );
        jPNDLayout.setVerticalGroup(
            jPNDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPFacturas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPNC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPRetencion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPND, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPFacturas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPRetencion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPNC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPND, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
       
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
             if(conexionBD!=null)
                 conexionBD.conectar();
             
            if(threadAutorizarFacturas!=null )
                if(threadAutorizarFacturas.isAlive())
                {threadAutorizarFacturas.facturaDAO.cambiaEstado(conexionBD, "APAGADO", 0);}
            if(threadAutorizarRetenciones!=null )
                if(threadAutorizarRetenciones.isAlive())
                {threadAutorizarRetenciones.retencionDAO.cambiaEstado(conexionBD, "APAGADO", 0);}
            if(threadAutorizarNotaCredito!=null)
                if(threadAutorizarNotaCredito.isAlive())
                {   threadAutorizarNotaCredito.notaCreditoDAO.cambiaEstado(conexionBD, "APAGADO", 0);}
            if(threadAutorizarNotaDebito!=null)
                if(threadAutorizarNotaDebito.isAlive())
                {threadAutorizarNotaDebito.notaDebitoDAO.cambiaEstado(conexionBD, "APAGADO", 0);}
           
            conexionBD.desconectar();
            
            
        } catch (SQLException ex) {
            Logger.getLogger(frmMonitor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(frmMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
        
        threadAutorizarFacturas.stop();
        this.cambiaEstadoPanel("jPFacturas", "Facturas [APAGADO]");
        threadAutorizarRetenciones.stop();
        this.cambiaEstadoPanel("jPRetencion", "Retenciones [APAGADO]");
        threadAutorizarNotaCredito.stop();
        this.cambiaEstadoPanel("jPNC", "Notas de Crédito [APAGADO]");
        threadAutorizarNotaDebito.stop();
        this.cambiaEstadoPanel("jPND", "Notas de Débito [APAGADO]");
    
        
        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmMonitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmMonitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmMonitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmMonitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmMonitor().setVisible(true);
            }
        });
    }

    private ConexionBD conexionBD;
    ThreadAutorizarFactura threadAutorizarFacturas;
    ThreadAutorizarComprobanteRetencion threadAutorizarRetenciones;
    ThreadAutorizarNotaCredito threadAutorizarNotaCredito;
    ThreadAutorizarNotaDebito threadAutorizarNotaDebito;
    private Servicio servicio;
   
            
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPFacturas;
    private javax.swing.JPanel jPNC;
    private javax.swing.JPanel jPND;
    private javax.swing.JPanel jPRetencion;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    // End of variables declaration//GEN-END:variables

    public ConexionBD getConexion() {
        return conexionBD;
    }

    public void setConexion(ConexionBD conexion) {
        this.conexionBD = conexion;
    }

    /**
     * @return the servicio
     */
    public Servicio getServicio() {
        return servicio;
    }

    /**
     * @param servicio the servicio to set
     */
    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }
}
