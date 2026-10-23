import { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { subjectsApi, studentsApi, semestersApi, resultsApi, ApiRequestError } from '../../api/client';
import type { SubjectRequest, StudentResponse, SemesterResponse } from '../../types/api';
import PageHeader from '../../components/ui/PageHeader';
import Breadcrumb from '../../components/ui/Breadcrumb';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import { FormField } from '../../components/ui/FormField';
import ErrorAlert from '../../components/ui/ErrorAlert';
import LoadingSpinner from '../../components/ui/LoadingSpinner';

// Backend requires at least 5 subjects when creating new ones
const MIN_SUBJECTS = 5;
const MAX_SUBJECTS = 20;

interface SubjectRow {
  subjectCode: string;
  subjectName: string;
  mark: string;
  errors: { subjectCode?: string; subjectName?: string; mark?: string };
}

function emptyRow(): SubjectRow {
  return { subjectCode: '', subjectName: '', mark: '', errors: {} };
}

function validateRow(row: SubjectRow): SubjectRow['errors'] {
  const errs: SubjectRow['errors'] = {};
  if (!row.subjectCode.trim()) errs.subjectCode = 'Required';
  else if (row.subjectCode.length > 30) errs.subjectCode = 'Max 30 chars';
  if (!row.subjectName.trim()) errs.subjectName = 'Required';
  else if (row.subjectName.length > 100) errs.subjectName = 'Max 100 chars';
  const m = parseFloat(row.mark);
  if (row.mark === '') errs.mark = 'Required';
  else if (isNaN(m) || m < 0 || m > 100) errs.mark = 'Must be 0–100';
  return errs;
}

export default function SubjectFormPage() {
  const { semesterId, subjectId } = useParams<{ semesterId: string; subjectId?: string }>();
  const isEdit = !!subjectId;
  const semId = Number(semesterId);
  const navigate = useNavigate();

  const [semester, setSemester] = useState<SemesterResponse | null>(null);
  const [student, setStudent] = useState<StudentResponse | null>(null);
  const [rows, setRows] = useState<SubjectRow[]>(
    isEdit ? [emptyRow()] : Array.from({ length: MIN_SUBJECTS }, emptyRow)
  );
  const [serverError, setServerError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  // 'idle' | 'saving' | 'calculating'
  const [calcStep, setCalcStep] = useState<'idle' | 'saving' | 'calculating'>('idle');

  // Step 1: ask how many subjects before showing the form
  const [step, setStep] = useState<'count' | 'form'>(isEdit ? 'form' : 'count');
  const [subjectCount, setSubjectCount] = useState<string>(String(MIN_SUBJECTS));
  const [countError, setCountError] = useState<string | null>(null);

  const handleCountConfirm = () => {
    const n = parseInt(subjectCount, 10);
    if (isNaN(n) || n < MIN_SUBJECTS) {
      setCountError(`Please enter at least ${MIN_SUBJECTS} subjects.`);
      return;
    }
    if (n > MAX_SUBJECTS) {
      setCountError(`Maximum ${MAX_SUBJECTS} subjects allowed.`);
      return;
    }
    setRows(Array.from({ length: n }, emptyRow));
    setStep('form');
  };

  useEffect(() => {
    const tasks: Promise<unknown>[] = [
      studentsApi.getAll().then(async (allStudents) => {
        for (const s of allStudents) {
          const sems = await semestersApi.getAll(s.id).catch(() => [] as SemesterResponse[]);
          const match = sems.find((sem) => sem.id === semId);
          if (match) { setSemester(match); setStudent(s); break; }
        }
      }),
    ];

    if (isEdit) {
      tasks.push(
        subjectsApi.getById(semId, Number(subjectId)).then((sub) => {
          setRows([{ subjectCode: sub.subjectCode, subjectName: sub.subjectName, mark: String(sub.mark), errors: {} }]);
        })
      );
    }

    Promise.all(tasks)
      .catch((e: Error) => setServerError(e.message))
      .finally(() => setLoading(false));
  }, [semesterId, subjectId, isEdit, semId]);

  const updateRow = (index: number, field: keyof Omit<SubjectRow, 'errors'>, value: string) => {
    setRows((prev) => prev.map((r, i) =>
      i === index
        ? { ...r, [field]: value, errors: { ...r.errors, [field]: undefined } }
        : r
    ));
  };

  const addRow = () => setRows((prev) => [...prev, emptyRow()]);
  const removeRow = (index: number) => {
    if (rows.length <= (isEdit ? 1 : MIN_SUBJECTS)) return;
    setRows((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const validated = rows.map((r) => ({ ...r, errors: validateRow(r) }));
    setRows(validated);
    if (validated.some((r) => Object.keys(r.errors).length > 0)) return;

    setSubmitting(true);
    setServerError(null);

    try {
      if (isEdit) {
        // ── Edit: just save, go back to subjects list ─────────────────────
        const req: SubjectRequest = {
          subjectCode: rows[0].subjectCode.trim(),
          subjectName: rows[0].subjectName.trim(),
          mark: parseFloat(rows[0].mark),
        };
        await subjectsApi.update(semId, Number(subjectId), req);
        navigate(`/semesters/${semesterId}/subjects`, { state: { success: 'Subject updated.' } });
      } else {
        // ── Create: step 1 – save subjects ────────────────────────────────
        setCalcStep('saving');
        const reqs: SubjectRequest[] = rows.map((r) => ({
          subjectCode: r.subjectCode.trim(),
          subjectName: r.subjectName.trim(),
          mark: parseFloat(r.mark),
        }));
        await subjectsApi.createBulk(semId, reqs);

        // ── step 2 – auto-calculate result ────────────────────────────────
        setCalcStep('calculating');
        const result = await resultsApi.calculate(semId);

        // Navigate straight to the result report
        navigate(`/results/${result.id}`, {
          state: { success: `${reqs.length} subjects saved & result calculated.` },
        });
      }
    } catch (e) {
      if (e instanceof ApiRequestError) setServerError(e.message);
      else setServerError('An unexpected error occurred.');
      setCalcStep('idle');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading…" />;

  const backPath = semester && student
    ? `/students/${student.id}/semesters/${semester.id}`
    : `/semesters/${semesterId}/subjects`;

  const semLabel = semester ? `Year ${semester.yearLevel} · Sem ${semester.semesterNumber}` : undefined;

  // ── Step 1: subject count screen (create mode only) ─────────────────────
  if (step === 'count') {
    return (
      <div className="space-y-5 max-w-md">
        <PageHeader
          title="Add Subjects"
          subtitle="Tell us how many subjects you have this semester"
          breadcrumb={
            <Breadcrumb
              crumbs={[
                { label: 'Students', to: '/students' },
                ...(student ? [{ label: student.fullName, to: `/students/${student.id}` }] : []),
                ...(semester && student ? [{ label: semLabel!, to: `/students/${student.id}/semesters/${semester.id}` }] : []),
                { label: 'Subjects', to: `/semesters/${semesterId}/subjects` },
                { label: 'Add' },
              ]}
            />
          }
        />

        <Card>
          <div className="space-y-5">
            <div className="flex items-center gap-3 p-4 rounded-lg bg-blue-50 border border-blue-100">
              <svg className="w-5 h-5 text-blue-500 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <p className="text-sm text-blue-700">
                You need <strong>at least {MIN_SUBJECTS} subjects</strong> to calculate your academic result.
              </p>
            </div>

            <div className="flex flex-col gap-1">
              <label htmlFor="field-subject-count" className="text-sm font-medium text-slate-700">
                How many subjects do you have? <span className="text-red-500">*</span>
              </label>
              <input
                id="field-subject-count"
                type="number"
                min={MIN_SUBJECTS}
                max={MAX_SUBJECTS}
                value={subjectCount}
                onChange={(e) => { setSubjectCount(e.target.value); setCountError(null); }}
                onKeyDown={(e) => e.key === 'Enter' && handleCountConfirm()}
                className={`rounded-md border px-3 py-2 text-sm text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 transition ${
                  countError ? 'border-red-400 bg-red-50' : 'border-slate-300'
                }`}
                placeholder={`Min ${MIN_SUBJECTS}, max ${MAX_SUBJECTS}`}
                autoFocus
              />
              {countError && <p className="text-xs text-red-600">{countError}</p>}
              <p className="text-xs text-slate-400">Enter a number between {MIN_SUBJECTS} and {MAX_SUBJECTS}.</p>
            </div>

            <div className="flex justify-end gap-3">
              <Link to={backPath}>
                <Button variant="secondary" type="button">Cancel</Button>
              </Link>
              <Button onClick={handleCountConfirm}>
                Continue →
              </Button>
            </div>
          </div>
        </Card>
      </div>
    );
  }

  // ── Step 2: subject input form ───────────────────────────────────────────
  return (
    <div className="space-y-5 max-w-4xl">
      <PageHeader
        title={isEdit ? 'Edit Subject' : 'Add Subjects'}
        subtitle={
          isEdit
            ? undefined
            : `${rows.length} subject${rows.length !== 1 ? 's' : ''} · minimum ${MIN_SUBJECTS} required`
        }
        breadcrumb={
          <Breadcrumb
            crumbs={[
              { label: 'Students', to: '/students' },
              ...(student ? [{ label: student.fullName, to: `/students/${student.id}` }] : []),
              ...(semester && student ? [{ label: semLabel!, to: `/students/${student.id}/semesters/${semester.id}` }] : []),
              { label: 'Subjects', to: `/semesters/${semesterId}/subjects` },
              { label: isEdit ? 'Edit' : 'Add' },
            ]}
          />
        }
      />

      {serverError && <ErrorAlert message={serverError} />}

      <Card padding="none">
        <form onSubmit={handleSubmit} noValidate>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-slate-200 bg-slate-50">
                  <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500 w-40">Code *</th>
                  <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Name *</th>
                  <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-slate-500 w-28">Mark (0–100) *</th>
                  {!isEdit && <th className="px-4 py-3 w-12" />}
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {rows.map((row, i) => (
                  <tr key={i} className="align-top">
                    <td className="px-3 py-2">
                      <input
                        value={row.subjectCode}
                        onChange={(e) => updateRow(i, 'subjectCode', e.target.value)}
                        placeholder="e.g. CS101"
                        maxLength={30}
                        disabled={submitting}
                        className={`w-full rounded-md border px-2.5 py-1.5 text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 ${row.errors.subjectCode ? 'border-red-400 bg-red-50' : 'border-slate-300'}`}
                      />
                      {row.errors.subjectCode && <p className="mt-0.5 text-xs text-red-600">{row.errors.subjectCode}</p>}
                    </td>
                    <td className="px-3 py-2">
                      <input
                        value={row.subjectName}
                        onChange={(e) => updateRow(i, 'subjectName', e.target.value)}
                        placeholder="Subject name"
                        maxLength={100}
                        disabled={submitting}
                        className={`w-full rounded-md border px-2.5 py-1.5 text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 ${row.errors.subjectName ? 'border-red-400 bg-red-50' : 'border-slate-300'}`}
                      />
                      {row.errors.subjectName && <p className="mt-0.5 text-xs text-red-600">{row.errors.subjectName}</p>}
                    </td>
                    <td className="px-3 py-2">
                      <input
                        type="number"
                        value={row.mark}
                        onChange={(e) => updateRow(i, 'mark', e.target.value)}
                        placeholder="0–100"
                        min="0"
                        max="100"
                        step="0.01"
                        disabled={submitting}
                        className={`w-full rounded-md border px-2.5 py-1.5 text-sm text-right text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 ${row.errors.mark ? 'border-red-400 bg-red-50' : 'border-slate-300'}`}
                      />
                      {row.errors.mark && <p className="mt-0.5 text-xs text-red-600 text-right">{row.errors.mark}</p>}
                    </td>
                    {!isEdit && (
                      <td className="px-2 py-2 text-center">
                        <button
                          type="button"
                          onClick={() => removeRow(i)}
                          disabled={rows.length <= MIN_SUBJECTS || submitting}
                          className="p-1.5 rounded-md text-slate-400 hover:text-red-500 hover:bg-red-50 transition-colors disabled:opacity-30"
                          aria-label="Remove row"
                        >
                          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                            <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                          </svg>
                        </button>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {!isEdit && (
            <div className="px-4 py-3 border-t border-slate-100">
              <button
                type="button"
                onClick={addRow}
                disabled={submitting}
                className="text-sm font-medium text-blue-600 hover:text-blue-700 transition-colors"
              >
                + Add another row
              </button>
            </div>
          )}

          {/* Step progress – only shown while submitting new subjects */}
          {!isEdit && calcStep !== 'idle' && (
            <div className="px-4 py-3 border-t border-blue-100 bg-blue-50 flex items-center gap-3">
              {/* Step 1 */}
              <span className={`flex items-center gap-1.5 text-xs font-medium ${
                calcStep === 'saving' ? 'text-blue-700' : 'text-green-600'
              }`}>
                {calcStep === 'saving' ? (
                  <svg className="animate-spin w-3.5 h-3.5" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                ) : (
                  <svg className="w-3.5 h-3.5" viewBox="0 0 20 20" fill="currentColor">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.857-9.809a.75.75 0 00-1.214-.882l-3.483 4.79-1.88-1.88a.75.75 0 10-1.06 1.061l2.5 2.5a.75.75 0 001.137-.089l4-5.5z" clipRule="evenodd" />
                  </svg>
                )}
                Saving subjects
              </span>

              <svg className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M9 5l7 7-7 7" />
              </svg>

              {/* Step 2 */}
              <span className={`flex items-center gap-1.5 text-xs font-medium ${
                calcStep === 'calculating' ? 'text-blue-700' : 'text-slate-400'
              }`}>
                {calcStep === 'calculating' && (
                  <svg className="animate-spin w-3.5 h-3.5" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                )}
                Calculating result
              </span>
            </div>
          )}

          <div className="flex justify-end gap-3 px-4 py-4 border-t border-slate-100">
            <Link to={backPath}>
              <Button variant="secondary" type="button" disabled={submitting}>Cancel</Button>
            </Link>
            <Button type="submit" loading={submitting}>
              {isEdit
                ? 'Save Changes'
                : calcStep === 'saving'
                  ? 'Saving subjects…'
                  : calcStep === 'calculating'
                    ? 'Calculating result…'
                    : `Submit & Calculate (${rows.length} subject${rows.length !== 1 ? 's' : ''})`
              }
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
}
