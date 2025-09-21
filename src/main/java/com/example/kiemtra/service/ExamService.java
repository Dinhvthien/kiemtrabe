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

import java.util.*;
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
                .isDelete(false)
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

        Page<Exam> examPage = examRepository.findBySearchAndIsDeletedFalse(pageable, search);

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
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        // Cập nhật trường isDeleted thành true
        exam.setIsDelete(true);
        examRepository.save(exam);
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


    // Tính xếp loại theo thang điểm 100
    private String calculateGrade(Double score) {
        if (score == null) {
            return "Chưa thi";
        }
        if (score >= 90) {
            return "Xuất sắc";
        } else if (score >= 80) {
            return "Giỏi";
        } else if (score >= 70) {
            return "Khá";
        } else if (score >= 50) {
            return "Đạt";
        } else {
            return "Không đạt";
        }
    }

    // Method chính: Lấy kết quả của tất cả học sinh trong lớp
    public List<StudentExamScoreDTO> getAllStudentScoresInClass(Long classId) {
        List<StudentExamScoreDTO> allResults = new ArrayList<>();

        // 1. Lấy thông tin lớp học
        Class clazz = classRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        // 2. Lấy tất cả StudentExamResult theo classId
        List<StudentExamResult> examResults = studentExamResultRepository.findByClassId(classId);

        // 3. Lấy tất cả sinh viên trong lớp
        List<StudentClass> studentClasses = studentClassRepository.findByClassId(classId);

        // Tạo Set để track sinh viên đã thi (để đối chiếu)
        Set<Long> studentIdsWithExam = new HashSet<>();

        // 4. Xử lý các sinh viên đã thi (có trong StudentExamResult)
        for (StudentExamResult examResult : examResults) {
            Student student = studentRepository.findById(examResult.getStudentId())
                    .orElse(null);

            Exam exam = examRepository.findById(examResult.getExamId())
                    .orElse(null);

            if (student != null && exam != null) {
                studentIdsWithExam.add(examResult.getStudentId());

                StudentExamScoreDTO scoreDTO = createStudentExamScoreFromResult(
                        student, clazz, exam, examResult);
                allResults.add(scoreDTO);
            }
        }

        // 5. Xử lý các sinh viên chưa thi (không có trong StudentExamResult)
        for (StudentClass studentClass : studentClasses) {
            // Nếu sinh viên này chưa có trong danh sách đã thi
            if (!studentIdsWithExam.contains(studentClass.getStudentId())) {
                Student student = studentRepository.findById(studentClass.getStudentId())
                        .orElse(null);

                if (student != null) {
                    StudentExamScoreDTO scoreDTO = createStudentExamScoreForNotTaken(student, clazz);
                    allResults.add(scoreDTO);
                }
            }
        }

        return allResults;
    }

    // Helper method: Tạo DTO cho sinh viên đã thi (từ StudentExamResult)
    private StudentExamScoreDTO createStudentExamScoreFromResult(Student student, Class clazz,
                                                                 Exam exam, StudentExamResult examResult) {
        Double score = examResult.getTotalScore() != null ? examResult.getTotalScore().doubleValue() : null;
        String grade = calculateGrade(score);

        // Lấy thông tin từ StudentExamResult
        Integer correctAnswers = examResult.getTotalCorrect() != null ? examResult.getTotalCorrect() : 0;
        Integer totalQuestions = examResult.getTotalQuestions() != null ? examResult.getTotalQuestions() : 0;

        return StudentExamScoreDTO.builder()
                .phoneNumber(student.getPhoneNumber())
                .fullName(student.getUserName())
                .email(student.getEmail())
                .className(clazz.getClassName())
                .examName(exam.getTitle()) // Mã đề từ exam
                .score(score != null ? Math.round(score * 10.0) / 10.0 : null)
                .grade(grade)
                .correctAnswers(correctAnswers)
                .totalQuestions(totalQuestions)
                .examStatus("Đã thi")
                .build();
    }

    // Helper method: Tạo DTO cho sinh viên chưa thi
    private StudentExamScoreDTO createStudentExamScoreForNotTaken(Student student, Class clazz) {
        return StudentExamScoreDTO.builder()
                .phoneNumber(student.getPhoneNumber())
                .fullName(student.getUserName())
                .email(student.getEmail())
                .className(clazz.getClassName())
                .examName("Chưa thi")
                .score(null)
                .grade("Chưa thi")
                .correctAnswers(0)
                .totalQuestions(0)
                .examStatus("Chưa thi")
                .build();
    }
}