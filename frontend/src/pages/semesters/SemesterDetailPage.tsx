import { useEffect, useState } from 'react';
import { useParams, Link, useLocation } from 'react-router-dom';
import { semestersApi, subjectsApi, resultsApi, studentsApi, ApiRequestError } from '../../api/client';
import type { SemesterResponse, SubjectResponse, AcademicResultResponse, StudentResponse } from '../../types/api';
import PageHeader from '../../components/ui/PageHeader';
import Breadcrumb from '../../components/ui/Breadcrumb';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import ErrorAlert from '../../components/ui/ErrorAlert';
import EmptyState from '../../components/ui/EmptyState';
import SuccessAlert from '../../components/ui/SuccessAlert';
import ConfirmDialog from '../../components/ui/ConfirmDialog';

function gradeColor(grade: string): string {
  switch (grade) {
    case 'A': return 'bg-green-100 text-green-800';
    case 'B': return 'bg-blue-100 text-blue-800';
    case 'C': return 'bg-yellow-100 text-yellow-800';
    case 'D': return 'bg-orange-100 text-orange-800';
    default:  return 'bg-red-100 text-red-800';
  }
}

export default function SemesterDetailPage() {
  const { studentId, semesterId } = useParams<{ studentId: string; semesterId: string }>();
  const location = useLocation();
  const sid = Number(studentId);
  const semId = Number(semesterId);

  const [student, setStudent] = useState<StudentResponse | null>(null);
  const [semester, setSemester] = useState<SemesterResponse | null>(null);
  const [subjects, setSubjects] = useState<SubjectResponse[]>([]);
  const [latestResult, setLatestResult] = useState<AcademicResultResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(
    (location.state as { success?: string } | null)?.success ?? null
  );
  const [calculating, setCalculating] = useState(false);
  const [calcError, setCalcError] = useState<string | null>(null);
  const [deleteSubjectTarget, setDeleteSubjectTarget] = useState<SubjectResponse | null>(null);
  const [deletingSubject, setDeletingSubject] = useState(false);

  const load = () => {
    setLoading(true);
    setError(null);
    Promise.all([
      studentsApi.getById(sid),
      semestersApi.getById(sid, semId),
      subjectsApi.getAll(semId),
      resultsApi.getLatest(semId).catch(() => null),
    ])
      .then(([s, sem, subs, res]) => {
        setStudent(s);
        setSemester(sem);
        setSubjects(subs);
        setLatestResult(res);
      })
      .catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, [semesterId]);

  const handleCalculate = async () => {
    setCalculating(true);
    setCalcError(null);
    try {
      const result = await resultsApi.calculate(semId);
      setLatestResult(result);
      setSuccessMsg('Academic result calculated successfully.');
    } catch (e) {
      if (e instanceof ApiRequestError) setCalcError(e.message);
      else setCalcError('Calculation failed.');
    } finally {
      setCalculating(false);
    }
  };

  const handleDeleteSubject = async () => {
    if (!deleteSubjectTarget) return;
    setDeletingSubject(true);
    try {
      await subjectsApi.delete(semId, deleteSubjectTarget.id);
      setSubjects((prev) => prev.filter((s) => s.id !== deleteSubjectTarget.id));
      setSuccessMsg(`Subject "${deleteSubjectTarget.subjectName}" deleted.`);
      setDeleteSubjectTarget(null);
    } catch (e) {
      if (e instanceof ApiRequestError) setCalcError(e.message);
    } finally {
      setDeletingSubject(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading semester…" />;
  if (error) return <ErrorAlert message={error} onRetry={load} />;
  if (!semester) return null;

  return (
    <div className="space-y-5">
      <PageHeader
        title={`Year ${semester.yearLevel} · Semester ${semester.semesterNumber}`}
        subtitle={`Year Level ${semester.yearLevel}${student ? ` · ${student.fullName}` : ''}`}
        breadcrumb={
          <Breadcrumb
            crumbs={[
              { label: 'Students', to: '/students' },
              { label: student?.fullName ?? '…', to: `/students/${studentId}` },
              { label: 'Semesters', to: `/students/${studentId}/semesters` },
              { label: `Year ${semester.yearLevel} · Sem ${semester.semesterNumber}` },
            ]}
          />
        }
        action={
          <div className="flex gap-2">
            <Link to={`/students/${studentId}/semesters/${semesterId}/edit`}>
              <Button variant="secondary" size="sm">Edit</Button>
            </Link>
            <Link to={`/semesters/${semesterId}/subjects/new`}>
              <Button size="sm">+ Add Subjects</Button>
            </Link>
          </div>
        }
      />

      {successMsg && <SuccessAlert message={successMsg} onDismiss={() => setSuccessMsg(null)} />}
      {calcError && <ErrorAlert message={calcError} />}

      {/* Latest Result Banner */}
      {latestResult && (
        <Card className="border-l-4 border-l-blue-500">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <p className="text-xs font-medium uppercase tracking-wide text-slate-500">Latest Result</p>
              <div className="flex items-center gap-3 mt-1">
                <span className={`rounded-full px-3 py-1 text-sm font-bold ${gradeColor(latestResult.grade)}`}>
                  Grade {latestResult.grade}
                </span>
                <span className="text-sm text-slate-700">Avg: <strong>{latestResult.averageMark}</strong></span>
                {latestResult.riskSubjects.length > 0 && (
                  <span className="rounded-full bg-red-100 px-2 py-0.5 text-xs font-medium text-red-700">
                    {latestResult.riskSubjects.length} at-risk
                  </span>
                )}
              </div>
              <p className="mt-1 text-xs text-slate-500 italic">"{latestResult.feedback}"</p>
            </div>
            <Link to={`/results/${latestResult.id}`}>
              <Button variant="secondary" size="sm">Full Report</Button>
            </Link>
          </div>
        </Card>
      )}

      {/* Calculate button */}
      <div className="flex items-center gap-3">
        <Button
          onClick={handleCalculate}
          loading={calculating}
          disabled={subjects.length < 5}
          icon={
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 11h.01M12 11h.01M15 11h.01M4 19h16a2 2 0 002-2V7a2 2 0 00-2-2H4a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
          }
        >
          {calculating ? 'Calculating…' : 'Calculate Result'}
        </Button>
        {subjects.length < 5 && (
          <p className="text-xs text-amber-600">Need at least 5 subjects to calculate.</p>
        )}
      </div>

      {/* Subjects table */}
      <Card padding="none">
        <div className="flex items-center justify-between px-5 py-4 border-b border-slate-100">
          <h2 className="text-sm font-semibold text-slate-900">Subjects ({subjects.length})</h2>
          <Link to={`/semesters/${semesterId}/subjects`}>
            <Button variant="ghost" size="sm">Manage</Button>
          </Link>
        </div>
        {subjects.length === 0 ? (
          <EmptyState
            title="No subjects yet"
            description="Add at least 5 subjects to enable result calculation."
            action={
              <Link to={`/semesters/${semesterId}/subjects/new`}>
                <Button size="sm">Add Subjects</Button>
              </Link>
            }
          />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-slate-200 bg-slate-50">
                  <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Code</th>
                  <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Name</th>
                  <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-slate-500">Mark</th>
                  <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-slate-500">Risk</th>
                  <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-slate-500">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {subjects.map((sub) => {
                  const isRisk = Number(sub.mark) < 40;
                  return (
                    <tr key={sub.id} className="hover:bg-slate-50 transition-colors">
                      <td className="px-4 py-3 font-mono text-xs text-slate-600">{sub.subjectCode}</td>
                      <td className="px-4 py-3 font-medium text-slate-900">{sub.subjectName}</td>
                      <td className="px-4 py-3 text-right">
                        <span className={`font-semibold ${Number(sub.mark) >= 60 ? 'text-green-700' : Number(sub.mark) >= 40 ? 'text-yellow-700' : 'text-red-700'}`}>
                          {sub.mark}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-right">
                        {isRisk && (
                          <span className="rounded-full bg-red-100 px-2 py-0.5 text-xs font-medium text-red-700">At Risk</span>
                        )}
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex justify-end gap-2">
                          <Link
                            to={`/semesters/${semesterId}/subjects/${sub.id}/edit`}
                            className="rounded-md px-2.5 py-1 text-xs font-medium text-blue-600 hover:bg-blue-50 transition-colors"
                          >
                            Edit
                          </Link>
                          <button
                            onClick={() => setDeleteSubjectTarget(sub)}
                            className="rounded-md px-2.5 py-1 text-xs font-medium text-red-600 hover:bg-red-50 transition-colors"
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </Card>

      <ConfirmDialog
        open={!!deleteSubjectTarget}
        danger
        title="Delete Subject"
        message={`Delete "${deleteSubjectTarget?.subjectName}"? This cannot be undone.`}
        confirmLabel="Delete"
        loading={deletingSubject}
        onConfirm={handleDeleteSubject}
        onCancel={() => setDeleteSubjectTarget(null)}
      />
    </div>
  );
}
