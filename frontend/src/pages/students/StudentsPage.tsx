import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { studentsApi, ApiRequestError } from '../../api/client';
import type { StudentResponse } from '../../types/api';
import PageHeader from '../../components/ui/PageHeader';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import ErrorAlert from '../../components/ui/ErrorAlert';
import EmptyState from '../../components/ui/EmptyState';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import SuccessAlert from '../../components/ui/SuccessAlert';

export default function StudentsPage() {
  const [students, setStudents] = useState<StudentResponse[]>([]);
  const [filtered, setFiltered] = useState<StudentResponse[]>([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  const [deleteTarget, setDeleteTarget] = useState<StudentResponse | null>(null);
  const [deleting, setDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState<string | null>(null);

  const load = () => {
    setLoading(true);
    setError(null);
    studentsApi.getAll()
      .then((data) => { setStudents(data); setFiltered(data); })
      .catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  useEffect(() => {
    const q = search.toLowerCase().trim();
    setFiltered(
      q
        ? students.filter(
            (s) =>
              s.fullName.toLowerCase().includes(q) ||
              s.studentId.toLowerCase().includes(q) ||
              s.department.toLowerCase().includes(q)
          )
        : students
    );
  }, [search, students]);

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeleting(true);
    setDeleteError(null);
    try {
      await studentsApi.delete(deleteTarget.id);
      setStudents((prev) => prev.filter((s) => s.id !== deleteTarget.id));
      setSuccessMsg(`Student "${deleteTarget.fullName}" deleted successfully.`);
      setDeleteTarget(null);
    } catch (e) {
      if (e instanceof ApiRequestError) setDeleteError(e.message);
      else setDeleteError('Delete failed. Please try again.');
    } finally {
      setDeleting(false);
    }
  };

  return (
    <div className="space-y-5">
      <PageHeader
        title="Students"
        subtitle={`${students.length} student${students.length !== 1 ? 's' : ''} registered`}
        action={
          <Link to="/students/new">
            <Button
              icon={
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" />
                </svg>
              }
            >
              Add Student
            </Button>
          </Link>
        }
      />

      {successMsg && (
        <SuccessAlert message={successMsg} onDismiss={() => setSuccessMsg(null)} />
      )}

      {loading && <LoadingSpinner message="Loading students…" />}
      {error && <ErrorAlert message={error} onRetry={load} />}

      {!loading && !error && (
        <>
          {/* Search */}
          <div className="relative max-w-sm">
            <svg className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            <input
              type="search"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by name, ID, or department…"
              className="w-full rounded-lg border border-slate-300 py-2 pl-9 pr-3 text-sm text-slate-900 placeholder:text-slate-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {filtered.length === 0 ? (
            <EmptyState
              title={search ? 'No results found' : 'No students yet'}
              description={
                search
                  ? `No students match "${search}"`
                  : 'Get started by adding your first student.'
              }
              action={
                !search ? (
                  <Link to="/students/new">
                    <Button>Add Student</Button>
                  </Link>
                ) : undefined
              }
            />
          ) : (
            <Card padding="none">
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-slate-200 bg-slate-50">
                      <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Name</th>
                      <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Student ID</th>
                      <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Department</th>
                      <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-slate-500">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {filtered.map((s) => (
                      <tr key={s.id} className="hover:bg-slate-50 transition-colors">
                        <td className="px-4 py-3">
                          <div className="flex items-center gap-3">
                            <div className="flex h-8 w-8 flex-shrink-0 items-center justify-center rounded-full bg-blue-100 text-blue-700 text-xs font-semibold">
                              {s.fullName.charAt(0).toUpperCase()}
                            </div>
                            <Link
                              to={`/students/${s.id}`}
                              className="font-medium text-slate-900 hover:text-blue-600 transition-colors"
                            >
                              {s.fullName}
                            </Link>
                          </div>
                        </td>
                        <td className="px-4 py-3 font-mono text-xs text-slate-600">{s.studentId}</td>
                        <td className="px-4 py-3 text-slate-600">{s.department}</td>
                        <td className="px-4 py-3">
                          <div className="flex justify-end gap-2">
                            <Link
                              to={`/students/${s.id}`}
                              className="rounded-md px-2.5 py-1.5 text-xs font-medium text-slate-600 hover:bg-slate-100 transition-colors"
                            >
                              View
                            </Link>
                            <Link
                              to={`/students/${s.id}/edit`}
                              className="rounded-md px-2.5 py-1.5 text-xs font-medium text-blue-600 hover:bg-blue-50 transition-colors"
                            >
                              Edit
                            </Link>
                            <button
                              onClick={() => { setDeleteTarget(s); setDeleteError(null); }}
                              className="rounded-md px-2.5 py-1.5 text-xs font-medium text-red-600 hover:bg-red-50 transition-colors"
                            >
                              Delete
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </Card>
          )}
        </>
      )}

      <ConfirmDialog
        open={!!deleteTarget}
        danger
        title="Delete Student"
        message={`Are you sure you want to delete "${deleteTarget?.fullName}"? This will also remove all their semesters, subjects, and results.`}
        confirmLabel="Delete"
        loading={deleting}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
      {deleteError && <ErrorAlert message={deleteError} />}
    </div>
  );
}
