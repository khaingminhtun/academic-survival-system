// Types matching the Spring Boot backend DTOs exactly

// ── Student ──────────────────────────────────────────────────────────────
export interface StudentResponse {
  id: number;
  studentId: string;
  fullName: string;
  department: string;
}

export interface StudentRequest {
  studentId: string;
  fullName: string;
  department: string;
}

// ── Semester ─────────────────────────────────────────────────────────────
export interface SemesterResponse {
  id: number;
  studentId: number;
  yearLevel: number;      // 1–6
  semesterNumber: number; // 1 or 2
}

export interface SemesterRequest {
  yearLevel: number;      // 1–6
  semesterNumber: number; // 1 or 2
}

// ── Subject ──────────────────────────────────────────────────────────────
export interface SubjectResponse {
  id: number;
  semesterId: number;
  subjectCode: string;
  subjectName: string;
  mark: number; // BigDecimal serialized as number in JSON
}

export interface SubjectRequest {
  subjectId?: number | null; // optional – required for bulk-update only
  subjectCode: string;
  subjectName: string;
  mark: number;
}

// ── Academic Result ──────────────────────────────────────────────────────
export interface RiskSubjectResponse {
  subjectId: number;
  subjectCode: string;
  subjectName: string;
  mark: number;
}

export interface AcademicResultResponse {
  id: number;
  semesterId: number;
  averageMark: number;
  grade: string;          // A | B | C | D | F
  riskSubjects: RiskSubjectResponse[];
  feedback: string;
  calculatedAt: string;   // ISO-8601 LocalDateTime
}

// ── Error ─────────────────────────────────────────────────────────────────
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors: Record<string, string>;
}
