import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/tasks',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./auth/components/login/login.component').then(c => c.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./auth/components/register/register.component').then(c => c.RegisterComponent)
  },
  {
    path: '',
    loadComponent: () => import('./shared/components/layout/layout.component').then(c => c.LayoutComponent),
    canActivate: [authGuard],
    children: [
      {
        path: 'tasks',
        loadComponent: () => import('./tasks/components/task-list/task-list.component').then(c => c.TaskListComponent)
      }
    ]
  },
  {
    path: '**',
    redirectTo: '/tasks'
  }
];
