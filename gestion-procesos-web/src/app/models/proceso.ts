

export interface Proceso{
    

    pid:number;
    nombre:string;
    usuario:string;
    descripcion:string;
    descripcionAux:string;
    estado:string;
    rafaga:number;
    reemplazosRealizados:number;
    quantum:number;
    tiempoLlegada:number;
    tiempoInicio:number;
    tiempoFinal:number;
    prioridad:number;
    turnaRound:number;
    tiempoHilo:number;
    tiempo:number;
    
}