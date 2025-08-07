import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, CreateTaskRequest, UpdateTaskRequest, UpdateTaskStatusRequest, TaskStatus } from '../../core/models';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private readonly API_URL = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) {}

  getTasks(status?: TaskStatus): Observable<Task[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<Task[]>(this.API_URL, { params });
  }

  createTask(task: CreateTaskRequest): Observable<Task> {
    return this.http.post<Task>(this.API_URL, task);
  }

  updateTask(id: string, task: UpdateTaskRequest): Observable<Task> {
    return this.http.put<Task>(`${this.API_URL}/${id}`, task);
  }

  updateTaskStatus(id: string, status: UpdateTaskStatusRequest): Observable<Task> {
    return this.http.patch<Task>(`${this.API_URL}/${id}/status`, status);
  }

  deleteTask(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
