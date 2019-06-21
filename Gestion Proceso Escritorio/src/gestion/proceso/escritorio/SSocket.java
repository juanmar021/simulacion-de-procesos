/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestion.proceso.escritorio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SSocket {
// socket object
public  static Socket socket = null; 
public static  void main(String[] args) throws UnknownHostException,    
IOException, ClassNotFoundException, JSONException { 
//    // class instance 
//    SSocket client = new SSocket(); 
//    // socket tcp connection 
//    String ip = "127.0.0.1"; 
//    int port = 8000; 
//    client.socketConnect(ip, port); 
//    // writes and receives the message
//
//    String message = "Mensaje del cliente"; 
//    System.out.println("Sending: " + message); 
//    //String returnStr = client.echo(message); 
//    //System.out.println("Receiving: " + returnStr); 
//   getSocket().close();
    } 



// make the connection with the socket 
public void socketConnect(String ip, int port) throws UnknownHostException,    
 IOException{
    System.out.println("[Connecting to socket...]"); 
    SSocket.socket= new Socket(ip, port); 
    
    System.out.println("CORRIENDO EN EL PUERTO:"+SSocket.socket.getLocalAddress()+":"+SSocket.socket.getPort());
    }

 
public String enviarEstado(String estado)
{
        
    try {
               JSONObject obj=  new JSONObject();
         
                         
                obj.put("estado_simulacion",estado);
		 
                
         // out & in 
        PrintWriter out = new PrintWriter(getSocket().getOutputStream(),    
        true); 
        BufferedReader in = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
        // writes str in the socket and read 
        out.println(obj); 
        String returnStr = in .readLine();
      
        //in.close();
        //out.close();
        
        return returnStr; 
        } catch (IOException e) 
        {
            e.printStackTrace();
       }catch(JSONException j)
       {
           j.printStackTrace();
       }
    return null; 
}



// ENVIA UN MENSAJE AL SERVIDOR 
public String enviarMensaje(Proceso proceso, boolean nuevo, int tiempo) throws JSONException 
{ 

    
    
    try {
         JSONObject obj=  new JSONObject();
          if(nuevo)
        {                 
                obj.put("pid", proceso.getPid());
		obj.put("nombre", proceso.getNombreProceso());
                obj.put("usuario",proceso.getUsuarioProceso());
		obj.put("descripcion",proceso.getDescripcion());
                obj.put("descripcionAux",proceso.getDescripcion());
                obj.put("estado",proceso.getEstado());
                obj.put("rafaga",proceso.getRafaga());
                obj.put("reemplazosRealizados",0);
                obj.put("quantum",proceso.getQuantum());
                obj.put("tiempoLlegada",proceso.getTiempoLLegada());
                obj.put("tiempoInicio",-1);
                obj.put("tiempoFinal",-1);
                obj.put("prioridad",proceso.getPrioridad());
                obj.put("turnaRound",-1);
                obj.put("tiempoHilo",proceso.getTiempoHilo());  
                obj.put("tiempo",tiempo);
          
        }else
          {
                obj.put("pid", proceso.getPid());
 		obj.put("descripcion",proceso.getDescripcion());
                obj.put("estado",proceso.getEstado());
                obj.put("reemplazosRealizados",proceso.getReemplazosRealizados());
                obj.put("tiempoInicio",proceso.getTiempoInicio());
                obj.put("tiempoFinal",proceso.getTiempoFinal());
                obj.put("turnaRound",proceso.getTurnaRound());
                 obj.put("tiempo",tiempo);
          }
          
      

                
         // out & in 
        PrintWriter out = new PrintWriter(getSocket().getOutputStream(),    
        true); 
        BufferedReader in = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
        // writes str in the socket and read 
        out.println(obj); 
        String returnStr = in .readLine();
      
        //in.close();
        //out.close();
        
        return returnStr; 
        } catch (IOException e) 
        {
            e.printStackTrace();
       }
    return null; 
    } // get the socket instance 



private static Socket getSocket() 
{
    return socket; 
    }
}
