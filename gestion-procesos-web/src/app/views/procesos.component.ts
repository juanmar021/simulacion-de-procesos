import { Component, OnInit, ViewChild } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { ProcesoServices } from './proceso.service';
import { Mensaje } from '../models/mensaje';
import { CONECTION_SERVER_ON, CONECTION_USER_JAVA_ON, CONECTION_USER_JAVA_OFF } from '../models/constantes';
import { Proceso } from '../models/proceso';
import Chart from 'chart.js';

@Component({
  selector: 'app-procesos',
  templateUrl: './procesos.component.html',
  styles: ['./procesos.component.css']
})
export class ProcesosComponent implements OnInit {

  procesos: Proceso[] = new Array();
  procesosTerminados: Proceso[] = new Array();

  conexionServer: boolean = false;
  conexionUserJava: boolean = false;
  mensaje: Mensaje;
  estado_simulacion: string = "SIMULACIÓN SIN INICIAR";
  tiempoHilo=0;
  tiempoSimulacion=0;

  @ViewChild('canvasGraf') canvas;

  public curvas = [];
  public etiquetas = [];
  public grafica;
  graficar: boolean = false;

  constructor(private socket: ProcesoServices) {


    this.socket.conexionPerdida().subscribe(data => {
      // console.log(data);
      this.conexionServer = false;


    });

    this.socket.recibirEstado().subscribe(data => {
      //console.log(data);
      this.estado_simulacion = data.estado;
      if(data.estado=='SIMULACION EN PROCESO')
      {
        if( this.procesosTerminados.length>0)
        {
            this.procesosTerminados.length=0;
        }
       

      }
      if(data.estado=='SIMULACION FINALIZADA')
      {
        
      }
      console.log(data.estado);


    });

    this.socket.recibirMensaje().subscribe(data => {
      //  console.log(data);

      this.mensaje = data;
      if (this.mensaje.tipo == 'INFO') {

        switch (this.mensaje.mensaje) {
          case CONECTION_SERVER_ON:
            this.conexionServer = true;
            break;

          case CONECTION_USER_JAVA_ON:
            this.conexionUserJava = true;
            break;
          case CONECTION_USER_JAVA_OFF:
            this.conexionUserJava = false;
            break;
        }


      } else {

        //  console.log(this.mensaje);

        this.agregarProceso(this.mensaje.proceso);



      }
    });

  }

  cambiarValores(pActual: Proceso, pNuevo: Proceso) {



    pActual.descripcion = pNuevo.descripcion;
    pActual.estado = pNuevo.estado;
    pActual.reemplazosRealizados = pNuevo.reemplazosRealizados;
    pActual.tiempoInicio = pNuevo.tiempoInicio;
    pActual.tiempoFinal = pNuevo.tiempoFinal;
    pActual.turnaRound = pNuevo.turnaRound;



  }


  //Busca un proceso terminado, lo quita de la lista procesos y lo agrega a la lista terminado
  getProceso(pid: number) {

    let i = 0;
    for (let proceso of this.procesos) {

      if (pid == proceso.pid) {
        this.procesos.splice(i, 1);
        return proceso;

      }
      i++;
    }

  }
  agregarProceso(proceso_new: Proceso) {

    if (proceso_new.estado == "FINALIZADO") {
      let proceso = this.getProceso(proceso_new.pid);
      proceso.tiempoFinal = proceso_new.tiempoFinal * proceso.tiempoHilo;
      proceso.turnaRound = proceso_new.turnaRound;
      this.tiempoSimulacion=(proceso_new.tiempo*this.tiempoHilo)/1000;


      this.procesosTerminados.push(proceso);
    } else {
      let i = 0;
      let procesoNuevo = true;
      for (let proceso of this.procesos) {

        if (proceso_new.pid == proceso.pid) {

          this.cambiarValores(this.procesos[i], proceso_new);
          this.tiempoSimulacion=(proceso_new.tiempo*this.tiempoHilo)/1000;
          procesoNuevo = false;
        }


        i++;


      }


      if (procesoNuevo) {
        if(this.procesos.length==0)
        {
          this.tiempoHilo=proceso_new.tiempoHilo;
        }
        this.procesos.push(proceso_new);
      }


    }



  }

  ordenarProcesosTerminados()
  {
   let  procesos: Proceso[] = new Array();

   
   for(let i=0;i<this.procesosTerminados.length;i++)
   {

    for(let proceso of this.procesosTerminados)
    {
       if(proceso.tiempoLlegada==i)
     {
       procesos.push(proceso);
     }
    }
  
   }
   return procesos;
  }
  public crearGrafica(){

    if (this.procesos.length==0 && this.procesosTerminados.length > 0) {
      let procesos=this.ordenarProcesosTerminados();
      for (let proceso of procesos) {
        this.curvas.push(proceso.turnaRound);
        this.etiquetas.push(proceso.nombre);
      }
  
      let speedData = {
        labels: this.etiquetas,
        datasets: [{
          label: "Proceso vs TurnaRound",
          data: this.curvas,
          lineTension: 0,
          fill: false,
          borderColor: 'orange',
          backgroundColor: 'transparent',
          borderDash: [5, 5],
          pointBorderColor: 'orange',
          pointBackgroundColor: 'rgba(255,150,0,0.5)',
          pointRadius: 5,
          pointHoverRadius: 10,
          pointHitRadius: 30,
          pointBorderWidth: 2,
          pointStyle: 'rectRounded'
        }]
      };
  
      this.grafica = new Chart(this.canvas.nativeElement, {
        type: 'line',
        data: speedData
      });
      
    }
    
  }

  ngOnInit() {

  }
  sumit() {
    //console.log("enciando mensaje")

    //this.socket.enviarMensaje("HOLLLL");

  }

}
