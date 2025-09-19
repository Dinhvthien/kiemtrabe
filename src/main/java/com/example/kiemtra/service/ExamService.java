package com.example.kiemtra.service;

import com.example.kiemtra.dto.*;
import com.example.kiemtra.dto.request.ExamRequest;
import com.example.kiemtra.dto.response.ExamResponse;
import com.example.kiemtra.entity.*;
import com.example.kiemtra.entity.Class;
import com.example.kiemtra.exception.AppException;
import com.example.kiemtra.exception.ErrorCode;
import com.example.kiemtra.repository.*;
import com.example.kiemtra.util.PageResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamService {

    ExamRepository examRepository;
    StudentRepository studentRepository;
    QuestionRepository questionRepository;
    OptionRepository optionRepository;
    ClassRepository classRepository;
    ClassExamRepository classExamRepository;
    StudentClassRepository studentClassRepository;
    StudentAnswerRepository studentAnswerRepository;
    StudentExamResultRepository studentExamResultRepository;
    public ExamResponse createExam(ExamRequest examRequest) {
        if (examRepository.existsByExamCode(examRequest.getExamCode())) {
            throw new AppException(ErrorCode.EXAM_ALREADY_EXISTS);
        }

        Exam exam = Exam.builder()
                .examCode(examRequest.getExamCode())
                .title(examRequest.getTitle())
                .duration(examRequest.getDuration())
                .description(examRequest.getDescription())
                .build();

        Exam savedExam = examRepository.save(exam);
        return ExamResponse.builder()
                .examId(savedExam.getExamId())
                .examCode(savedExam.getExamCode())
                .title(savedExam.getTitle())
                .duration(savedExam.getDuration())
                .description(savedExam.getDescription())
                .build();
    }

    public PageResponse<ExamResponse> getExams(int page, int size, String search) {
        Sort sort = Sort.by(Sort.Direction.DESC, "examId");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Exam> examPage = examRepository.findBySearch(pageable, search);

        List<ExamResponse> contentList = examPage.getContent().stream()
                .map(exam -> ExamResponse.builder()
                        .examId(exam.getExamId())
                        .examCode(exam.getExamCode())
                        .title(exam.getTitle())
                        .duration(exam.getDuration())
                        .description(exam.getDescription())
                        .build())
                .collect(Collectors.toList());

        PageResponse<ExamResponse> response = PageResponse.<ExamResponse>builder()
                .content(contentList)
                .page(examPage.getNumber())
                .size(examPage.getSize())
                .totalElements(examPage.getTotalElements())
                .totalPages(examPage.getTotalPages())
                .first(examPage.isFirst())
                .last(examPage.isLast())
                .build();

        return response;
    }

    public ExamResponse getById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        return ExamResponse.builder()
                .examId(exam.getExamId())
                .examCode(exam.getExamCode())
                .title(exam.getTitle())
                .duration(exam.getDuration())
                .description(exam.getDescription())
                .build();
    }

    public ExamResponse update(Long id, ExamRequest request) {
        log.info(request.toString());
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        if (!exam.getExamCode().equals(request.getExamCode()) &&
                examRepository.existsByExamCode(request.getExamCode())) {
            throw new AppException(ErrorCode.EXAM_ALREADY_EXISTS);
        }

        exam.setExamCode(request.getExamCode());
        exam.setTitle(request.getTitle());
        exam.setDuration(request.getDuration());
        exam.setDescription(request.getDescription());

        Exam updatedExam = examRepository.save(exam);
        return ExamResponse.builder()
                .examId(updatedExam.getExamId())
                .examCode(updatedExam.getExamCode())
                .title(updatedExam.getTitle())
                .duration(updatedExam.getDuration())
                .description(updatedExam.getDescription())
                .build();
    }
    @Transactional
    public void delete(Long id) {
        if (!examRepository.existsById(id)) {
            throw new AppException(ErrorCode.EXAM_NOT_FOUND);
        }

        // Xóa thủ công questions và options trước
        List<Question> questions = questionRepository.findByExamId(id);  // Giả sử có method này trong QuestionRepository
        for (Question q : questions) {
            optionRepository.deleteByQuestionId(q.getQuestionId());  // Xóa options của question
        }
        questionRepository.deleteByExamId(id);  // Xóa questions

        classExamRepository.deleteByExamId(id);
        // Sau đó xóa exam
        examRepository.deleteById(id);
    }

    public ExamResponseDTO getRandomExamByClass(
            @NotBlank(message = "Số điện thoại không được để trống")
            @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải có 10 số và bắt đầu bằng 0")
            String phoneNumber,
            @NotBlank(message = "Mã lớp không được để trống")
            @Size(max = 6, message = "Mã lớp tối đa 6 ký tự")
            String classCode) {

        // Validate student
        Student student = studentRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        // Validate class
        Class classEntity = classRepository.findByClassCode(classCode)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        // Check if student is enrolled in the class
        boolean isEnrolled = studentClassRepository.findByStudentIdAndClassId(
                student.getStudentId(), classEntity.getClassId()).isPresent();
        if (!isEnrolled) {
            throw new AppException(ErrorCode.STUDENT_NOT_ENROLLED_IN_CLASS);
        }
        // check if đã thi rồi

        boolean hasCompleted = studentExamResultRepository
                .findByStudentIdAndClassId(student.getStudentId(), classEntity.getClassId())
                .isPresent(); // hoặc !isEmpty() nếu repo trả List
        if (hasCompleted) {
            throw new AppException(ErrorCode.STUDENT_ALREADY_COMPLETED_EXAM);
        }


        // Get exams for the class via ClassExam
        List<ClassExam> classExams = classExamRepository.findByClassId(classEntity.getClassId());
        if (classExams.isEmpty()) {
            throw new AppException(ErrorCode.NO_EXAMS_IN_CLASS);
        }

        // Extract exam IDs and fetch exams
        List<Long> examIds = classExams.stream()
                .map(ClassExam::getExamId)
                .collect(Collectors.toList());
        List<Exam> exams = examRepository.findAllById(examIds);
        if (exams.isEmpty()) {
            throw new AppException(ErrorCode.NO_EXAMS_IN_CLASS);
        }

        // Select random exam
        Random random = new Random();
        Exam selectedExam = exams.get(random.nextInt(exams.size()));

        // Get questions for the exam
        List<Question> questions = questionRepository.findByExamId(selectedExam.getExamId());
        if (questions.isEmpty()) {
            throw new AppException(ErrorCode.NO_QUESTIONS_IN_EXAM);
        }

        // Build question DTOs with options
        List<QuestionResponseDTO> questionDTOs = questions.stream()
                .map(question -> {
                    List<OptionResponseDTO> optionDTOs = optionRepository.findByQuestionId(question.getQuestionId())
                            .stream()
                            .map(option -> OptionResponseDTO.builder()
                                    .optionId(option.getOptionId())
                                    .optionText(option.getContent()) // Map Option.content to optionText
                                    .build())
                            .collect(Collectors.toList());

                    return QuestionResponseDTO.builder()
                            .questionId(question.getQuestionId())
                            .questionText(question.getContent()) // Map Question.content to questionText
                            .options(optionDTOs)
                            .build();
                })
                .collect(Collectors.toList());

        // Build exam response
        return ExamResponseDTO.builder()
                .examId(selectedExam.getExamId())
                .duration(selectedExam.getDuration())
                .questions(questionDTOs)
                .build();
    }

    // Tính số câu trả lời đúng
    private int calculateCorrectAnswers(List<StudentAnswer> studentAnswers) {
        int correctCount = 0;

        for (StudentAnswer answer : studentAnswers) {
            // Kiểm tra xem đáp án học sinh chọn có đúng không
            Option selectedOption = optionRepository.findById(answer.getOptionId())
                    .orElse(null);

            if (selectedOption != null && selectedOption.getIsCorrect()) {
                correctCount++;
            }
        }

        return correctCount;
    }

    // Tính xếp loại theo thang điểm 100
    private String calculateGrade(double score) {
        if (score >= 90) {
            return "Xuất sắc";
        } else if (score >= 80) {
            return "Giỏi";
        } else if (score >= 70) {
            return "Khá";
        } else if (score >= 50) {
            return "Trung bình";
        } else {
            return "Yếu";
        }
    }

    // Method chính: Lấy kết quả của tất cả học sinh trong lớp (bao gồm cả chưa thi)
    public List<StudentExamScoreDTO> getAllStudentScoresInClass(Long classId) {
        List<StudentExamScoreDTO> allResults = new ArrayList<>();

        // 1. Lấy thông tin lớp học
        Class clazz = classRepository.findById(classId)
                .orElseThrow(() ->  new AppException(ErrorCode.CLASS_NOT_FOUND));

        // 2. Lấy tất cả học sinh trong lớp
        List<StudentClass> studentClasses = studentClassRepository.findByClassId(classId);

        // 3. Lấy tất cả kỳ thi của lớp này
        List<ClassExam> classExams = classExamRepository.findByClassId(classId);

        // 4. Duyệt qua từng học sinh
        for (StudentClass studentClass : studentClasses) {
            Student student = studentRepository.findById(studentClass.getStudentId())
                    .orElse(null);

            if (student != null) {
                // 5. Duyệt qua từng kỳ thi của lớp
                for (ClassExam classExam : classExams) {
                    Exam exam = examRepository.findById(classExam.getExamId())
                            .orElse(null);

                    if (exam != null) {
                        // Tạo kết quả cho học sinh này với kỳ thi này
                        StudentExamScoreDTO scoreDTO = createStudentExamScore(
                                student, clazz, exam, classId);
                        allResults.add(scoreDTO);
                    }
                }

                // Nếu lớp chưa có kỳ thi nào, vẫn hiển thị thông tin học sinh
                if (classExams.isEmpty()) {
                    StudentExamScoreDTO scoreDTO = StudentExamScoreDTO.builder()
                            .phoneNumber(student.getPhoneNumber())
                            .fullName(student.getUserName())
                            .email(student.getEmail())
                            .className(clazz.getClassName())
                            .examName("Chưa có kỳ thi")
                            .score(null)
                            .grade("Chưa thi")
                            .correctAnswers(0)
                            .totalQuestions(0)
                            .build();
                    allResults.add(scoreDTO);
                }
            }
        }

        return allResults;
    }

    // Helper method để tạo thông tin điểm cho 1 học sinh với 1 kỳ thi
    private StudentExamScoreDTO createStudentExamScore(Student student, Class clazz,Exam exam, Long classId) {
        // Lấy tổng số câu hỏi trong đề thi
        List<Question> examQuestions = questionRepository.findByExamId(exam.getExamId());
        int totalQuestions = examQuestions.size();

        // Lấy câu trả lời của học sinh cho kỳ thi này
        List<StudentAnswer> studentAnswers = studentAnswerRepository
                .findByStudentIdAndExamIdAndClassId(
                        student.getStudentId(), exam.getExamId(), classId);

        // Kiểm tra xem học sinh đã thi chưa
        if (studentAnswers.isEmpty()) {
            // Học sinh chưa thi
            return StudentExamScoreDTO.builder()
                    .phoneNumber(student.getPhoneNumber())
                    .fullName(student.getUserName())
                    .email(student.getEmail())
                    .className(clazz.getClassName())
                    .examName(exam.getTitle())
                    .score(null)
                    .grade("Chưa thi")
                    .correctAnswers(0)
                    .totalQuestions(totalQuestions)
                    .build();
        } else {
            // Học sinh đã thi - tính điểm
            int correctAnswers = calculateCorrectAnswers(studentAnswers);
            double score = totalQuestions > 0 ?
                    (double) correctAnswers / totalQuestions * 100 : 0;
            String grade = calculateGrade(score);

            return StudentExamScoreDTO.builder()
                    .phoneNumber(student.getPhoneNumber())
                    .fullName(student.getUserName())
                    .email(student.getEmail())
                    .className(clazz.getClassName())
                    .examName(exam.getTitle())
                    .score(Math.round(score * 10.0) / 10.0)
                    .grade(grade)
                    .correctAnswers(correctAnswers)
                    .totalQuestions(totalQuestions)
                    .build();
        }
    }

    // Method để lấy kết quả tổng hợp (chỉ những ai đã thi)
    public List<StudentExamScoreDTO> getCompletedExamScoresInClass(Long classId) {
        return getAllStudentScoresInClass(classId)
                .stream()
                .filter(score -> score.getScore() != null) // Chỉ lấy những ai đã thi
                .collect(Collectors.toList());
    }

    // Method để lấy danh sách học sinh chưa thi
    public List<StudentExamScoreDTO> getNotCompletedExamScoresInClass(Long classId) {
        return getAllStudentScoresInClass(classId)
                .stream()
                .filter(score -> score.getScore() == null) // Chỉ lấy những ai chưa thi
                .collect(Collectors.toList());
    }

    // Method để lấy thống kê lớp học
    public ClassStatisticsDTO getClassStatistics(Long classId) {
        List<StudentExamScoreDTO> allScores = getAllStudentScoresInClass(classId);

        long totalStudents = allScores.stream()
                .map(score -> score.getPhoneNumber())
                .distinct()
                .count();

        long completedExams = allScores.stream()
                .filter(score -> score.getScore() != null)
                .count();

        long notCompletedExams = allScores.stream()
                .filter(score -> score.getScore() == null)
                .count();

        // Tính điểm trung bình lớp
        double averageScore = allScores.stream()
                .filter(score -> score.getScore() != null)
                .mapToDouble(StudentExamScoreDTO::getScore)
                .average()
                .orElse(0.0);

        // Thống kê theo xếp loại
        Map<String, Long> gradeDistribution = allScores.stream()
                .filter(score -> score.getScore() != null)
                .collect(Collectors.groupingBy(
                        StudentExamScoreDTO::getGrade,
                        Collectors.counting()
                ));

        Class clazz = classRepository.findById(classId).orElse(null);
        String className = clazz != null ? clazz.getClassName() : "Unknown";

        return ClassStatisticsDTO.builder()
                .classId(classId)
                .className(className)
                .totalStudents((int) totalStudents)
                .completedExams((int) completedExams)
                .notCompletedExams((int) notCompletedExams)
                .averageScore(Math.round(averageScore * 10.0) / 10.0)
                .gradeDistribution(gradeDistribution)
                .build();
    }
}