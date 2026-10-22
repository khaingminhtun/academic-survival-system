import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { resultsApi, semestersApi, studentsApi } from '../../api/client';
import type { AcademicResultResponse, SemesterResponse, StudentResponse } from '../../types/api';
import PageHeader from '../../components/ui/PageHeader';
import Breadcrumb from '../../components/ui/Breadcrumb';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import ErrorAlert from '../../components/ui/ErrorAlert';

function gradeDetails(grade: string): {
  label: string;
  color: string;
  bg: string;
  strip: string;   // solid left-border color
  pill: string;    // pill bg + text
  icon: string;    // icon fill color
} {
  const map = {
    A: { label: 'Excellent',     color: 'text-green-700',  bg: 'bg-green-50 border-green-200',   strip: 'border-green-500',  pill: 'bg-green-100 text-green-800',  icon: '#15803d' },
    B: { label: 'Good',          color: 'text-blue-700',   bg: 'bg-blue-50 border-blue-200',     strip: 'border-blue-500',   pill: 'bg-blue-100 text-blue-800',    icon: '#1d4ed8' },
    C: { label: 'Average',       color: 'text-yellow-700', bg: 'bg-yellow-50 border-yellow-200', strip: 'border-yellow-500', pill: 'bg-yellow-100 text-yellow-800', icon: '#a16207' },
    D: { label: 'Below Average', color: 'text-orange-700', bg: 'bg-orange-50 border-orange-200', strip: 'border-orange-500', pill: 'bg-orange-100 text-orange-800', icon: '#c2410c' },
    F: { label: 'Fail',          color: 'text-red-700',    bg: 'bg-red-50 border-red-200',       strip: 'border-red-500',    pill: 'bg-red-100 text-red-800',      icon: '#b91c1c' },
  } as const;
  return (
    (map as Record<string, typeof map[keyof typeof map]>)[grade] ??
    { label: grade, color: 'text-slate-700', bg: 'bg-slate-50 border-slate-200', strip: 'border-slate-400', pill: 'bg-slate-100 text-slate-700', icon: '#475569' }
  );
}

export default function ResultDetailPage() {
  const { resultId } = useParams<{ resultId: string }>();
  const [result, setResult] = useState<AcademicResultResponse | null>(null);
  const [semester, setSemester] = useState<SemesterResponse | null>(null);
  const [student, setStudent] = useState<StudentResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = () => {
    setLoading(true);
    setError(null);
    resultsApi.getById(Number(resultId))
      .then(async (r) => {
        setResult(r);
        // Resolve semester and student
        const allStudents = await studentsApi.getAll().catch(() => [] as StudentResponse[]);
        for (const s of allStudents) {
          const sems = await semestersApi.getAll(s.id).catch(() => [] as SemesterResponse[]);
          const match = sems.find((sem) => sem.id === r.semesterId);
          if (match) { setSemester(match); setStudent(s); break; }
        }
      })
      .catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, [resultId]);

  if (loading) return <LoadingSpinner message="Loading result…" />;
  if (error) return <ErrorAlert message={error} onRetry={load} />;
  if (!result) return null;

  const gd = gradeDetails(result.grade);

  return (
    <div className="space-y-5 max-w-2xl">
      <PageHeader
        title="Academic Result Report"
        subtitle={`Result ID #${result.id} · ${new Date(result.calculatedAt).toLocaleString()}`}
        breadcrumb={
          <Breadcrumb
            crumbs={[
              { label: 'Students', to: '/students' },
              ...(student ? [{ label: student.fullName, to: `/students/${student.id}` }] : []),
              ...(semester && student
                ? [{ label: semester.semesterName, to: `/students/${student.id}/semesters/${semester.id}` }]
                : []),
              { label: 'Result #' + result.id },
            ]}
          />
        }
        action={
          semester && student ? (
            <Link to={`/students/${student.id}/results`}>
              <Button variant="secondary" size="sm">All Results</Button>
            </Link>
          ) : undefined
        }
      />

      {/* Grade summary */}
      <Card className={`border-2 ${gd.bg}`}>
        <div className="flex items-center gap-6">
          <div className={`flex h-20 w-20 flex-shrink-0 items-center justify-center rounded-2xl bg-white border text-4xl font-black ${gd.color} shadow-sm`}>
            {result.grade}
          </div>
          <div className="flex-1">
            <p className={`text-lg font-bold ${gd.color}`}>{gd.label}</p>
            <p className="text-2xl font-bold text-slate-900 mt-1">
              Average: <span className={gd.color}>{result.averageMark}</span>
              <span className="text-sm font-normal text-slate-500"> / 100</span>
            </p>
          </div>
        </div>
      </Card>

      {/* Feedback message callout */}
      <div className={`flex overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm`}>
        {/* Solid left accent strip */}
        <div className={`w-1.5 flex-shrink-0 ${gd.strip}`} />

        <div className="flex flex-1 gap-4 p-5">
          {/* Quote icon */}
          <svg
            className="w-8 h-8 flex-shrink-0 mt-0.5 opacity-80"
            viewBox="0 0 24 24"
            fill={gd.icon}
            aria-hidden="true"
          >
            <path d="M4.583 17.321C3.553 16.227 3 15 3 13.011c0-3.5 2.457-6.637 6.03-8.188l.893 1.378c-3.335 1.804-3.987 4.145-4.247 5.621.537-.278 1.24-.375 1.929-.311 1.804.167 3.226 1.648 3.226 3.489a3.5 3.5 0 01-3.5 3.5c-1.073 0-2.099-.49-2.748-1.179zm10 0C13.553 16.227 13 15 13 13.011c0-3.5 2.457-6.637 6.03-8.188l.893 1.378c-3.335 1.804-3.987 4.145-4.247 5.621.537-.278 1.24-.375 1.929-.311 1.804.167 3.226 1.648 3.226 3.489a3.5 3.5 0 01-3.5 3.5c-1.073 0-2.099-.49-2.748-1.179z" />
          </svg>

          <div className="flex-1 min-w-0">
            {/* Label pill */}
            <span className={`inline-block rounded-full px-2.5 py-0.5 text-xs font-semibold mb-3 ${gd.pill}`}>
              Academic Feedback
            </span>

            {/* Feedback text */}
            <p className="text-sm font-medium leading-relaxed text-slate-700">
              {result.feedback}
            </p>
          </div>
        </div>
      </div>

      {/* Risk subjects */}
      {result.riskSubjects.length > 0 ? (
        <Card padding="none">
          <div className="px-5 py-4 border-b border-slate-100 flex items-center gap-2">
            <svg className="w-4 h-4 text-red-500" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M8.485 2.495c.673-1.167 2.357-1.167 3.03 0l6.28 10.875c.673 1.167-.17 2.625-1.516 2.625H3.72c-1.347 0-2.189-1.458-1.515-2.625L8.485 2.495zM10 5a.75.75 0 01.75.75v3.5a.75.75 0 01-1.5 0v-3.5A.75.75 0 0110 5zm0 9a1 1 0 100-2 1 1 0 000 2z" clipRule="evenodd" />
            </svg>
            <h2 className="text-sm font-semibold text-red-700">
              At-Risk Subjects ({result.riskSubjects.length})
            </h2>
          </div>
          <p className="px-5 py-2 text-xs text-slate-500 border-b border-slate-100">Subjects with mark below 40 (HIGH risk)</p>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-slate-100 bg-red-50">
                  <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-red-600">Code</th>
                  <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-red-600">Name</th>
                  <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-red-600">Mark</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-red-50">
                {result.riskSubjects.map((rs) => (
                  <tr key={rs.subjectId} className="hover:bg-red-50 transition-colors">
                    <td className="px-4 py-3 font-mono text-xs text-slate-600">{rs.subjectCode}</td>
                    <td className="px-4 py-3 font-medium text-slate-900">{rs.subjectName}</td>
                    <td className="px-4 py-3 text-right font-bold text-red-700">{rs.mark}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      ) : (
        <Card className="border-green-200">
          <div className="flex items-center gap-3">
            <svg className="w-5 h-5 text-green-500 flex-shrink-0" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.857-9.809a.75.75 0 00-1.214-.882l-3.483 4.79-1.88-1.88a.75.75 0 10-1.06 1.061l2.5 2.5a.75.75 0 001.137-.089l4-5.5z" clipRule="evenodd" />
            </svg>
            <p className="text-sm font-medium text-green-800">No at-risk subjects — all marks are above 40.</p>
          </div>
        </Card>
      )}

      {/* Metadata */}
      <Card>
        <h3 className="text-xs font-semibold uppercase tracking-wide text-slate-500 mb-3">Result Metadata</h3>
        <dl className="grid grid-cols-2 gap-3 text-sm">
          <div>
            <dt className="text-slate-500">Result ID</dt>
            <dd className="font-medium text-slate-900">#{result.id}</dd>
          </div>
          <div>
            <dt className="text-slate-500">Semester ID</dt>
            <dd className="font-medium text-slate-900">#{result.semesterId}</dd>
          </div>
          <div>
            <dt className="text-slate-500">Calculated At</dt>
            <dd className="font-medium text-slate-900">{new Date(result.calculatedAt).toLocaleString()}</dd>
          </div>
          <div>
            <dt className="text-slate-500">Risk Subjects</dt>
            <dd className="font-medium text-slate-900">{result.riskSubjects.length}</dd>
          </div>
        </dl>
      </Card>
    </div>
  );
}
