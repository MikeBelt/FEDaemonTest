/*
 * PantallaConexionBD.java
 *
 * Created on 19/01/2015, 09:10 AM
 */

package fedaemon.pruebas.frms;

import fedaemon.pruebas.util.ConexionBD;
import fedaemon.pruebas.util.Empresa;
import fedaemon.pruebas.util.Servicio;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public final class frmConexionBD extends javax.swing.JFrame {

    /** Creates new form PantallaConexionBD */
    public frmConexionBD() {
        initComponents();
        this.setLocation(500,200);
       
    }
    
    public void ejecutarForm()
    {
        System.out.println(this.getTitle()); 
        try{
            conexionBD=new ConexionBD(this.getEmpresa().getUsuario()
                    ,this.getEmpresa().getPasword()
                    ,this.getEmpresa().getServidor()
                    ,this.getEmpresa().getBase()
                    ,this.getEmpresa().isSid()
                    ,this.getEmpresa().isServiceName());
            conexionBD.conectar();
            System.out.println("CONEXION EXITOSA");
            conexionBD.desconectar();
            
                //creo la pantalla de Monitoreo
                frmMonitor PIS=new frmMonitor();
                PIS.setConexion(conexionBD);
                PIS.setServicio(servicio);
                PIS.setVisible(true);
                PIS.lanzarHilos();

    //                    this.dispose();
                mostrarNotificacion();
        }
        catch(SQLException | ClassNotFoundException e)
        {
           System.out.println("[error] - NO SE PUDO ESTABLECER CONEXION CON EL SERVIDOR DE LA BASE");
           JOptionPane.showMessageDialog(this,"CONEXION FALLIDA\n"+e.getMessage());

        }
        finally
        {
            System.out.println("[info] - continuando...");
        }
    
    }

    public static void mostrarNotificacion(){
    try{
        TrayIcon icono = new TrayIcon(getImagen(),"FEDaemon Pruebas",crearMenu());
        SystemTray.getSystemTray().add(icono);
//        Thread.sleep(5000);
        
        icono.displayMessage("FEDaemon - Pruebas", "Ejecutando hilos de proceso", TrayIcon.MessageType.INFO);
    }catch(Exception ex){ex.printStackTrace();}
    }
    
    public static Image getImagen() {
        Image retValue = Toolkit.getDefaultToolkit().getImage("/fedaemon/img/icono-tevcol-16x16.png");
        return retValue;
    }
    
//     @Override
//    public Image getIconImage() {
//        Image retValue = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("img/icono-tevcol.png"));
//        return retValue;
//    }
    
    public static PopupMenu crearMenu(){
        PopupMenu menu = new PopupMenu();
        MenuItem salir = new MenuItem("Salir");
        MenuItem monitor = new MenuItem("Monitor");
        salir.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });
        menu.add(salir);
        menu.add(monitor);
        return menu;
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        tf_user = new javax.swing.JTextField();
        tf_server = new javax.swing.JTextField();
        tf_base = new javax.swing.JTextField();
        pf_pass = new javax.swing.JPasswordField();
        jButton3 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Ambiente: PRUEBAS - Conexión a la base de datos");
        setIconImage(getIconImage());
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBackground(java.awt.Color.black);
        jLabel1.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel1.setForeground(java.awt.Color.green);
        jLabel1.setText("USER");
        jLabel1.setOpaque(true);
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 120, -1, -1));

        jLabel2.setBackground(java.awt.Color.black);
        jLabel2.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel2.setForeground(java.awt.Color.green);
        jLabel2.setText("PASSWORD");
        jLabel2.setOpaque(true);
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, -1));

        jLabel3.setBackground(java.awt.Color.black);
        jLabel3.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel3.setForeground(java.awt.Color.green);
        jLabel3.setText("IP SERVER");
        jLabel3.setOpaque(true);
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, -1, -1));

        jLabel4.setBackground(java.awt.Color.black);
        jLabel4.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel4.setForeground(java.awt.Color.green);
        jLabel4.setText("BASE");
        jLabel4.setOpaque(true);
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, -1, -1));

        jButton1.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jButton1.setText("CONECTAR");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 240, -1, -1));

        jButton2.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jButton2.setText("CANCELAR");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 240, -1, -1));
        getContentPane().add(tf_user, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 217, -1));
        getContentPane().add(tf_server, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, 217, -1));
        getContentPane().add(tf_base, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 200, 217, -1));
        getContentPane().add(pf_pass, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 140, 217, -1));

        jButton3.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jButton3.setText("LIMPIAR");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 240, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fedaemon/pruebas/img/icono-tevcol.png"))); // NOI18N
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 120, 100));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String usr=tf_user.getText();
        String pass=pf_pass.getText();
        String server=tf_server.getText();
        String base=tf_base.getText();

        if(usr.length()>0&&pass.length()>0&&server.length()>0&&base.length()>0)
             try{
                conexionBD=new ConexionBD(usr,pass,server,base,false,true);
                conexionBD.conectar();
                System.out.println("CONEXION EXITOSA");
                JOptionPane.showMessageDialog(this,"CONEXION EXITOSA");
                conexionBD.desconectar();
                    //creo la pantalla de inicio de sesion
                    frmMonitor PIS=new frmMonitor();
                    PIS.setConexion(conexionBD);
                    PIS.setServicio(servicio);
                    PIS.setVisible(true);
                    PIS.lanzarHilos();

                    this.dispose();
           }
           catch(Exception e){JOptionPane.showMessageDialog(this,"CONEXION FALLIDA\n"+e.getMessage());
           //e.printStackTrace();
           }
        else
            JOptionPane.showMessageDialog(this,"DEBE RELLENAR TODOS LOS CAMPOS");

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_jButton3ActionPerformed

    public void limpiarCampos(){
        tf_user.setText("");
        pf_pass.setText("");
        tf_server.setText("");
        tf_base.setText("");
    }
    
    public void escribeArchivo()     {         
        FileWriter fichero = null;
        PrintWriter pw = null;         
        try
        {
        fichero = new FileWriter("c:/FEDaemon-PDIs.txt");             
        pw = new PrintWriter(fichero);

        for (int i = 0; i < 10; i++)
            pw.println("Linea " + i);           
        } catch (Exception e) {
        e.printStackTrace();
        }         
 
    finally 
    {
        try {            
        // Nuevamente aprovechamos el finally para             
        // asegurarnos que se cierra el fichero.            
        if (null != fichero)
            fichero.close();
        } catch (Exception e2) {
        e2.printStackTrace();
        }         
    }
} 

    
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmConexionBD().setVisible(false);
            }
        });
    }


     private ConexionBD conexionBD;
     private Empresa empresa;
     private Servicio servicio;
     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPasswordField pf_pass;
    private javax.swing.JTextField tf_base;
    private javax.swing.JTextField tf_server;
    private javax.swing.JTextField tf_user;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the empresa
     */
    public Empresa getEmpresa() {
        return empresa;
    }

    /**
     * @param empresa the empresa to set
     */
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
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
