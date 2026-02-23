import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.development';

export interface MigrationRequest {
  sourceCode: string;
  sourceLanguage: string;
  targetLanguage: string;
}

export interface RuleReport {
  ruleName: string;
  description: string;
  type: 'APPLIED' | 'WARNING';
}

export interface MigrationResult {
  generatedCode: string;
  report: RuleReport[];
}

@Injectable({
  providedIn: 'root'
})
export class MigrationService {

  private http = inject(HttpClient);

  private readonly API_URL = '/api/v1/migrations';

  migrateCode(request: MigrationRequest): Observable<MigrationResult> {
    return this.http.post<MigrationResult>(this.API_URL, request);
  }

}
