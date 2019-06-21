
package gestion.proceso.escritorio;
 
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.json.JSONException;
import org.json.JSONObject;
 
public class Ventana extends javax.swing.JFrame  implements ListenerHilos{

   DefaultTableModel modeloTabla;
DefaultTableCellRenderer modelocentrar = new DefaultTableCellRenderer();
int TH,QUANTUM,NUMERO_PROCESOS;

  private ArrayList<Proceso> ListaProcesos;
  private Proceso enPausa;
    private ArrayList<Proceso> cola;
    private ArrayList<Proceso> terminados;
     private String [][] datos = {};
    
     int totalProcesos;
    private int tiempo;
    private DefaultTableModel modeloCola;
    private DefaultTableModel modeloTerminados;
    private DefaultTableModel modeloHistorico;
    
    static SSocket socket;
    public static int ESTADO_SIMULACION ;    
    private Thread hiloActual;
    
    public Ventana() throws IOException {
         modeloTabla = new DefaultTableModel(null, getColumnas());
         TH=0;
         QUANTUM=0;
         NUMERO_PROCESOS=0;
         
          setDefaultCloseOperation(Ventana.DO_NOTHING_ON_CLOSE);
                  

        initComponents();
        setTitle("SIMULACION DE PROCESOS ");
        
        
        //BLOQUEO EL BOTON DE CERRAR
         setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //INICIAMOS LA CONEXION CON EL SOCKET SERVIDOR
         conectarServer();
         
          this.tiempo = 0;
        
        hiloActual = null;
        this.terminados = new ArrayList<>();
        
       
         btnIniciar.setEnabled(false);
         btnPausar.setEnabled(false);
           txtSimulacionProceso.setVisible(false);

              

    
    }
    
    public void removeMinMaxClose(Component comp)
{
  if(comp instanceof JButton)
  {
    String accName = ((JButton) comp).getAccessibleContext().getAccessibleName();
   // System.out.println(accName);
    if(accName.equals("Maximize")|| accName.equals("Iconify")||
       accName.equals("Cerrar")) comp.getParent().remove(comp);
  }
  if (comp instanceof Container)
  {
    Component[] comps = ((Container)comp).getComponents();
    for(int x = 0, y = comps.length; x < y; x++)
    {
      removeMinMaxClose(comps[x]);
    }
  }
}

    void conectarServer() throws IOException
    {
        try{
             socket= new SSocket();
          socket.socketConnect(Constantes.IP, Constantes.PUERTO);
        }catch(EnumConstantNotPresentException e)
        {
            e.printStackTrace();
        }
       
    }
     Object[] getColumnas()
    {
         Object[] cols   = {
      "T. LLEGADA","PID", "NOMBRE","DESCRIPCION","USUARIO","PRIORIDAD"

    };
         return cols;
    }
 

    
    
    //OPTIENE LOS PROCESOS DEL SISTEMA OPERATIVO
        public  void obtenerProcesos(int num_procesos){
            
            ListaProcesos=new ArrayList<Proceso>();
        try {

            String str_proceso = null;
            String admin
                    = System.getenv("windir") + "\\system32\\" + "tasklist /v /fi \"STATUS eq running\"";
            Process proceso = Runtime.getRuntime().exec(admin);
            proceso.getClass();
            
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(proceso.getInputStream()));
            
            int cont=0;
            while ((str_proceso = input.readLine()) != null ) {
                
                //System.out.println(str_proceso);
                if (cont>2 && (cont-3)<num_procesos){
                    optenerProcesos(str_proceso,cont);      
                }
                cont++;
                
            }
            
           // PrepararProcesos();//AQUI SE LES AGREGA EL LISTENER EN CADA PROCESO
            
            totalProcesos=ListaProcesos.size();
            MostrarProcesos(ListaProcesos);
            Ventana.ESTADO_SIMULACION=Constantes.LISTA;
            btnIniciar.setEnabled(true);//AHORA SI PODEMOS INICIAR LA SIMULACION

            
            input.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
        void PrepararProcesos()//crea la cola de espera y agrega los listeners
        {
         this.cola = new ArrayList<>();
        this.cola.add( ListaProcesos.get(0) );// al principio el primero proceso es el listo
        
        for(Proceso proceso : ListaProcesos){
            proceso.setListenerHilo(this);
            proceso.setTiempoHilo(TH);
        }
            
        }
        
 public void Centrar()//centra el texto en las columnas de la tabla
    {
         for (int i=0;i<6;i++)  {
         tablaProcesos.getColumnModel().getColumn(i).setCellRenderer(modelocentrar);  
         }
        
    }
    
        
    //MUESTRA LOS PROCESOS EN LA TABLA
      public  void MostrarProcesos(ArrayList<Proceso> lista){
          modeloTabla.setRowCount(0);
          
        Proceso p = new Proceso();

        Object[][] data = new Object[lista.size()][6];// vector de objetos para mostrar los productos en una tabla
 



    for (int i=0;i<lista.size();i++)
    {
        data[i][0]=lista.get(i).getTiempoLLegada();
        data[i][1]=lista.get(i).getPid();        
        data[i][2]=lista.get(i).getNombreProceso();
        data[i][3]=lista.get(i).getDescripcion();
        data[i][4]=lista.get(i).getUsuarioProceso();
        data[i][5]=lista.get(i).getPrioridad();
        
           
            modeloTabla.addRow(data[i]);
    }
    tablaProcesos.setFont(new java.awt.Font("Perpetua", 0, 15));
         Centrar();    

 
    
    }

      //OPTIENE LOS PROCESOS QUE SE ESTAN EJECUTANDO EN EL SO
    private  void optenerProcesos(String linea, int pos) {
        String Pid="",NombreProceso="",UsuarioProceso="",Descripcion="";
        int Prioridad=0;
        for (int i = 0; i < linea.length(); i++) {
            
            if(i<=26){
                NombreProceso+=linea.charAt(i);
            }
            if(i>=27 && i<=34){
                Pid+=linea.charAt(i);
            }
            if(i>=88 && i<=120){
                UsuarioProceso+=linea.charAt(i);
            }
             if(i>156){
                Descripcion+=linea.charAt(i);
                  
            }
            
        }
        
        Descripcion=Descripcion.trim();
        Pid=Pid.trim();
        UsuarioProceso=UsuarioProceso.trim();
        NombreProceso= NombreProceso.trim();
         
        NombreProceso=NombreProceso.split(".exe")[0];
       
        if(Descripcion.length()<10){
            Prioridad=1;
        } 
        

        int rafaga=Descripcion.length();
        System.out.println("descripcion: "+Descripcion+ " R: "+rafaga);

        
        Proceso proceso=new Proceso(Pid, NombreProceso, UsuarioProceso, Descripcion, Prioridad, QUANTUM, "LISTO", rafaga,TH);
      
        ListaProcesos.add(proceso);
        
        GrabarProceso(proceso);
      
      
         
        if((pos-3)!=0)
        {
            // LE AGREGA tiempo de llegada al proceso = tiempo final proceso anterior + 1
            int tiempoFinalAnterior=ListaProcesos.get((pos-3)-1).getTiempoLLegada();
             ListaProcesos.get((pos-3)).setTiempoLLegada(tiempoFinalAnterior+1);
        }
        else
        {
            //ENTONCES ES EL PRIMER PROCESO
              ListaProcesos.get(0).setTiempoLLegada(0);
              //          ListaProcesos[pos-3].setTiempoLLegada(0);

        }
        
        //AGREGAMOS EL PROCESO AL SOCKET
        agregarProcesoSocket(proceso,true);
        
        
        //JOptionPane.showMessageDialog(null, ListaProcesos[pos-3].toString()); 
    }

    //AGREGAMOS EL PROCESO AL SOCKET 
    public void agregarProcesoSocket(Proceso proceso, boolean nuevo)
    {
          
         try {
         String result=  socket.enviarMensaje(proceso,nuevo,tiempo);
         
          // System.out.println("RESULT SOCKET: "+result);
         
       } catch (JSONException ex) {
           Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
       }
         
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        btnObtenerProcesos = new javax.swing.JButton();
        edtQuantum = new javax.swing.JTextField();
        edtTH = new javax.swing.JTextField();
        edtNumProcesos = new javax.swing.JTextField();
        btnIniciar = new javax.swing.JButton();
        btnPausar = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        txtSimulacionProceso = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaProcesos = new JTable(){

            public boolean isCellEditable(int rowIndex, int colIndex) {

                return false; //Las celdas no son editables.

            }
        };
        jLabel5 = new javax.swing.JLabel();
        btnSalir = new javax.swing.JToggleButton();

        jScrollPane1.setViewportView(jEditorPane1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnObtenerProcesos.setText("Obtener Procesos");
        btnObtenerProcesos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnObtenerProcesosActionPerformed(evt);
            }
        });

        edtNumProcesos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                edtNumProcesosFocusGained(evt);
            }
        });
        edtNumProcesos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtNumProcesosActionPerformed(evt);
            }
        });

        btnIniciar.setText("INICIAR");
        btnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarActionPerformed(evt);
            }
        });

        btnPausar.setText("PAUSAR");
        btnPausar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPausarActionPerformed(evt);
            }
        });

        txtSimulacionProceso.setFont(new java.awt.Font("Monospaced", 1, 18)); // NOI18N
        txtSimulacionProceso.setForeground(new java.awt.Color(0, 153, 0));
        txtSimulacionProceso.setText("Simulación en proceso...");

        jLabel1.setText("TH (milisegundos)");

        jLabel3.setText("Numero de procesos");

        jLabel4.setText("Quantum");

        tablaProcesos.setModel(modeloTabla);
        jScrollPane2.setViewportView(tablaProcesos);

        btnSalir.setText("SALIR");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(edtNumProcesos, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(25, 25, 25)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(edtQuantum, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(36, 36, 36)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(edtTH, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(49, 49, 49)
                                                .addComponent(btnObtenerProcesos))
                                            .addComponent(jLabel1)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(458, 458, 458)
                                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtSimulacionProceso))
                                .addGap(0, 200, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane2))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jSeparator2)))
                        .addGap(0, 44, Short.MAX_VALUE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(199, 199, 199)
                .addComponent(btnIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(82, 82, 82)
                .addComponent(btnPausar, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(71, 71, 71)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edtNumProcesos))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(edtQuantum)
                            .addComponent(btnObtenerProcesos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(edtTH))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPausar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addComponent(txtSimulacionProceso)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnObtenerProcesosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnObtenerProcesosActionPerformed


//        
        
        try{
               if(!edtQuantum.getText().isEmpty())
        {
              if(!edtTH.getText().isEmpty())
        {
            
            try{
            QUANTUM=Integer.parseInt(edtQuantum.getText());
            TH=Integer.parseInt(edtTH.getText()); 
            
            int num_procesos=Integer.parseInt(edtNumProcesos.getText());
            obtenerProcesos(num_procesos);
           // IniciarSimulacion(ListaProcesos,QUANTUM,TH);

            }catch(NumberFormatException e)
            {
                e.printStackTrace();
            }
            
            
        }else
        {
             JOptionPane.showMessageDialog(null,"Ingrese el TH");
        }
        }else
        {
             JOptionPane.showMessageDialog(null,"Ingrese el QUANTUM");
        }
            
        }catch(java.lang.NumberFormatException e)
        {
            JOptionPane.showMessageDialog(null,"Ingrese el numero de procesos");
        }
        
       
    }//GEN-LAST:event_btnObtenerProcesosActionPerformed

    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarActionPerformed

        
        socket.enviarEstado("SIMULACION EN PROCESO");
       
        if(Ventana.ESTADO_SIMULACION!=Constantes.PAUSA){
                      PrepararProcesos();//AQUI SE LES AGREGA EL LISTENER EN CADA PROCESO
        tiempo=0;//REINICIAMOS EL TIEMPO
 
        }
        edtNumProcesos.setEnabled(false);
        edtQuantum.setEnabled(false);
         edtTH.setEnabled(false);

        btnIniciar.setEnabled(false);
        btnPausar.setEnabled(true);
            Ventana.ESTADO_SIMULACION=Constantes.EJECUCION;
           txtSimulacionProceso.setVisible(true);
        if(hiloActual != null){
                 ejecutarProceso(false);
            }else{
                ejecutarProceso(true);
            }
     
    }//GEN-LAST:event_btnIniciarActionPerformed

    private void btnPausarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPausarActionPerformed
       
         socket.enviarEstado("SIMULACION EN PAUSA");
        btnPausar.setEnabled(false);
         edtNumProcesos.setEnabled(true);
        edtQuantum.setEnabled(true);
                   txtSimulacionProceso.setVisible(false);

         edtTH.setEnabled(true);   
         btnIniciar.setEnabled(true);
         
       Ventana.ESTADO_SIMULACION=Constantes.PAUSA;//PAUSAMOS LA SIMULACION

    }//GEN-LAST:event_btnPausarActionPerformed

    private void edtNumProcesosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_edtNumProcesosFocusGained
      
    }//GEN-LAST:event_edtNumProcesosFocusGained

    private void edtNumProcesosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtNumProcesosActionPerformed
       
    }//GEN-LAST:event_edtNumProcesosActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
     try{
                SSocket.socket.close();
                 
               this.setVisible(false);
               System.exit(0);
     }catch(IOException e)
     {
         e.printStackTrace();
     }
      
    }//GEN-LAST:event_btnSalirActionPerformed

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
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Ventana().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
     @Override
    public void notificar(Proceso proceso, int mensaje) {
        System.out.println("Proceso "+proceso.getNombreProceso()+", mensaje: "+mensaje+" Descripcion: "+proceso.getDescripcion());
        
        switch(mensaje){
            case Constantes.TERMINO_QUANTUM:
               // proceso.agregarTiempoFinal(tiempo);
                
                // Lo metemos a cola
                cola.add(proceso);
                proceso.setEstado("LISTO");
                
               // actualizarTablaCola(cola);
                
                break;
            case Constantes.TERMINO_RAFAGA:
                //proceso.agregarTiempoFinal(tiempo);
                //proceso.setTiempoFinalTotal(tiempo);
                proceso.setTiempoFinal(tiempo);
                proceso.setTurnaRound(tiempo-proceso.getTiempoInicio());
                proceso.setEstado("FINALIZADO");
                              
                //  Lo metemos a terminados
                terminados.add(proceso);
                
                if(terminados.size()==totalProcesos)
                {
                   finalizarSimulacion();
                   
                }
                
               // actualizarTablaTerminados(terminados);
                break;
                
            case Constantes.HILO_PAUSADO:
            
                 try {
         String result=  Ventana.socket.enviarMensaje(proceso,false,tiempo);
         
          // System.out.println("RESULT SOCKET: "+result);
         
       } catch (JSONException ex) {
           Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
       }
               
                this.enPausa=proceso;
                break;
        }
        
        
         agregarProcesoSocket(proceso,false);
        
        
        if(Ventana.ESTADO_SIMULACION==Constantes.EJECUCION)
        {
                    ejecutarProceso(true);

        }
        
//        if( modeloTerminados.getRowCount() == ListaProcesos.size() ){
//            btnIniciar.setEnabled(false);
//        }
    }
    
    private void ejecutarProceso(boolean nuevoProceso){
 
        if(nuevoProceso){
            if(cola.size() > 0){
                // Ejecutamos el primer proceso que esté en la lista
                Proceso proceso = cola.remove(0);

              //  proceso.agregarTiempoInicio(tiempo);

                // Si es la primera vez que se ejecuta seteamos el tiemo inicio
              if(proceso.getTiempoInicio()== -1){
                    proceso.setTiempoInicio(tiempo);
                  //  System.out.println("TIEMPO INICIIO"+tiempo);
                }


                hiloActual = new Thread( proceso );
                hiloActual.start();
            }else{
                //jTextFieldPalabra.setText("");
            }
        }else{
         
                     //   System.out.println("PROCESO NO ES NUEVO-----------------------------");
                Proceso proceso =enPausa;

               // proceso.setEstado("EJECUCION");

                hiloActual = new Thread( proceso );
                hiloActual.start();
                        
         }

    }

    @Override
    public void caracterProcesado(Proceso proceso, String texto, int pos) {
               
       try {
         String result=  Ventana.socket.enviarMensaje(proceso,false,tiempo);
         
          // System.out.println("RESULT SOCKET: "+result);
         
       } catch (JSONException ex) {
           Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
       }
       
        
        tiempo++;
        
        System.out.println("Tiempo "+tiempo+" - "+proceso.getDescripcion());
        
        // Vemos que procesos entran en este tiempo
        for(Proceso p : ListaProcesos){
            if(p.getTiempoLLegada()== tiempo){
                
                cola.add(p);
            }
        }
        
        //actualizarTablaCola(cola);
        //agregarProcesoAlHistorico(proceso.getNombre(), tiempo + "");

    }
    
  
 
    void finalizarSimulacion()
    {
        socket.enviarEstado("SIMULACION FINALIZADA");
        
        btnIniciar.setEnabled(true);
        btnPausar.setEnabled(false);
        hiloActual=null;
        edtNumProcesos.setEnabled(true);
        edtQuantum.setEnabled(true);
          txtSimulacionProceso.setVisible(false);

        edtTH.setEnabled(true); 
            Ventana.ESTADO_SIMULACION=Constantes.FINALIZADA;
       // mostrarResultado();
        terminados.clear();
        
         reiniciarTiempos();// EN CASO DE QUE SE QUIERA INICIAR DE NUEVO
        
    }
         void    mostrarResultado()
         {
             
   
         Object[] cols   = {"NOMBRE","T. LLEGADA","T. INICIO","RAFAGA","TURNROUND","T.FINAL","PRIORIDAD"} ;
         
 
           
        Proceso p = new Proceso();

        Object[][] data = new Object[terminados.size()][7];// vector de objetos para mostrar los productos en una tabla
 
        



    for (int i=0;i<terminados.size();i++)
    {
         data[i][0]=terminados.get(i).getNombreProceso();
        data[i][1]=terminados.get(i).getTiempoLLegada();
        data[i][2]=terminados.get(i).getTiempoInicio();
        data[i][3]=terminados.get(i).getRafaga();
        data[i][4]=terminados.get(i).getTurnaRound();
        data[i][5]=terminados.get(i).getTiempoFinal();
        data[i][6]=terminados.get(i).getPrioridad();      
           
     }
    
    
    JTable table = new JTable(data, cols);
    table.setPreferredScrollableViewportSize(new Dimension(500, 100));
     JOptionPane.showMessageDialog(null, new JScrollPane(table),"RESULTADOS",JOptionPane.INFORMATION_MESSAGE);  

             
             
             
         }

    private void reiniciarTiempos()
    {
        
        for(int i=0;i<ListaProcesos.size();i++)
            
        {
            ListaProcesos.get(i).reiniciarValores();
        }
        
    }
     
    public void GrabarProceso(Proceso proceso)// guardar el proceso y su descripcion en un fichero .txt
    {
        
        FileWriter fichero = null;
        PrintWriter pw = null;
		try {
                       String direccion_usuario=System.getProperty("user.dir");// obtenemos la direccion de ejecucion del programa para abrir los ficheros

			fichero = new FileWriter(direccion_usuario+"/ficheros/"+proceso.getNombreProceso()+".txt");
                         pw = new PrintWriter(fichero);


                            // Escribimos la descripcion del proceso en el fichero
                        
                                String descripcion=proceso.getDescripcion();
			 
				pw.println(descripcion);
                                
			 

			pw.close();

		} catch (Exception ex) {
			System.out.println("Error al escribir en el fichero mensajes.txt: " + ex.getMessage());
		}
        
    }
//    private void agregarProcesoAlHistorico(String nombre, String tiempo){
//        modeloHistorico.addRow(new Object[] { nombre, tiempo });
//       
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIniciar;
    private javax.swing.JButton btnObtenerProcesos;
    private javax.swing.JButton btnPausar;
    private javax.swing.JToggleButton btnSalir;
    private javax.swing.JTextField edtNumProcesos;
    private javax.swing.JTextField edtQuantum;
    private javax.swing.JTextField edtTH;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable tablaProcesos;
    private javax.swing.JLabel txtSimulacionProceso;
    // End of variables declaration//GEN-END:variables
}
