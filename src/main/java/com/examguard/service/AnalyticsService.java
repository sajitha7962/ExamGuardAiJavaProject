package com.examguard.service;

import com.examguard.dto.AnalyticsDTOs.*;
import com.examguard.entity.Department;
import com.examguard.entity.Exam;
import com.examguard.entity.ExamResult;
import com.examguard.entity.Student;
import com.examguard.entity.enums.Enums.ApprovalStatus;
import com.examguard.entity.enums.Enums.ExamStatus;
import com.examguard.entity.enums.Enums.PassFailStatus;
import com.examguard.entity.enums.Enums.Role;
import com.examguard.exception.ResourceNotFoundException;
import com.examguard.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class AnalyticsService {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final ExamRepository examRepository;
    private final ExamAttemptRepository attemptRepository;
    private final ExamResultRepository resultRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final ProctoringEventRepository proctoringEventRepository;

    public AnalyticsService(StudentRepository studentRepository, ProfessorRepository professorRepository,
                            ExamRepository examRepository, ExamAttemptRepository attemptRepository,
                            ExamResultRepository resultRepository, DepartmentRepository departmentRepository,
                            SubjectRepository subjectRepository, UserRepository userRepository,
                            ProctoringEventRepository proctoringEventRepository) {
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.examRepository = examRepository;
        this.attemptRepository = attemptRepository;
        this.resultRepository = resultRepository;
        this.departmentRepository = departmentRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.proctoringEventRepository = proctoringEventRepository;
    }

    @Transactional(readOnly = true)
    public StudentAnalyticsDTO getStudentAnalytics(Long studentUserId) {
        Student student = studentRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        List<ExamResult> results = resultRepository.findByStudentId(student.getId());

        StudentAnalyticsDTO dto = new StudentAnalyticsDTO();
        dto.setTotalExams(results.size());
        dto.setCompletedExams(results.size());
        dto.setPendingResults(0);

        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal highest = BigDecimal.ZERO;
        List<String> labels = new ArrayList<>();
        List<BigDecimal> scores = new ArrayList<>();
        Map<String, BigDecimal> subMap = new HashMap<>();

        for (ExamResult r : results) {
            sum = sum.add(r.getTotalMarksObtained());
            if (r.getTotalMarksObtained().compareTo(highest) > 0) {
                highest = r.getTotalMarksObtained();
            }
            labels.add(r.getExam().getTitle());
            scores.add(r.getPercentage());
            subMap.put(r.getExam().getSubject().getCode(), r.getPercentage());
        }

        BigDecimal avg = results.isEmpty() ? BigDecimal.ZERO : sum.divide(BigDecimal.valueOf(results.size()), 2, RoundingMode.HALF_UP);
        dto.setAverageScore(avg);
        dto.setHighestScore(highest);
        dto.setExamLabels(labels);
        dto.setExamScores(scores);
        dto.setSubjectWiseAverage(subMap);

        return dto;
    }

    @Transactional(readOnly = true)
    public ProfessorAnalyticsDTO getProfessorAnalytics(Long professorUserId) {
        ProfessorAnalyticsDTO dto = new ProfessorAnalyticsDTO();

        long profId = professorRepository.findByUserId(professorUserId).map(p -> p.getId()).orElse(0L);
        List<Exam> exams = examRepository.findByProfessorId(profId);

        dto.setTotalExams(exams.size());
        long active = exams.stream().filter(e -> e.getStatus() == ExamStatus.ONGOING || e.getStatus() == ExamStatus.PUBLISHED).count();
        dto.setActiveExams(active);
        dto.setCompletedExams(exams.stream().filter(e -> e.getStatus() == ExamStatus.COMPLETED).count());
        dto.setTotalStudents(studentRepository.count());

        List<ExamResult> allResults = new ArrayList<>();
        for (Exam e : exams) {
            allResults.addAll(resultRepository.findByExamId(e.getId()));
        }

        BigDecimal sumMarks = BigDecimal.ZERO;
        long passCount = 0;
        int gradeO = 0, gradeA = 0, gradeB = 0, gradeC = 0, gradeF = 0;

        for (ExamResult r : allResults) {
            sumMarks = sumMarks.add(r.getTotalMarksObtained());
            if (r.getPassFailStatus() == PassFailStatus.PASS) passCount++;

            String g = r.getGrade();
            if ("O".equals(g)) gradeO++;
            else if ("A+".equals(g) || "A".equals(g)) gradeA++;
            else if ("B+".equals(g) || "B".equals(g)) gradeB++;
            else if ("C".equals(g)) gradeC++;
            else gradeF++;
        }

        BigDecimal avgMarks = allResults.isEmpty() ? BigDecimal.ZERO : sumMarks.divide(BigDecimal.valueOf(allResults.size()), 2, RoundingMode.HALF_UP);
        BigDecimal passPct = allResults.isEmpty() ? BigDecimal.ZERO : BigDecimal.valueOf(passCount)
                .divide(BigDecimal.valueOf(allResults.size()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP);

        dto.setClassAverageMarks(avgMarks);
        dto.setPassPercentage(passPct);
        dto.setGradeLabels(List.of("Grade O", "Grade A", "Grade B", "Grade C", "Grade F"));
        dto.setGradeCounts(List.of(gradeO, gradeA, gradeB, gradeC, gradeF));

        List<String> examTitles = new ArrayList<>();
        List<BigDecimal> examAverages = new ArrayList<>();
        for (Exam e : exams) {
            examTitles.add(e.getTitle());
            List<ExamResult> ers = resultRepository.findByExamId(e.getId());
            if (ers.isEmpty()) {
                examAverages.add(BigDecimal.ZERO);
            } else {
                BigDecimal examSum = ers.stream().map(ExamResult::getTotalMarksObtained).reduce(BigDecimal.ZERO, BigDecimal::add);
                examAverages.add(examSum.divide(BigDecimal.valueOf(ers.size()), 2, RoundingMode.HALF_UP));
            }
        }
        dto.setExamTitles(examTitles);
        dto.setExamAverages(examAverages);

        return dto;
    }

    @Transactional(readOnly = true)
    public AdminAnalyticsDTO getAdminAnalytics() {
        AdminAnalyticsDTO dto = new AdminAnalyticsDTO();
        dto.setTotalStudents(studentRepository.count());
        dto.setTotalProfessors(professorRepository.count());
        dto.setPendingProfessors(professorRepository.countByApprovalStatus(ApprovalStatus.PENDING));
        dto.setTotalDepartments(departmentRepository.count());
        dto.setTotalSubjects(subjectRepository.count());
        dto.setTotalExams(examRepository.count());
        dto.setActiveExams(examRepository.countByStatus(ExamStatus.ONGOING) + examRepository.countByStatus(ExamStatus.PUBLISHED));
        dto.setTotalAttempts(attemptRepository.count());

        Map<String, Long> deptStudents = new HashMap<>();
        Map<String, Long> deptExams = new HashMap<>();

        for (Department d : departmentRepository.findAll()) {
            deptStudents.put(d.getCode(), studentRepository.countByDepartmentId(d.getId()));
            deptExams.put(d.getCode(), (long) examRepository.findByDepartmentId(d.getId()).size());
        }
        dto.setDepartmentStudentCounts(deptStudents);
        dto.setDepartmentExamCounts(deptExams);

        return dto;
    }
}

