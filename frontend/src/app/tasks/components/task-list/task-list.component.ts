import { Component, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { CommonModule } from '@angular/common';
import { TaskService } from '../../services/task.service';
import { Task, TaskStatus, CreateTaskRequest, UpdateTaskRequest } from '../../../core/models';
import { TaskItemComponent } from '../task-item/task-item.component';
import { TaskDialogComponent, TaskDialogData } from '../task-dialog/task-dialog.component';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    TaskItemComponent
  ],
  template: `
    <div class="task-list-container">
      <div class="header">
        <h1>Minhas Tarefas</h1>
        <button mat-fab color="primary" (click)="openCreateDialog()" title="Nova Tarefa">
          <mat-icon>add</mat-icon>
        </button>
      </div>

      <div class="filters">
        <mat-chip-listbox aria-label="Status Filter" [value]="selectedFilter" (selectionChange)="onFilterChange($event)">
          <mat-chip-option value="all">
            Todas ({{ allTasks().length }})
          </mat-chip-option>
          <mat-chip-option value="pending">
            Pendentes ({{ pendingCount() }})
          </mat-chip-option>
          <mat-chip-option value="completed">
            Concluídas ({{ completedCount() }})
          </mat-chip-option>
        </mat-chip-listbox>
      </div>

      <div class="content" *ngIf="!isLoading(); else loadingTemplate">
        <div *ngIf="filteredTasks().length === 0" class="empty-state">
          <mat-icon class="empty-icon">assignment</mat-icon>
          <h2>{{ getEmptyMessage() }}</h2>
          <p>{{ getEmptySubMessage() }}</p>
          <button mat-raised-button color="primary" (click)="openCreateDialog()" *ngIf="selectedFilter === 'all'">
            Criar primeira tarefa
          </button>
        </div>

        <div class="tasks-grid" *ngIf="filteredTasks().length > 0">
          <app-task-item
            *ngFor="let task of filteredTasks(); trackBy: trackByTaskId"
            [task]="task"
            (edit)="openEditDialog($event)"
            (delete)="deleteTask($event)"
            (toggleStatus)="toggleTaskStatus($event)">
          </app-task-item>
        </div>
      </div>

      <ng-template #loadingTemplate>
        <div class="loading-container">
          <mat-spinner></mat-spinner>
          <p>Carregando tarefas...</p>
        </div>
      </ng-template>
    </div>
  `,
  styles: [`
    .task-list-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 20px;
    }

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
    }

    .header h1 {
      margin: 0;
      color: #333;
    }

    .filters {
      margin-bottom: 24px;
    }

    .filters mat-chip-listbox {
      display: flex;
      gap: 8px;
    }

    .content {
      min-height: 400px;
    }

    .tasks-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 16px;
    }

    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 60px 20px;
      text-align: center;
      color: #666;
    }

    .empty-icon {
      font-size: 72px;
      width: 72px;
      height: 72px;
      margin-bottom: 16px;
      opacity: 0.5;
    }

    .empty-state h2 {
      margin: 0 0 8px 0;
      color: #333;
    }

    .empty-state p {
      margin: 0 0 24px 0;
      font-size: 14px;
    }

    .loading-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 60px 20px;
    }

    .loading-container p {
      margin-top: 16px;
      color: #666;
    }

    @media (max-width: 768px) {
      .task-list-container {
        padding: 16px;
      }

      .tasks-grid {
        grid-template-columns: 1fr;
      }

      .header {
        flex-direction: column;
        gap: 16px;
        text-align: center;
      }
    }
  `]
})
export class TaskListComponent implements OnInit {
  private taskService = inject(TaskService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  allTasks = signal<Task[]>([]);
  filteredTasks = signal<Task[]>([]);
  isLoading = signal(false);
  selectedFilter: 'all' | 'pending' | 'completed' = 'all';

  pendingCount = signal(0);
  completedCount = signal(0);

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.isLoading.set(true);
    this.taskService.getTasks().subscribe({
      next: (tasks) => {
        this.allTasks.set(tasks);
        this.updateCounts();
        this.applyFilter();
      },
      error: (error) => {
        this.snackBar.open('Erro ao carregar tarefas', 'Fechar', { duration: 3000 });
        console.error('Error loading tasks:', error);
      },
      complete: () => {
        this.isLoading.set(false);
      }
    });
  }

  filterTasks(filter: 'all' | 'pending' | 'completed'): void {
    this.selectedFilter = filter;
    this.applyFilter();
  }

  onFilterChange(event: any): void {
    const value = event.value;
    if (value) {
      this.filterTasks(value);
    }
  }

  private applyFilter(): void {
    const tasks = this.allTasks();
    switch (this.selectedFilter) {
      case 'pending':
        this.filteredTasks.set(tasks.filter(task => task.status === TaskStatus.PENDING));
        break;
      case 'completed':
        this.filteredTasks.set(tasks.filter(task => task.status === TaskStatus.COMPLETED));
        break;
      default:
        this.filteredTasks.set(tasks);
    }
  }

  private updateCounts(): void {
    const tasks = this.allTasks();
    this.pendingCount.set(tasks.filter(task => task.status === TaskStatus.PENDING).length);
    this.completedCount.set(tasks.filter(task => task.status === TaskStatus.COMPLETED).length);
  }

  openCreateDialog(): void {
    const dialogRef = this.dialog.open(TaskDialogComponent, {
      data: { mode: 'create' } as TaskDialogData,
      width: '500px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.createTask(result);
      }
    });
  }

  openEditDialog(task: Task): void {
    const dialogRef = this.dialog.open(TaskDialogComponent, {
      data: { task, mode: 'edit' } as TaskDialogData,
      width: '500px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.updateTask(task.id, result);
      }
    });
  }

  createTask(taskData: CreateTaskRequest): void {
    this.taskService.createTask(taskData).subscribe({
      next: (newTask) => {
        const updatedTasks = [...this.allTasks(), newTask];
        this.allTasks.set(updatedTasks);
        this.updateCounts();
        this.applyFilter();
        this.snackBar.open('Tarefa criada com sucesso!', 'Fechar', { duration: 3000 });
      },
      error: (error) => {
        this.snackBar.open('Erro ao criar tarefa', 'Fechar', { duration: 3000 });
        console.error('Error creating task:', error);
      }
    });
  }

  updateTask(id: string, taskData: UpdateTaskRequest): void {
    this.taskService.updateTask(id, taskData).subscribe({
      next: (updatedTask) => {
        const tasks = this.allTasks().map(task => 
          task.id === id ? updatedTask : task
        );
        this.allTasks.set(tasks);
        this.updateCounts();
        this.applyFilter();
        this.snackBar.open('Tarefa atualizada com sucesso!', 'Fechar', { duration: 3000 });
      },
      error: (error) => {
        this.snackBar.open('Erro ao atualizar tarefa', 'Fechar', { duration: 3000 });
        console.error('Error updating task:', error);
      }
    });
  }

  deleteTask(id: string): void {
    if (confirm('Tem certeza que deseja excluir esta tarefa?')) {
      this.taskService.deleteTask(id).subscribe({
        next: () => {
          const tasks = this.allTasks().filter(task => task.id !== id);
          this.allTasks.set(tasks);
          this.updateCounts();
          this.applyFilter();
          this.snackBar.open('Tarefa excluída com sucesso!', 'Fechar', { duration: 3000 });
        },
        error: (error) => {
          this.snackBar.open('Erro ao excluir tarefa', 'Fechar', { duration: 3000 });
          console.error('Error deleting task:', error);
        }
      });
    }
  }

  toggleTaskStatus(event: { id: string, status: TaskStatus }): void {
    this.taskService.updateTaskStatus(event.id, { status: event.status }).subscribe({
      next: (updatedTask) => {
        const tasks = this.allTasks().map(task => 
          task.id === event.id ? updatedTask : task
        );
        this.allTasks.set(tasks);
        this.updateCounts();
        this.applyFilter();
        
        const message = event.status === TaskStatus.COMPLETED 
          ? 'Tarefa marcada como concluída!' 
          : 'Tarefa marcada como pendente!';
        this.snackBar.open(message, 'Fechar', { duration: 3000 });
      },
      error: (error) => {
        this.snackBar.open('Erro ao atualizar status da tarefa', 'Fechar', { duration: 3000 });
        console.error('Error updating task status:', error);
      }
    });
  }

  trackByTaskId(index: number, task: Task): string {
    return task.id;
  }

  getEmptyMessage(): string {
    switch (this.selectedFilter) {
      case 'pending':
        return 'Nenhuma tarefa pendente';
      case 'completed':
        return 'Nenhuma tarefa concluída';
      default:
        return 'Nenhuma tarefa encontrada';
    }
  }

  getEmptySubMessage(): string {
    switch (this.selectedFilter) {
      case 'pending':
        return 'Todas as suas tarefas foram concluídas! 🎉';
      case 'completed':
        return 'Ainda não há tarefas concluídas.';
      default:
        return 'Comece criando sua primeira tarefa.';
    }
  }
}
