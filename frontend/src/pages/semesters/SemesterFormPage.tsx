import { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { semestersApi, studentsApi, ApiRequestError } from '../../api/client';
import type { SemesterRequest, StudentResponse } from '../../types/api';
import PageHeader from '../../components/ui/PageHeader';
import Breadcrumb from '../../components/ui/Breadcrumb';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import { FormField } from '../../components/ui/FormField';
import ErrorAlert from '../../components/ui/ErrorAlert';
import LoadingSpinner from '../../components/ui/LoadingSpinner';

type FieldErrors = Partial<Record<keyof SemesterRequest, string>>;

function validate(form: SemesterRequest): FieldErrors {
  const errs: FieldErrors = {};
  if (!form.semesterName.trim()) errs.semesterName = 'Semester name is required';
  else if (form.semesterName.length > 50) errs.semesterName = 'Must not exceed 50 characters';
  if (!form.academicYear.trim()) errs.academicYear = 'Academic year is required';
  else if (form.academicYear.length > 20) errs.academicYear = 'Must not exceed 20 characters';
  return errs;
}

export default function SemesterFormPage() {
  const { studentId, semesterId } = useParams<{ studentId: string; semesterId?: string }>();
  const isEdit = !!semesterId;
  const navigate = useNavigate();
  const sid = Number(studentId);

  const [student, setStudent] = useState<StudentResponse | null>(null);
  const [form, setForm] = useState<SemesterRequest>({ semesterName: '', academicYear: '' });
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
  const [serverError, setServerError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const tasks: Promise<unknown>[] = [studentsApi.getById(sid).then(setStudent)];
    if (isEdit) {
      tasks.push(
        semestersApi.getById(sid, Number(semesterId)).then((s) =>
          setForm({ semesterName: s.semesterName, academicYear: s.academicYear })
        )
      );
    }
    Promise.all(tasks)
      .catch((e: Error) => setServerError(e.message))
      .finally(() => setLoading(false));
  }, [studentId, semesterId, isEdit, sid]);

  const handleChange = (field: keyof SemesterRequest) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
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
          <FormField
            id="field-semester-name"
            label="Semester Name"
            required
            value={form.semesterName}
            onChange={handleChange('semesterName')}
            error={fieldErrors.semesterName}
            placeholder="e.g. Semester 1"
            maxLength={50}
            disabled={submitting}
          />
          <FormField
            id="field-academic-year"
            label="Academic Year"
            required
            value={form.academicYear}
            onChange={handleChange('academicYear')}
            error={fieldErrors.academicYear}
            placeholder="e.g. 2024-2025"
            maxLength={20}
            disabled={submitting}
          />
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
