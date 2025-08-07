import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { CommonModule } from '@angular/common';
import { Task, TaskStatus, TaskPriority } from '../../../core/models';

@Component({
  selector: 'app-task-item',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatCheckboxModule,
    MatChipsModule
  ],
  template: `
    <mat-card class="task-card" [class.completed]="task.status === 'COMPLETED'">
      <mat-card-content>
        <div class="task-header">
          <mat-checkbox 
            [checked]="task.status === 'COMPLETED'"
            (change)="onToggleStatus()"
            color="primary">
          </mat-checkbox>
          <h3 class="task-title" [class.completed-text]="task.status === 'COMPLETED'">
            {{ task.title }}
          </h3>
          <mat-chip 
            [class]="'priority-' + task.priority.toLowerCase()"
            class="priority-chip">
            {{ getPriorityLabel(task.priority) }}
          </mat-chip>
        </div>
        
        <p class="task-description" [class.completed-text]="task.status === 'COMPLETED'">
          {{ task.description }}
        </p>
        
        <div class="task-actions">
          <button 
            mat-icon-button 
            color="primary" 
            (click)="onEdit()"
            [disabled]="task.status === 'COMPLETED'"
            title="Editar">
            <mat-icon>edit</mat-icon>
          </button>
          <button 
            mat-icon-button 
            color="warn" 
            (click)="onDelete()"
            title="Excluir">
            <mat-icon>delete</mat-icon>
          </button>
        </div>
      </mat-card-content>
    </mat-card>
  `,
  styles: [`
    .task-card {
      margin-bottom: 16px;
      transition: all 0.3s ease;
    }

    .task-card:hover {
      box-shadow: 0 4px 8px rgba(0,0,0,0.12);
      transform: translateY(-2px);
    }

    .task-card.completed {
      opacity: 0.7;
      background-color: #f5f5f5;
    }

    .task-header {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      margin-bottom: 12px;
    }

    .task-title {
      flex: 1;
      margin: 0;
      font-size: 16px;
      font-weight: 500;
    }

    .task-description {
      margin: 0 0 16px 0;
      color: #666;
      line-height: 1.4;
    }

    .completed-text {
      text-decoration: line-through;
    }

    .task-actions {
      display: flex;
      justify-content: flex-end;
      gap: 8px;
    }

    .priority-chip {
      font-size: 12px;
      min-height: 24px;
    }

    .priority-high {
      background-color: #f44336;
      color: white;
    }

    .priority-medium {
      background-color: #ff9800;
      color: white;
    }

    .priority-low {
      background-color: #4caf50;
      color: white;
    }
  `]
})
export class TaskItemComponent {
  @Input({ required: true }) task!: Task;
  @Output() edit = new EventEmitter<Task>();
  @Output() delete = new EventEmitter<string>();
  @Output() toggleStatus = new EventEmitter<{ id: string, status: TaskStatus }>();

  onEdit(): void {
    this.edit.emit(this.task);
  }

  onDelete(): void {
    this.delete.emit(this.task.id);
  }

  onToggleStatus(): void {
    const newStatus = this.task.status === TaskStatus.COMPLETED 
      ? TaskStatus.PENDING 
      : TaskStatus.COMPLETED;
    
    this.toggleStatus.emit({ id: this.task.id, status: newStatus });
  }

  getPriorityLabel(priority: TaskPriority): string {
    const labels = {
      [TaskPriority.LOW]: 'Baixa',
      [TaskPriority.MEDIUM]: 'Média',
      [TaskPriority.HIGH]: 'Alta'
    };
    return labels[priority];
  }
}
