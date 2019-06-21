import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';

import { ProcesosComponent } from './views/procesos.component';
import { AppComponent } from './app.component';

const APP_ROUTES: Routes = [
    { path: 'app-root', component: AppComponent },
    { path: 'procesos', component: ProcesosComponent },
    { path: '**', redirectTo: 'home'}
];

export const APP_ROUTING = RouterModule.forRoot(APP_ROUTES,{ useHash: true });