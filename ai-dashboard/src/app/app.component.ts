import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { ApiService } from './services/api.service';
import { Student, ModelMetrics } from './models/student.model';

@Component({
  selector: 'app-root',
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'AI Early Warning Dashboard';
  
  // Statistics
  totalStudents = 0;
  highRiskCount = 0;
  moderateRiskCount = 0;
  lowRiskCount = 0;

  // Filters
  selectedDistrict = 'all';
  selectedThreshold = 50;
  districts = ['all', 'Visakhapatnam', 'Guntur', 'Krishna'];

  // Data
  allStudents: Student[] = [];
  filteredStudents: Student[] = [];
  modelMetrics?: ModelMetrics;
  
  // Intervention form
  interventionStudentId = '';
  interventionAction = '';
  interventionOfficer = '';
  outputMessage = 'Click a button to see results...';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loadAtRiskStudents();
    this.loadModelMetrics();
  }

  loadAtRiskStudents(): void {
    this.apiService.getAtRiskStudents(this.selectedThreshold).subscribe({
      next: (data) => {
        this.allStudents = data.students;
        this.filterStudents();
        this.updateStatistics();
      },
      error: (error) => {
        console.error('Error loading students:', error);
        this.outputMessage = '⚠ Error: Make sure the server is running on port 3000';
      }
    });
  }

  loadModelMetrics(): void {
    this.apiService.getModelMetrics().subscribe({
      next: (data) => {
        this.modelMetrics = data;
      },
      error: (error) => {
        console.error('Error loading metrics:', error);
      }
    });
  }

  filterStudents(): void {
    if (this.selectedDistrict === 'all') {
      this.filteredStudents = [...this.allStudents];
    } else {
      this.filteredStudents = this.allStudents.filter(
        s => s.district === this.selectedDistrict
      );
    }
  }

  updateStatistics(): void {
    this.totalStudents = this.filteredStudents.length;
    this.highRiskCount = this.filteredStudents.filter(s => s.risk === 'High').length;
    this.moderateRiskCount = this.filteredStudents.filter(s => s.risk === 'Moderate').length;
    this.lowRiskCount = this.filteredStudents.filter(s => s.risk === 'Low').length;
  }

  onDistrictChange(): void {
    this.filterStudents();
    this.updateStatistics();
  }

  onThresholdChange(): void {
    this.loadAtRiskStudents();
  }

  viewStudent(studentId: string): void {
    this.apiService.getStudentRisk(studentId).subscribe({
      next: (data) => {
        this.outputMessage = `
<strong>Student: ${studentId}</strong><br><br>
<strong>Risk Level:</strong> ${data.risk} (${data.probability}%)<br><br>
<strong>District:</strong> ${data.district} | <strong>Grade:</strong> ${data.grade}<br><br>
<strong>Risk Factors:</strong><br>${data.reasons.join('<br>')}<br><br>
<strong>Recommended Actions:</strong><br>${data.recommendations.join('<br>')}
        `.trim();
      },
      error: (error) => {
        this.outputMessage = 'Error loading student details';
      }
    });
  }

  quickIntervention(studentId: string): void {
    this.interventionStudentId = studentId;
    this.interventionAction = 'Parent meeting scheduled';
  }

  logIntervention(): void {
    if (!this.interventionStudentId || !this.interventionAction) {
      alert('Please fill in student ID and action');
      return;
    }

    const intervention = {
      studentId: this.interventionStudentId,
      action: this.interventionAction,
      actionedBy: this.interventionOfficer || 'Teacher',
      timestamp: new Date().toISOString()
    };

    this.apiService.logIntervention(intervention).subscribe({
      next: (data) => {
        this.outputMessage = `
<strong style="color: #27ae60;">✓ Intervention Recorded</strong><br><br>
<pre>${JSON.stringify(data, null, 2)}</pre>
        `.trim();
        
        // Clear form
        this.interventionStudentId = '';
        this.interventionAction = '';
      },
      error: (error) => {
        this.outputMessage = 'Error logging intervention';
      }
    });
  }

  getRiskClass(risk: string): string {
    return risk.toLowerCase() + '-risk';
  }

  getRiskBadgeClass(risk: string): string {
    return 'risk-badge ' + risk.toLowerCase();
  }
}
