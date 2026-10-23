import { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { semestersApi, studentsApi, ApiRequestError } from '../../api/client';
import type { SemesterRequest, StudentResponse } from '../../types/api';
import PageHeader from '../../components/ui/PageHeader';
import Breadcrumb from '../../components/ui/Breadcrumb';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import ErrorAlert from '../../components/ui/ErrorAlert';
import LoadingSpinner from '../../components/ui/LoadingSpinner';

type FieldErrors = Partial<Record<keyof SemesterRequest, string>>;

function validate(form: SemesterRequest): FieldErrors {
  const errs: FieldErrors = {};
  if (!form.yearLevel || form.yearLevel < 1 || form.yearLevel > 6)
    errs.yearLevel = 'Year level must be between 1 and 6';
  if (!form.semesterNumber || (form.semesterNumber !== 1 && form.semesterNumber !== 2))
    errs.semesterNumber = 'Semester number must be 1 or 2';
  return errs;
}

export default function SemesterFormPage() {
  const { studentId, semesterId } = useParams<{ studentId: string; semesterId?: string }>();
  const isEdit = !!semesterId;
  const navigate = useNavigate();
  const sid = Number(studentId);

  const [student, setStudent] = useState<StudentResponse | null>(null);
  const [form, setForm] = useState<SemesterRequest>({ yearLevel: 1, semesterNumber: 1 });
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
  const [serverError, setServerError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const tasks: Promise<unknown>[] = [studentsApi.getById(sid).then(setStudent)];
    if (isEdit) {
      tasks.push(
        semestersApi.getById(sid, Number(semesterId)).then((s) =>
          setForm({ yearLevel: s.yearLevel, semesterNumber: s.semesterNumber })
        )
      );
    }
    Promise.all(tasks)
      .catch((e: Error) => setServerError(e.message))
      .finally(() => setLoading(false));
  }, [studentId, semesterId, isEdit, sid]);

  const handleSelectChange =
    (field: keyof SemesterRequest) => (e: React.ChangeEvent<HTMLSelectElement>) => {
      setForm((prev) => ({ ...prev, [field]: Number(e.target.value) }));
      if (fieldErrors[field]) setFieldErrors((prev) => ({ ...prev, [field]: undefined }));
    };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const errs = validate(form);
    if (Object.keys(errs).length > 0) { setFieldErrors(errs); return; }
    setSubmitting(true);
    setServerError(null);
    try {
      if (isEdit) {
        await semestersApi.update(sid, Number(semesterId), form);
        navigate(`/students/${studentId}/semesters/${semesterId}`, { state: { success: 'Semester updated.' } });
      } else {
        const created = await semestersApi.create(sid, form);
        navigate(`/students/${studentId}/semesters/${created.id}`, { state: { success: 'Semester created.' } });
      }
    } catch (e) {
      if (e instanceof ApiRequestError) {
        const ve = e.apiError?.validationErrors ?? {};
        if (Object.keys(ve).length > 0) setFieldErrors(ve as FieldErrors);
        else setServerError(e.message);
      } else {
        setServerError('An unexpected error occurred.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading…" />;

  return (
    <div className="space-y-5 max-w-xl">
      <PageHeader
        title={isEdit ? 'Edit Semester' : 'Add Semester'}
        subtitle={student ? `For ${student.fullName}` : undefined}
        breadcrumb={
          <Breadcrumb
            crumbs={[
              { label: 'Students', to: '/students' },
              { label: student?.fullName ?? '…', to: `/students/${studentId}` },
              { label: 'Semesters', to: `/students/${studentId}/semesters` },
              { label: isEdit ? 'Edit' : 'New' },
            ]}
          />
        }
      />

      {serverError && <ErrorAlert message={serverError} />}

      <Card>
        <form onSubmit={handleSubmit} noValidate className="space-y-4">

          {/* Year Level */}
          <div className="flex flex-col gap-1">
            <label htmlFor="field-year-level" className="text-sm font-medium text-slate-700">
              Year Level <span className="text-red-500">*</span>
            </label>
            <select
              id="field-year-level"
              value={form.yearLevel}
              onChange={handleSelectChange('yearLevel')}
              disabled={submitting}
              className={`rounded-md border px-3 py-2 text-sm bg-white text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 transition ${fieldErrors.yearLevel ? 'border-red-400' : 'border-slate-300'
                }`}
            >
              {[1, 2, 3, 4, 5, 6].map((y) => (
                <option key={y} value={y}>Year {y}</option>
              ))}
            </select>
            {fieldErrors.yearLevel && (
              <p className="text-xs text-red-600">{fieldErrors.yearLevel}</p>
            )}
          </div>

          {/* Semester Number */}
          <div className="flex flex-col gap-1">
            <label htmlFor="field-semester-number" className="text-sm font-medium text-slate-700">
              Semester Number <span className="text-red-500">*</span>
            </label>
            <select
              id="field-semester-number"
              value={form.semesterNumber}
              onChange={handleSelectChange('semesterNumber')}
              disabled={submitting}
              className={`rounded-md border px-3 py-2 text-sm bg-white text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 transition ${fieldErrors.semesterNumber ? 'border-red-400' : 'border-slate-300'
                }`}
            >
              <option value={1}>Semester 1</option>
              <option value={2}>Semester 2</option>
            </select>
            {fieldErrors.semesterNumber && (
              <p className="text-xs text-red-600">{fieldErrors.semesterNumber}</p>
            )}
          </div>

          <div className="flex justify-end gap-3 pt-2">
            <Link to={isEdit ? `/students/${studentId}/semesters/${semesterId}` : `/students/${studentId}/semesters`}>
              <Button variant="secondary" type="button" disabled={submitting}>Cancel</Button>
            </Link>
            <Button type="submit" loading={submitting}>
              {isEdit ? 'Save Changes' : 'Create Semester'}
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
}

