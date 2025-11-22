import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Student, DistrictStats, AtRiskResponse, ModelMetrics, InterventionRequest, InterventionResponse } from '../models/student.model';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'http://localhost:3000';

  constructor(private http: HttpClient) { }

  getStudentRisk(studentId: string): Observable<Student> {
    return this.http.get<Student>(`${this.apiUrl}/students/${studentId}/risk`);
  }

  getDistrictStats(districtName: string): Observable<DistrictStats> {
    return this.http.get<DistrictStats>(`${this.apiUrl}/districts/${districtName}/stats`);
  }

  getAtRiskStudents(threshold: number = 50): Observable<AtRiskResponse> {
    return this.http.get<AtRiskResponse>(`${this.apiUrl}/students/at-risk/all?threshold=${threshold}`);
  }

  getModelMetrics(): Observable<ModelMetrics> {
    return this.http.get<ModelMetrics>(`${this.apiUrl}/model/metrics`);
  }

  logIntervention(intervention: InterventionRequest): Observable<InterventionResponse> {
    return this.http.post<InterventionResponse>(`${this.apiUrl}/interventions`, intervention);
  }
}
