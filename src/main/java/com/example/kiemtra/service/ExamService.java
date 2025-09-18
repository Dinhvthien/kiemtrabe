package com.example.kiemtra.service;

import com.example.kiemtra.dto.ApiResponse;
import com.example.kiemtra.dto.ExamResponseDTO;
import com.example.kiemtra.dto.OptionResponseDTO;
import com.example.kiemtra.dto.QuestionResponseDTO;
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

import java.util.List;
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
        // neu ton tai studentClass thi thong bao loi
        StudentClass studentClass =  studentClassRepository.findByStudentIdAndClassId(student.getStudentId(), classEntity.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED_ERROR));
        if (Boolean.TRUE.equals(studentClass.getIsCompletedExam())) {
            throw new AppException(ErrorCode.EXAM_ALREADY_COMPLETED);
        }

        // Check if student is enrolled in the class
        boolean isEnrolled = studentClassRepository.findByStudentIdAndClassId(
                student.getStudentId(), classEntity.getClassId()).isPresent();
        if (!isEnrolled) {
            throw new AppException(ErrorCode.STUDENT_NOT_ENROLLED_IN_CLASS);
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
}