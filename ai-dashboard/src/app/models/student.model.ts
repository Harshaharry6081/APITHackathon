export interface Student {
  id: string;
  risk: 'High' | 'Moderate' | 'Low';
  probability: number;
  district: string;
  grade: number;
  reasons: string[];
  recommendations: string[];
}

export interface DistrictStats {
  district: string;
  totalStudents: number;
  highRisk: number;
  moderateRisk: number;
  lowRisk: number;
  averageRiskProbability: number;
}

export interface AtRiskResponse {
  count: number;
  threshold: number;
  students: Student[];
}

export interface ModelMetrics {
  model_accuracy: number;
  inclusion_error: number;
  exclusion_error: number;
  poc_criteria_met: boolean;
}

export interface InterventionRequest {
  studentId: string;
  action: string;
  actionedBy?: string;
  timestamp?: string;
}

export interface InterventionResponse {
  status: string;
  intervention: {
    studentId: string;
    action: string;
    actionedBy: string;
    timestamp: string;
    status: string;
  };
  message: string;
}
