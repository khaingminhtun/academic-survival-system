import { useEffect, useState } from 'react';
import { useParams, Link, useLocation } from 'react-router-dom';
import { studentsApi, semestersApi } from '../../api/client';
import type { StudentResponse, SemesterResponse } from '../../types/api';
import PageHeader from '../../components/ui/PageHeader';
import Breadcrumb from '../../components/ui/Breadcrumb';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import ErrorAlert from '../../components/ui/ErrorAlert';
import EmptyState from '../../components/ui/EmptyState';
import SuccessAlert from '../../components/ui/SuccessAlert';

export default function StudentDetailPage() {
  const { studentId } = useParams<{ studentId: string }>();
  const location = useLocation();

  const [student, setStudent] = useState<StudentResponse | null>(null);
  const [semesters, setSemesters] = useState<SemesterResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(
    (location.state as { success?: string } | null)?.success ?? null
  );

  const load = () => {
    if (!studentId) return;
    setLoading(true);
    setError(null);
    Promise.all([
      studentsApi.getById(Number(studentId)),
      semestersApi.getAll(Number(studentId)),
    ])
      .then(([s, sems]) => { setStudent(s); setSemesters(sems); })
      .catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, [studentId]);

  if (loading) return <LoadingSpinner message="Loading student…" />;
  if (error) return <ErrorAlert message={error} onRetry={load} />;
  if (!student) return null;

  return (
    <div className="space-y-5">
      <PageHeader
        title={student.fullName}
        subtitle={`${student.studentId} · ${student.department}`}
        breadcrumb={<Breadcrumb crumbs={[{ label: 'Students', to: '/students' }, { label: student.fullName }]} />}
        action={
          <div className="flex gap-2">
            <Link to={`/students/${studentId}/edit`}>
              <Button variant="secondary" size="sm">Edit</Button>
            </Link>
            <Link to={`/students/${studentId}/semesters`}>
              <Button size="sm">View Semesters</Button>
            </Link>
          </div>
        }
      />

      {successMsg && <SuccessAlert message={successMsg} onDismiss={() => setSuccessMsg(null)} />}

      {/* Info card */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        {[
          { label: 'Student ID', value: student.studentId },
          { label: 'Full Name', value: student.fullName },
          { label: 'Department', value: student.department },
        ].map((item) => (
          <Card key={item.label}>
            <p className="text-xs font-medium uppercase tracking-wide text-slate-500">{item.label}</p>
            <p className="mt-1 text-sm font-semibold text-slate-900">{item.value}</p>
          </Card>
        ))}
      </div>

      {/* Semesters list */}
      <Card padding="none">
        <div className="flex items-center justify-between px-5 py-4 border-b border-slate-100">
          <h2 className="text-sm font-semibold text-slate-900">Semesters ({semesters.length})</h2>
          <Link to={`/students/${studentId}/semesters/new`}>
            <Button size="sm">+ Add Semester</Button>
          </Link>
        </div>
        {semesters.length === 0 ? (
          <EmptyState
            title="No semesters yet"
            description="Add a semester to start tracking academic results."
            action={
              <Link to={`/students/${studentId}/semesters/new`}>
                <Button size="sm">Add Semester</Button>
              </Link>
            }
          />
        ) : (
          <ul className="divide-y divide-slate-100">
            {semesters.map((sem) => (
              <li key={sem.id}>
                <Link
                  to={`/students/${studentId}/semesters/${sem.id}`}
                  className="flex items-center justify-between px-5 py-3.5 hover:bg-slate-50 transition-colors"
                >
                  <div>
                    <p className="text-sm font-medium text-slate-900">{sem.semesterName}</p>
                    <p className="text-xs text-slate-500">AY {sem.academicYear}</p>
                  </div>
                  <svg className="w-4 h-4 text-slate-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M9 5l7 7-7 7" />
                  </svg>
                </Link>
              </li>
            ))}
          </ul>
        )}
      </Card>

      {/* Results shortcut */}
      <Card>
        <p className="text-sm font-semibold text-slate-900 mb-2">Academic Results</p>
        <Link to={`/students/${studentId}/results`}>
          <Button variant="secondary" size="sm">View All Results</Button>
        </Link>
      </Card>
    </div>
  );
}
