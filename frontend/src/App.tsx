import { Routes, Route, Navigate } from 'react-router-dom';
import AppLayout from './components/layout/AppLayout';
import DashboardPage from './pages/DashboardPage';
import StudentsPage from './pages/students/StudentsPage';
import StudentDetailPage from './pages/students/StudentDetailPage';
import StudentFormPage from './pages/students/StudentFormPage';
import SemestersPage from './pages/semesters/SemestersPage';
import SemesterDetailPage from './pages/semesters/SemesterDetailPage';
import SemesterFormPage from './pages/semesters/SemesterFormPage';
import SubjectsPage from './pages/subjects/SubjectsPage';
import SubjectFormPage from './pages/subjects/SubjectFormPage';
import ResultsPage from './pages/results/ResultsPage';
import ResultDetailPage from './pages/results/ResultDetailPage';

export default function App() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<DashboardPage />} />

        {/* Students */}
        <Route path="/students" element={<StudentsPage />} />
        <Route path="/students/new" element={<StudentFormPage />} />
        <Route path="/students/:studentId" element={<StudentDetailPage />} />
        <Route path="/students/:studentId/edit" element={<StudentFormPage />} />

        {/* Semesters (nested under student) */}
        <Route path="/students/:studentId/semesters" element={<SemestersPage />} />
        <Route path="/students/:studentId/semesters/new" element={<SemesterFormPage />} />
        <Route path="/students/:studentId/semesters/:semesterId" element={<SemesterDetailPage />} />
        <Route path="/students/:studentId/semesters/:semesterId/edit" element={<SemesterFormPage />} />

        {/* Subjects (nested under semester) */}
        <Route path="/semesters/:semesterId/subjects" element={<SubjectsPage />} />
        <Route path="/semesters/:semesterId/subjects/new" element={<SubjectFormPage />} />
        <Route path="/semesters/:semesterId/subjects/:subjectId/edit" element={<SubjectFormPage />} />

        {/* Academic Results */}
        <Route path="/students/:studentId/results" element={<ResultsPage />} />
        <Route path="/semesters/:semesterId/results" element={<ResultsPage />} />
        <Route path="/results/:resultId" element={<ResultDetailPage />} />

        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Route>
    </Routes>
  );
}
