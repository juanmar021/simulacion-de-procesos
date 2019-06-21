 
package gestion.proceso.escritorio;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

public class Proceso   implements Runnable{
    
    private String Pid,NombreProceso,UsuarioProceso,Descripcion,DescripcionAuxiliar,estado;
    int Rafaga,contadorReemplazo,quantum,tiempoLLegada,tiempoInicio,tiempoFinal,Prioridad, TurnaRound;
    int reemplazosRealizados;
   
    int tiempoHilo;
     private ListenerHilos listener;

    public Proceso(String Pid, String NombreProceso, String UsuarioProceso, String Descripcion, int Prioridad, int quamtum, String estado, int Rafaga,int TH) {
        this.Pid = Pid;
        this.NombreProceso = NombreProceso;
        this.UsuarioProceso = UsuarioProceso;
        this.Descripcion = Descripcion;
        this.Prioridad = Prioridad;
        this.Rafaga = Rafaga;
        this.quantum = quamtum;
         this.estado = estado;
         this.contadorReemplazo=0;
        this.reemplazosRealizados=0;
        this.tiempoInicio=-1;
        this.tiempoHilo=TH;
        DescripcionAuxiliar=Descripcion;
        
     }

    public void reiniciarValores()
    {
        this.reemplazosRealizados=0;
                this.tiempoInicio=-1;

        this.Descripcion=DescripcionAuxiliar;
     }
    public int getTiempoHilo() {
        return tiempoHilo;
    }

    public void setTiempoHilo(int tiempoHilo) {
        this.tiempoHilo = tiempoHilo;
    }
    

    public int getReemplazosRealizados() {
        return reemplazosRealizados;
    }

    public void setReemplazosRealizados(int reemplazosRealizados) {
        this.reemplazosRealizados = reemplazosRealizados;
    }

    
    public int getTurnaRound() {
        return TurnaRound;
    }

    public void setTurnaRound(int TurnaRound) {
        this.TurnaRound = TurnaRound;
    }

    
    public Proceso() {
    }

    public int getTiempoInicio() {
        return tiempoInicio;
    }

    public void setTiempoInicio(int tiempoInicio) {
        this.tiempoInicio = tiempoInicio;
    }

    public int getTiempoFinal() {
        return tiempoFinal;
    }

    public void setTiempoFinal(int tiempoFinal) {
        this.tiempoFinal = tiempoFinal;
    }


    public String getPid() {
        return Pid;
    }

    public void setPid(String Pid) {
        this.Pid = Pid;
    }

    public String getNombreProceso() {
        return NombreProceso;
    }

    public void setNombreProceso(String NombreProceso) {
        this.NombreProceso = NombreProceso;
    }

    public String getUsuarioProceso() {
        return UsuarioProceso;
    }

    public void setUsuarioProceso(String UsuarioProceso) {
        this.UsuarioProceso = UsuarioProceso;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    public int getPrioridad() {
        return Prioridad;
    }

    public void setPrioridad(int Prioridad) {
        this.Prioridad = Prioridad;
    }

    public int getRafaga() {
        return Rafaga;
    }

    public void setRafaga(int rafaga) {
        this.Rafaga = rafaga;
    }
    public void setListenerHilo(ListenerHilos listener){
        this.listener = listener;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quamtum) {
        this.quantum = quamtum;
    }

    public int getContadorReemplazo() {
        return contadorReemplazo;
    }

    public void setContadorReemplazo(int contadorReemplazo) {
        this.contadorReemplazo = contadorReemplazo;
    }

    

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getTiempoLLegada() {
        return tiempoLLegada;
    }

    public void setTiempoLLegada(int tiempoLLegada) {
        this.tiempoLLegada = tiempoLLegada;
    }
    

    @Override
    public String toString() {
        return ("PID:"+Pid+"_"+NombreProceso+"_Usuario:"+UsuarioProceso+"_Descripcion:"+Descripcion+"_#Carat:"+Rafaga);
    }
    
    
    @Override
    public void run() { 
       
        int i = 0;
         this.estado="EJECUCION";
        System.out.println("ESTADO:::"+Ventana.ESTADO_SIMULACION);
         // Mientras no se supere el quantum, tampoco se supere el tamaño del texto, o
        // mientras no se haya alcanzado el numero de refagas, o si el proceso es no expulsivo
        while((contadorReemplazo < quantum && reemplazosRealizados < Rafaga) || Prioridad ==1  && i < Descripcion.length()){
          
           if(Ventana.ESTADO_SIMULACION==Constantes.EJECUCION)
        {
                listener.caracterProcesado(this, Descripcion, reemplazosRealizados);
                
                 StringBuilder stringBuilder = new StringBuilder(Descripcion);
                stringBuilder.setCharAt(reemplazosRealizados, '*');// cambiamos el caracter 's' por el *x'
                Descripcion = stringBuilder.toString();
                
                reemplazosRealizados++;
                contadorReemplazo++;


       
                try {
                   
                    Thread.sleep(tiempoHilo);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Proceso.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            

            i++;
        }else
           {
                                    this.estado="PAUSA";

               break;
           }
        }
        
        
        int mensaje;
        
        if( reemplazosRealizados == Rafaga)
        {
            mensaje=Constantes.TERMINO_RAFAGA;
            contadorReemplazo=0;
        }
        else if(Ventana.ESTADO_SIMULACION==Constantes.PAUSA)
        {
            
            mensaje=Constantes.HILO_PAUSADO;
        }
        else
        {
            contadorReemplazo=0;
            mensaje= Constantes.TERMINO_QUANTUM;
        }
        
       
             listener.notificar(this,mensaje);

        
    }
    
}
