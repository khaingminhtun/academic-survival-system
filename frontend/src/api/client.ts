import type { ApiError } from '../types/api';

const BASE_URL = import.meta.env.VITE_API_BASE_URL as string;

// Custom error that carries the parsed API error body
export class ApiRequestError extends Error {
  public readonly status: number;
  public readonly apiError: ApiError | null;

  constructor(status: number, apiError: ApiError | null, message: string) {
    super(message);
    this.name = 'ApiRequestError';
    this.status = status;
    this.apiError = apiError;
  }
}

async function handleResponse<T>(res: Response): Promise<T> {
  if (res.status === 204) {
    return undefined as unknown as T; // No Content
  }

  const text = await res.text();
  let parsed: unknown;
  try {
    parsed = text ? JSON.parse(text) : undefined;
  } catch {
    parsed = undefined;
  }

  if (!res.ok) {
    const apiError = (parsed as ApiError) ?? null;
    const message =
      apiError?.message ??
      `HTTP ${res.status}: ${res.statusText}`;
    throw new ApiRequestError(res.status, apiError, message);
  }

  return parsed as T;
}

function url(path: string): string {
  return `${BASE_URL}${path}`;
}

// ── Students ─────────────────────────────────────────────────────────────
import type { StudentRequest, StudentResponse } from '../types/api';

export const studentsApi = {
  getAll(): Promise<StudentResponse[]> {
    return fetch(url('/students')).then(handleResponse<StudentResponse[]>);
  },
  getById(id: number): Promise<StudentResponse> {
    return fetch(url(`/students/${id}`)).then(handleResponse<StudentResponse>);
  },
  create(data: StudentRequest): Promise<StudentResponse> {
    return fetch(url('/students'), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    }).then(handleResponse<StudentResponse>);
  },
  update(id: number, data: StudentRequest): Promise<StudentResponse> {
    return fetch(url(`/students/${id}`), {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    }).then(handleResponse<StudentResponse>);
  },
  delete(id: number): Promise<void> {
    return fetch(url(`/students/${id}`), { method: 'DELETE' }).then(
      handleResponse<void>
    );
  },
};

// ── Semesters ─────────────────────────────────────────────────────────────
import type { SemesterRequest, SemesterResponse } from '../types/api';

export const semestersApi = {
  getAll(studentId: number, yearLevel?: number): Promise<SemesterResponse[]> {
    const params = yearLevel != null ? `?yearLevel=${yearLevel}` : '';
    return fetch(url(`/students/${studentId}/semesters${params}`)).then(
      handleResponse<SemesterResponse[]>
    );
  },
  getById(studentId: number, semesterId: number): Promise<SemesterResponse> {
    return fetch(url(`/students/${studentId}/semesters/${semesterId}`)).then(
      handleResponse<SemesterResponse>
    );
  },
  create(studentId: number, data: SemesterRequest): Promise<SemesterResponse> {
    return fetch(url(`/students/${studentId}/semesters`), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    }).then(handleResponse<SemesterResponse>);
  },
  update(
    studentId: number,
    semesterId: number,
    data: SemesterRequest
  ): Promise<SemesterResponse> {
    return fetch(url(`/students/${studentId}/semesters/${semesterId}`), {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    }).then(handleResponse<SemesterResponse>);
  },
  delete(studentId: number, semesterId: number): Promise<void> {
    return fetch(url(`/students/${studentId}/semesters/${semesterId}`), {
      method: 'DELETE',
    }).then(handleResponse<void>);
  },
};

// ── Subjects ──────────────────────────────────────────────────────────────
import type { SubjectRequest, SubjectResponse } from '../types/api';

export const subjectsApi = {
  getAll(semesterId: number): Promise<SubjectResponse[]> {
    return fetch(url(`/semesters/${semesterId}/subjects`)).then(
      handleResponse<SubjectResponse[]>
    );
  },
  getById(semesterId: number, subjectId: number): Promise<SubjectResponse> {
    return fetch(url(`/semesters/${semesterId}/subjects/${subjectId}`)).then(
      handleResponse<SubjectResponse>
    );
  },
  createBulk(semesterId: number, data: SubjectRequest[]): Promise<SubjectResponse[]> {
    return fetch(url(`/semesters/${semesterId}/subjects`), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    }).then(handleResponse<SubjectResponse[]>);
  },
  update(
    semesterId: number,
    subjectId: number,
    data: SubjectRequest
  ): Promise<SubjectResponse> {
    return fetch(url(`/semesters/${semesterId}/subjects/${subjectId}`), {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    }).then(handleResponse<SubjectResponse>);
  },
  updateBulk(semesterId: number, data: SubjectRequest[]): Promise<SubjectResponse[]> {
    return fetch(url(`/semesters/${semesterId}/subjects/bulk`), {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    }).then(handleResponse<SubjectResponse[]>);
  },
  delete(semesterId: number, subjectId: number): Promise<void> {
    return fetch(url(`/semesters/${semesterId}/subjects/${subjectId}`), {
      method: 'DELETE',
    }).then(handleResponse<void>);
  },
};

// ── Academic Results ───────────────────────────────────────────────────────
import type { AcademicResultResponse, RiskSubjectResponse } from '../types/api';

export const resultsApi = {
  calculate(semesterId: number): Promise<AcademicResultResponse> {
    return fetch(url(`/semesters/${semesterId}/results/calculate`), {
      method: 'POST',
    }).then(handleResponse<AcademicResultResponse>);
  },
  getLatest(semesterId: number): Promise<AcademicResultResponse> {
    return fetch(url(`/semesters/${semesterId}/results`)).then(
      handleResponse<AcademicResultResponse>
    );
  },
  getHistory(semesterId: number): Promise<AcademicResultResponse[]> {
    return fetch(url(`/semesters/${semesterId}/results/history`)).then(
      handleResponse<AcademicResultResponse[]>
    );
  },
  getById(resultId: number): Promise<AcademicResultResponse> {
    return fetch(url(`/results/${resultId}`)).then(
      handleResponse<AcademicResultResponse>
    );
  },
  getStudentResults(studentId: number): Promise<AcademicResultResponse[]> {
    return fetch(url(`/students/${studentId}/results`)).then(
      handleResponse<AcademicResultResponse[]>
    );
  },
  getRiskSubjects(resultId: number): Promise<RiskSubjectResponse[]> {
    return fetch(url(`/results/${resultId}/risk-subjects`)).then(
      handleResponse<RiskSubjectResponse[]>
    );
  },
};
