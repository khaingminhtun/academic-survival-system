import { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { studentsApi, ApiRequestError } from '../../api/client';
import type { StudentRequest } from '../../types/api';
import PageHeader from '../../components/ui/PageHeader';
import Breadcrumb from '../../components/ui/Breadcrumb';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import { FormField } from '../../components/ui/FormField';
import ErrorAlert from '../../components/ui/ErrorAlert';
import LoadingSpinner from '../../components/ui/LoadingSpinner';

type FieldErrors = Partial<Record<keyof StudentRequest, string>>;

function validate(form: StudentRequest): FieldErrors {
  const errs: FieldErrors = {};
  if (!form.studentId.trim()) errs.studentId = 'Student ID is required';
  else if (form.studentId.length > 50) errs.studentId = 'Must not exceed 50 characters';
  if (!form.fullName.trim()) errs.fullName = 'Full name is required';
  else if (form.fullName.length > 100) errs.fullName = 'Must not exceed 100 characters';
  if (!form.department.trim()) errs.department = 'Department is required';
  else if (form.department.length > 100) errs.department = 'Must not exceed 100 characters';
  return errs;
}

export default function StudentFormPage() {
  const { studentId } = useParams<{ studentId?: string }>();
  const isEdit = !!studentId;
  const navigate = useNavigate();

  const [form, setForm] = useState<StudentRequest>({ studentId: '', fullName: '', department: '' });
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
  const [serverError, setServerError] = useState<string | null>(null);
  const [loading, setLoading] = useState(isEdit);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!isEdit) return;
    studentsApi.getById(Number(studentId))
      .then((s) => setForm({ studentId: s.studentId, fullName: s.fullName, department: s.department }))
      .catch((e: Error) => setServerError(e.message))
      .finally(() => setLoading(false));
  }, [studentId, isEdit]);

  const handleChange = (field: keyof StudentRequest) => (e: React.ChangeEvent<HTMLInputElement>) => {
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
        await studentsApi.update(Number(studentId), form);
        navigate(`/students/${studentId}`, { state: { success: 'Student updated successfully.' } });
      } else {
        const created = await studentsApi.create(form);
        navigate(`/students/${created.id}`, { state: { success: 'Student created successfully.' } });
      }
    } catch (e) {
      if (e instanceof ApiRequestError) {
        const ve = e.apiError?.validationErrors ?? {};
        if (Object.keys(ve).length > 0) {
          setFieldErrors(ve as FieldErrors);
        } else {
          setServerError(e.message);
        }
      } else {
        setServerError('An unexpected error occurred.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading student…" />;

  return (
    <div className="space-y-5 max-w-xl">
      <PageHeader
        title={isEdit ? 'Edit Student' : 'Add Student'}
        breadcrumb={
          <Breadcrumb
            crumbs={[
              { label: 'Students', to: '/students' },
              ...(isEdit ? [{ label: 'Edit' }] : [{ label: 'New' }]),
            ]}
          />
        }
      />

      {serverError && <ErrorAlert message={serverError} />}

      <Card>
        <form id="student-form" onSubmit={handleSubmit} noValidate className="space-y-4">
          <FormField
            id="field-student-id"
            label="Student ID"
            required
            value={form.studentId}
            onChange={handleChange('studentId')}
            error={fieldErrors.studentId}
            placeholder="e.g. STU-001"
            maxLength={50}
            disabled={submitting}
          />
          <FormField
            id="field-full-name"
            label="Full Name"
            required
            value={form.fullName}
            onChange={handleChange('fullName')}
            error={fieldErrors.fullName}
            placeholder="e.g. John Smith"
            maxLength={100}
            disabled={submitting}
          />
          <FormField
            id="field-department"
            label="Department"
            required
            value={form.department}
            onChange={handleChange('department')}
            error={fieldErrors.department}
            placeholder="e.g. Computer Science"
            maxLength={100}
            disabled={submitting}
          />

          <div className="flex items-center justify-end gap-3 pt-2">
            <Link to={isEdit ? `/students/${studentId}` : '/students'}>
              <Button variant="secondary" type="button" disabled={submitting}>
                Cancel
              </Button>
            </Link>
            <Button type="submit" loading={submitting}>
              {isEdit ? 'Save Changes' : 'Create Student'}
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
}
