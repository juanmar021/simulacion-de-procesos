import { Proceso } from './proceso';

export interface Mensaje
{    
    tipo:string;
    mensaje?:string;
    proceso?:Proceso;
    estado?:string;
}