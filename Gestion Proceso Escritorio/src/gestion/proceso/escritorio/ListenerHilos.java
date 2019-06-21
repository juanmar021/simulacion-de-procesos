 
package gestion.proceso.escritorio;

public interface ListenerHilos {
    void notificar(Proceso proceso, int mensaje);
    void caracterProcesado(Proceso proceso, String texto, int pos);
}