import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { semestersApi, studentsApi, ApiRequestError } from '../../api/client';
import type { SemesterResponse, StudentResponse } from '../../types/api';
import PageHeader from '../../components/ui/PageHeader';
import Breadcrumb from '../../components/ui/Breadcrumb';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import ErrorAlert from '../../components/ui/ErrorAlert';
import EmptyState from '../../components/ui/EmptyState';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import SuccessAlert from '../../components/ui/SuccessAlert';

export default function SemestersPage() {
  const { studentId } = useParams<{ studentId: string }>();
  const [student, setStudent] = useState<StudentResponse | null>(null);
  const [semesters, setSemesters] = useState<SemesterResponse[]>([]);
  const [yearFilter, setYearFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<SemesterResponse | null>(null);
  const [deleting, setDeleting] = useState(false);

  const sid = Number(studentId);

  const load = () => {
    setLoading(true);
    setError(null);
    Promise.all([
      studentsApi.getById(sid),
      semestersApi.getAll(sid, yearFilter || undefined),
    ])
      .then(([s, sems]) => { setStudent(s); setSemesters(sems); })
      .catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, [studentId, yearFilter]);

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeleting(true);
    try {
      await semestersApi.delete(sid, deleteTarget.id);
      setSemesters((prev) => prev.filter((s) => s.id !== deleteTarget.id));
      setSuccessMsg(`Semester "${deleteTarget.semesterName}" deleted.`);
      setDeleteTarget(null);
    } catch (e) {
      if (e instanceof ApiRequestError) setError(e.message);
    } finally {
      setDeleting(false);
    }
  };

  const uniqueYears = [...new Set(semesters.map((s) => s.academicYear))].sort();

  return (
    <div className="space-y-5">
      <PageHeader
        title="Semesters"
        subtitle={student ? `${student.fullName}'s academic semesters` : ''}
        breadcrumb={
          <Breadcrumb
            crumbs={[
              { label: 'Students', to: '/students' },
              { label: student?.fullName ?? '…', to: `/students/${studentId}` },
              { label: 'Semesters' },
            ]}
          />
        }
        action={
          <Link to={`/students/${studentId}/semesters/new`}>
            <Button>+ Add Semester</Button>
          </Link>
        }
      />

      {successMsg && <SuccessAlert message={successMsg} onDismiss={() => setSuccessMsg(null)} />}

      {/* Year filter */}
      {uniqueYears.length > 1 && (
        <div className="flex items-center gap-2 flex-wrap">
          <button
            onClick={() => setYearFilter('')}
            className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${!yearFilter ? 'bg-blue-600 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'}`}
          >
            All
          </button>
          {uniqueYears.map((y) => (
            <button
              key={y}
              onClick={() => setYearFilter(y)}
              className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${yearFilter === y ? 'bg-blue-600 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'}`}
            >
              {y}
            </button>
          ))}
        </div>
      )}

      {loading && <LoadingSpinner message="Loading semesters…" />}
      {error && <ErrorAlert message={error} onRetry={load} />}

      {!loading && !error && (
        semesters.length === 0 ? (
          <EmptyState
            title="No semesters yet"
            description="Add a semester to begin tracking subjects and results."
            action={
              <Link to={`/students/${studentId}/semesters/new`}>
                <Button>Add Semester</Button>
              </Link>
            }
          />
        ) : (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {semesters.map((sem) => (
              <Card key={sem.id} className="hover:border-blue-200 hover:shadow-md transition-all">
                <div className="flex items-start justify-between">
                  <div>
                    <p className="text-sm font-semibold text-slate-900">{sem.semesterName}</p>
                    <p className="text-xs text-slate-500 mt-0.5">AY {sem.academicYear}</p>
                  </div>
                  <span className="rounded-full bg-blue-50 px-2 py-0.5 text-xs font-medium text-blue-700">
                    ID {sem.id}
                  </span>
                </div>
                <div className="mt-4 flex gap-2">
                  <Link to={`/students/${studentId}/semesters/${sem.id}`} className="flex-1">
                    <Button variant="secondary" size="sm" className="w-full justify-center">View</Button>
                  </Link>
                  <Link to={`/students/${studentId}/semesters/${sem.id}/edit`}>
                    <Button variant="ghost" size="sm">Edit</Button>
                  </Link>
                  <button
                    onClick={() => setDeleteTarget(sem)}
                    className="rounded-md p-1.5 text-red-500 hover:bg-red-50 transition-colors"
                    aria-label="Delete semester"
                  >
                    <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </Card>
            ))}
          </div>
        )
      )}

      <ConfirmDialog
        open={!!deleteTarget}
        danger
        title="Delete Semester"
        message={`Delete "${deleteTarget?.semesterName} (AY ${deleteTarget?.academicYear})"? All subjects and results will be removed.`}
        confirmLabel="Delete"
        loading={deleting}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}
