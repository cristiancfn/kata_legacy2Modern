import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MigrationRequest, MigrationResult, MigrationService } from './services/migration.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  private migrationService = inject(MigrationService);

  sourceLanguage = 'COBOL';
  targetLanguage = 'JAVA';
  sourceCode = '';

  result: MigrationResult | null = null;
  isLoading = false;
  errorMessage = '';

  onMigrate() {
    if (!this.sourceCode.trim()) {
      this.errorMessage = 'Por favor, ingresar el código fuente';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.result = null;

    const request: MigrationRequest = {
      sourceCode: this.sourceCode,
      sourceLanguage: this.sourceLanguage,
      targetLanguage: this.targetLanguage
    };

    this.migrationService.migrateCode(request).subscribe({
      next: (response) => {
        this.result = response;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Ocurrió un error al procesar laa migración. Verificar la consola.';
        this.isLoading = false;
      }
    });
  }
}
