import { Injectable } from '@angular/core';
import { Socket } from 'ngx-socket-io';
import { Observable } from 'rxjs';
import * as io from 'socket.io-client';
import { Mensaje } from '../models/mensaje';
import { Proceso } from '../models/proceso';
import { IP_SERVER } from '../models/constantes';
  
@Injectable()
export class ProcesoServices {
 
    
  observable:Observable<string>;
  socket:any;
  
    constructor() {
      this.socket=io(IP_SERVER);  
      //  this.socket=io('http://localhost:3000');     
     }
 
     recibirMensaje():Observable<any>{
        return  this.observable=new Observable((observer)=>{
          this.socket.on('enviarMensaje',(data)=>observer.next(data)
        );}) 
      }

      recibirEstado():Observable<any>{
        return  this.observable=new Observable((observer)=>{
          this.socket.on('cambiarEstado',(data)=>observer.next(data)
        );}) 
      }
      
      conexionPerdida():Observable<any>{
        return  this.observable=new Observable((observer)=>{
          this.socket.on('disconnect',(data)=>observer.next(data)
        );}) 
      }
 
    //This one is for send data from angular to node 
      enviarMensaje(mensaje:any){
        this.socket.emit('enviarMensaje',mensaje);
      }


 
}
 