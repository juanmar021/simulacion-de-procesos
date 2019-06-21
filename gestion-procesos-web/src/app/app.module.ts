import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ProcesosComponent } from './views/procesos.component';

import { SocketIoModule, SocketIoConfig } from 'ngx-socket-io';

//const config: SocketIoConfig = { url: 'http://localhost:3000', options: {} };
//RUTAS
import { APP_ROUTING } from './app.routes';
import { ProcesoServices } from './views/proceso.service';
 

@NgModule({
  declarations: [
    AppComponent,
    ProcesosComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
   // SocketIoModule.forRoot(config),
    APP_ROUTING
  ],
  providers: [ProcesoServices],
  bootstrap: [AppComponent]
})
export class AppModule { }
