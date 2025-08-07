import { Component, inject, Inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';
import { Task, TaskPriority } from '../../../core/models';

export interface TaskDialogData {
  task?: Task;
  mode: 'create' | 'edit';
}

@Component({
  selector: 'app-task-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data.mode === 'create' ? 'Nova Tarefa' : 'Editar Tarefa' }}</h2>
    
    <mat-dialog-content>
      <form [formGroup]="taskForm">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Título</mat-label>
          <input matInput formControlName="title" required>
          <mat-error *ngIf="taskForm.get('title')?.hasError('required')">
            Título é obrigatório
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Descrição</mat-label>
          <textarea matInput formControlName="description" rows="3" required></textarea>
          <mat-error *ngIf="taskForm.get('description')?.hasError('required')">
            Descrição é obrigatória
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width" *ngIf="data.mode === 'edit'">
          <mat-label>Prioridade</mat-label>
          <mat-select formControlName="priority">
            <mat-option value="LOW">Baixa</mat-option>
            <mat-option value="MEDIUM">Média</mat-option>
            <mat-option value="HIGH">Alta</mat-option>
          </mat-select>
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancelar</button>
      <button 
        mat-raised-button 
        color="primary" 
        (click)="onSave()"
        [disabled]="taskForm.invalid">
        {{ data.mode === 'create' ? 'Criar' : 'Salvar' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .full-width {
      width: 100%;
      margin-bottom: 16px;
    }

    mat-dialog-content {
      min-width: 400px;
    }
  `]
})
export class TaskDialogComponent {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<TaskDialogComponent>);

  taskForm: FormGroup;

  constructor(@Inject(MAT_DIALOG_DATA) public data: TaskDialogData) {
    this.taskForm = this.fb.group({
      title: [data.task?.title || '', Validators.required],
      description: [data.task?.description || '', Validators.required],
      priority: [data.task?.priority || TaskPriority.MEDIUM]
    });
  }

  onSave(): void {
    if (this.taskForm.valid) {
      const formValue = this.taskForm.value;
      this.dialogRef.close(formValue);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
