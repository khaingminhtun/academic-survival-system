import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { subjectsApi, semestersApi, studentsApi, ApiRequestError } from '../../api/client';
import type { SubjectResponse, SemesterResponse, StudentResponse } from '../../types/api';
import PageHeader from '../../components/ui/PageHeader';
import Breadcrumb from '../../components/ui/Breadcrumb';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import ErrorAlert from '../../components/ui/ErrorAlert';
import EmptyState from '../../components/ui/EmptyState';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import SuccessAlert from '../../components/ui/SuccessAlert';

export default function SubjectsPage() {
  const { semesterId } = useParams<{ semesterId: string }>();
  const semId = Number(semesterId);

  const [semester, setSemester] = useState<SemesterResponse | null>(null);
  const [student, setStudent] = useState<StudentResponse | null>(null);
  const [subjects, setSubjects] = useState<SubjectResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<SubjectResponse | null>(null);
  const [deleting, setDeleting] = useState(false);

  const load = () => {
    setLoading(true);
    setError(null);
    semestersApi.getAll(0).catch(() => [] as SemesterResponse[]);
    subjectsApi.getAll(semId)
      .then((subs) => {
        setSubjects(subs);
        const firstSub = subs[0];
        // Try to load semester info from subjects' semesterId
        if (firstSub) {
          return semestersApi.getById(0, firstSub.semesterId).catch(() => null);
        }
        return null;
      })
      .then(() => {
        // Fetch semester list to find which student owns this semester
        // We piggyback by checking all students (simplification)
      })
      .catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    setLoading(true);
    setError(null);
    // We need to fetch the semester info; unfortunately semester info needs studentId
    // Load subjects first, then load students to find which one matches
    Promise.all([
      subjectsApi.getAll(semId),
      studentsApi.getAll(),
    ])
      .then(async ([subs, allStudents]) => {
        setSubjects(subs);
        // Find the semester by trying each student's semesters
        for (const s of allStudents) {
          try {
            const sems = await semestersApi.getAll(s.id);
            const match = sems.find((sem) => sem.id === semId);
            if (match) {
              setSemester(match);
              setStudent(s);
              break;
            }
          } catch {
            // continue
          }
        }
      })
      .catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false));
  }, [semesterId]);

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeleting(true);
    try {
      await subjectsApi.delete(semId, deleteTarget.id);
      setSubjects((prev) => prev.filter((s) => s.id !== deleteTarget.id));
      setSuccessMsg(`"${deleteTarget.subjectName}" deleted.`);
      setDeleteTarget(null);
    } catch (e) {
      if (e instanceof ApiRequestError) setError(e.message);
    } finally {
      setDeleting(false);
    }
  };

  return (
    <div className="space-y-5">
      <PageHeader
        title="Subjects"
        subtitle={semester ? `${semester.semesterName} · AY ${semester.academicYear}${student ? ` · ${student.fullName}` : ''}` : `Semester ${semesterId}`}
        breadcrumb={
          <Breadcrumb
            crumbs={[
              { label: 'Students', to: '/students' },
              ...(student ? [{ label: student.fullName, to: `/students/${student.id}` }] : []),
              ...(semester && student ? [{ label: semester.semesterName, to: `/students/${student.id}/semesters/${semester.id}` }] : []),
              { label: 'Subjects' },
            ]}
          />
        }
        action={
          <Link to={`/semesters/${semesterId}/subjects/new`}>
            <Button>+ Add Subjects</Button>
          </Link>
        }
      />

      {successMsg && <SuccessAlert message={successMsg} onDismiss={() => setSuccessMsg(null)} />}

      {loading && <LoadingSpinner message="Loading subjects…" />}
      {error && <ErrorAlert message={error} onRetry={load} />}

      {!loading && !error && (
        subjects.length === 0 ? (
          <EmptyState
            title="No subjects yet"
            description="Add at least 5 subjects to enable result calculation."
            action={
              <Link to={`/semesters/${semesterId}/subjects/new`}>
                <Button>Add Subjects</Button>
              </Link>
            }
          />
        ) : (
          <Card padding="none">
            <div className="px-5 py-3 border-b border-slate-100 text-xs text-slate-500">
              {subjects.length} subject{subjects.length !== 1 ? 's' : ''} · {subjects.length < 5 && <span className="text-amber-600 font-medium">Need {5 - subjects.length} more for calculation</span>}
            </div>
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-slate-200 bg-slate-50">
                    <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Code</th>
                    <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Name</th>
                    <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-slate-500">Mark</th>
                    <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-slate-500">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {subjects.map((sub) => {
                    const mark = Number(sub.mark);
                    const markColor = mark >= 60 ? 'text-green-700' : mark >= 40 ? 'text-yellow-700' : 'text-red-700';
                    return (
                      <tr key={sub.id} className="hover:bg-slate-50 transition-colors">
                        <td className="px-4 py-3 font-mono text-xs text-slate-600">{sub.subjectCode}</td>
                        <td className="px-4 py-3">
                          <div>
                            <p className="font-medium text-slate-900">{sub.subjectName}</p>
                            {mark < 40 && (
                              <span className="inline-block mt-0.5 rounded-full bg-red-100 px-2 py-0.5 text-xs font-medium text-red-700">At Risk</span>
                            )}
                          </div>
                        </td>
                        <td className={`px-4 py-3 text-right font-semibold ${markColor}`}>{sub.mark}</td>
                        <td className="px-4 py-3">
                          <div className="flex justify-end gap-2">
                            <Link
                              to={`/semesters/${semesterId}/subjects/${sub.id}/edit`}
                              className="rounded-md px-2.5 py-1 text-xs font-medium text-blue-600 hover:bg-blue-50 transition-colors"
                            >
                              Edit
                            </Link>
                            <button
                              onClick={() => setDeleteTarget(sub)}
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
          </Card>
        )
      )}

      <ConfirmDialog
        open={!!deleteTarget}
        danger
        title="Delete Subject"
        message={`Delete "${deleteTarget?.subjectName}" (${deleteTarget?.subjectCode})?`}
        confirmLabel="Delete"
        loading={deleting}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}
