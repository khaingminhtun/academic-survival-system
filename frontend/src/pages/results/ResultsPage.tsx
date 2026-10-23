import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { resultsApi, studentsApi, semestersApi } from '../../api/client';
import type { AcademicResultResponse, StudentResponse, SemesterResponse } from '../../types/api';
import PageHeader from '../../components/ui/PageHeader';
import Breadcrumb from '../../components/ui/Breadcrumb';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import ErrorAlert from '../../components/ui/ErrorAlert';
import EmptyState from '../../components/ui/EmptyState';

function GradeBadge({ grade }: { grade: string }) {
  const colors: Record<string, string> = {
    A: 'bg-green-100 text-green-800 border-green-200',
    B: 'bg-blue-100 text-blue-800 border-blue-200',
    C: 'bg-yellow-100 text-yellow-800 border-yellow-200',
    D: 'bg-orange-100 text-orange-800 border-orange-200',
    F: 'bg-red-100 text-red-800 border-red-200',
  };
  return (
    <span className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold ${colors[grade] ?? 'bg-slate-100 text-slate-700'}`}>
      {grade}
    </span>
  );
}

export default function ResultsPage() {
  const { studentId, semesterId } = useParams<{ studentId?: string; semesterId?: string }>();
  const [results, setResults] = useState<AcademicResultResponse[]>([]);
  const [student, setStudent] = useState<StudentResponse | null>(null);
  const [semester, setSemester] = useState<SemesterResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = () => {
    setLoading(true);
    setError(null);

    if (semesterId) {
      // Semester-scoped results
      resultsApi.getHistory(Number(semesterId))
        .then(setResults)
        .catch((e: Error) => setError(e.message))
        .finally(() => setLoading(false));
    } else if (studentId) {
      // Student-scoped results
      Promise.all([
        resultsApi.getStudentResults(Number(studentId)),
        studentsApi.getById(Number(studentId)),
      ])
        .then(([res, s]) => { setResults(res); setStudent(s); })
        .catch((e: Error) => setError(e.message))
        .finally(() => setLoading(false));
    }
  };

  useEffect(() => { load(); }, [studentId, semesterId]);

  useEffect(() => {
    if (!semesterId) return;
    // Try to resolve semester info
    studentsApi.getAll().then(async (allStudents) => {
      for (const s of allStudents) {
        const sems = await semestersApi.getAll(s.id).catch(() => [] as SemesterResponse[]);
        const match = sems.find((sem) => sem.id === Number(semesterId));
        if (match) { setSemester(match); setStudent(s); break; }
      }
    }).catch(() => {});
  }, [semesterId]);

  const pageTitle = semesterId
    ? `Results · ${semester ? `Year ${semester.yearLevel} · Sem ${semester.semesterNumber}` : 'Semester ' + semesterId}`
    : `Results · ${student?.fullName ?? 'Student'}`;

  const breadcrumbs = [
    { label: 'Students', to: '/students' },
    ...(student ? [{ label: student.fullName, to: `/students/${student.id}` }] : []),
    ...(semester && student ? [{ label: `Year ${semester.yearLevel} · Sem ${semester.semesterNumber}`, to: `/students/${student.id}/semesters/${semester.id}` }] : []),
    { label: 'Results' },
  ];

  return (
    <div className="space-y-5">
      <PageHeader
        title={pageTitle}
        subtitle={`${results.length} result${results.length !== 1 ? 's' : ''} found`}
        breadcrumb={<Breadcrumb crumbs={breadcrumbs} />}
      />

      {loading && <LoadingSpinner message="Loading results…" />}
      {error && <ErrorAlert message={error} onRetry={load} />}

      {!loading && !error && results.length === 0 && (
        <EmptyState
          title="No results yet"
          description="Calculate an academic result from the semester page after adding at least 5 subjects."
        />
      )}

      {!loading && !error && results.length > 0 && (
        <div className="space-y-4">
          {results.map((r) => (
            <Card key={r.id} className="hover:border-blue-200 hover:shadow-md transition-all">
              <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <div className="flex items-center gap-4">
                  <div className="flex h-14 w-14 flex-shrink-0 items-center justify-center rounded-xl bg-slate-100 text-2xl font-bold text-slate-700">
                    {r.grade}
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <GradeBadge grade={r.grade} />
                      <span className="text-sm font-semibold text-slate-900">Average: {r.averageMark}</span>
                      {r.riskSubjects.length > 0 && (
                        <span className="rounded-full bg-red-100 px-2 py-0.5 text-xs font-medium text-red-700">
                          {r.riskSubjects.length} at-risk
                        </span>
                      )}
                    </div>
                    <p className="mt-0.5 text-xs text-slate-500 italic">"{r.feedback}"</p>
                    <p className="mt-1 text-xs text-slate-400">
                      Calculated: {new Date(r.calculatedAt).toLocaleString()}
                    </p>
                  </div>
                </div>
                <Link to={`/results/${r.id}`}>
                  <Button variant="secondary" size="sm">View Report</Button>
                </Link>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
