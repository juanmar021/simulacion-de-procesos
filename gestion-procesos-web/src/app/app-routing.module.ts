import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ProcesosComponent } from './views/procesos.component';
import { AppComponent } from './app.component';

const ROUTES: Routes = [
  { path: 'app-root', component: AppComponent },
  { path: 'procesos', component: ProcesosComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(ROUTES)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

//export const APP_ROUTING = RouterModule.forRoot(APP_ROUTES,{ useHash: true });