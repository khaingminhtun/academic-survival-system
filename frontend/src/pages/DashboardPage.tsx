import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { studentsApi } from '../api/client';
import type { StudentResponse } from '../types/api';
import Card from '../components/ui/Card';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import ErrorAlert from '../components/ui/ErrorAlert';

interface StatCardProps {
  label: string;
  value: number | string;
  icon: React.ReactNode;
  color: string;
  to?: string;
}

function StatCard({ label, value, icon, color, to }: StatCardProps) {
  const inner = (
    <Card className="flex items-center gap-4 hover:border-blue-200 hover:shadow-md transition-all">
      <div className={`flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-xl ${color}`}>
        {icon}
      </div>
      <div>
        <p className="text-xs font-medium uppercase tracking-wider text-slate-500">{label}</p>
        <p className="mt-0.5 text-2xl font-bold text-slate-900">{value}</p>
      </div>
    </Card>
  );
  return to ? <Link to={to} className="block">{inner}</Link> : inner;
}

export default function DashboardPage() {
  const [students, setStudents] = useState<StudentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = () => {
    setLoading(true);
    setError(null);
    studentsApi.getAll()
      .then(setStudents)
      .catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-xl font-bold text-slate-900">Dashboard</h1>
        <p className="mt-0.5 text-sm text-slate-500">Academic Survival System overview</p>
      </div>

      {loading && <LoadingSpinner message="Loading dashboard…" />}
      {error && <ErrorAlert message={error} onRetry={load} />}

      {!loading && !error && (
        <>
          {/* Stats */}
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            <StatCard
              label="Total Students"
              value={students.length}
              to="/students"
              color="bg-blue-100"
              icon={
                <svg className="w-6 h-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.75}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
              }
            />
            <StatCard
              label="Departments"
              value={new Set(students.map(s => s.department)).size}
              color="bg-violet-100"
              icon={
                <svg className="w-6 h-6 text-violet-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.75}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                </svg>
              }
            />
            <StatCard
              label="Active Records"
              value={students.length > 0 ? 'Live' : 'None'}
              color="bg-green-100"
              icon={
                <svg className="w-6 h-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.75}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              }
            />
          </div>

          {/* Recent Students */}
          <Card padding="none">
            <div className="flex items-center justify-between px-5 py-4 border-b border-slate-100">
              <h2 className="text-sm font-semibold text-slate-900">Recent Students</h2>
              <Link to="/students" className="text-xs font-medium text-blue-600 hover:text-blue-700">View all →</Link>
            </div>
            {students.length === 0 ? (
              <div className="px-5 py-10 text-center">
                <p className="text-sm text-slate-500">No students yet.</p>
                <Link to="/students/new" className="mt-2 inline-block text-sm font-medium text-blue-600 hover:text-blue-700">
                  Add your first student →
                </Link>
              </div>
            ) : (
              <ul className="divide-y divide-slate-100">
                {students.slice(0, 5).map((s) => (
                  <li key={s.id}>
                    <Link
                      to={`/students/${s.id}`}
                      className="flex items-center gap-4 px-5 py-3.5 hover:bg-slate-50 transition-colors"
                    >
                      <div className="flex h-9 w-9 flex-shrink-0 items-center justify-center rounded-full bg-blue-100 text-blue-700 text-sm font-semibold">
                        {s.fullName.charAt(0).toUpperCase()}
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-slate-900 truncate">{s.fullName}</p>
                        <p className="text-xs text-slate-500 truncate">{s.studentId} · {s.department}</p>
                      </div>
                      <svg className="w-4 h-4 text-slate-300 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                        <path strokeLinecap="round" strokeLinejoin="round" d="M9 5l7 7-7 7" />
                      </svg>
                    </Link>
                  </li>
                ))}
              </ul>
            )}
          </Card>

          {/* Quick Actions */}
          <Card>
            <h2 className="text-sm font-semibold text-slate-900 mb-3">Quick Actions</h2>
            <div className="flex flex-wrap gap-2">
              <Link
                to="/students/new"
                className="inline-flex items-center gap-1.5 rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 transition-colors"
              >
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" />
                </svg>
                Add Student
              </Link>
              <Link
                to="/students"
                className="inline-flex items-center gap-1.5 rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50 transition-colors"
              >
                Browse Students
              </Link>
            </div>
          </Card>
        </>
      )}
    </div>
  );
}
