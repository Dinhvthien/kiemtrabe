package com.example.kiemtra.service;

import com.example.kiemtra.dto.request.ExamSubmissionRequest;
import com.example.kiemtra.dto.response.ExamSubmissionResponse;
import com.example.kiemtra.entity.*;
import com.example.kiemtra.entity.Class;
import com.example.kiemtra.exception.AppException;
import com.example.kiemtra.exception.ErrorCode;
import com.example.kiemtra.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamSubmissionService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final StudentClassRepository studentClassRepository;
    private final OptionRepository optionRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final StudentExamResultRepository studentExamResultRepository;

    @Transactional
    public ExamSubmissionResponse submitExam(ExamSubmissionRequest request) {
        log.info("Processing exam submission for student: {}, exam: {}",
                request.getStudentName(), request.getExamId());

        // 1. Tìm học sinh (không tự động tạo)
        Student student = studentRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        // 2. Tìm lớp học
        Class classEntity = classRepository.findByClassCode(request.getClassCode())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        // 3. Tìm bài thi
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        // 5. Lấy tổng số câu trong đề thi
        int totalQuestionsInExam =(int)questionRepository.countByExamId(exam.getExamId());

        // 6. Lưu câu trả lời của học sinh và đếm số câu đúng
        int totalCorrect = 0;
        int answeredQuestions = request.getAnswers().size();

        for (ExamSubmissionRequest.AnswerSubmission answer : request.getAnswers()) {
            // Lưu câu trả lời
            StudentAnswer studentAnswer = StudentAnswer.builder()
                    .studentId(student.getStudentId())
                    .classId(classEntity.getClassId())
                    .examId(exam.getExamId())
                    .questionId(answer.getQuestionId())
                    .optionId(answer.getSelectedOptionId())
                    .build();
            studentAnswerRepository.save(studentAnswer);

            // Kiểm tra đáp án đúng
            Option selectedOption = optionRepository.findById(answer.getSelectedOptionId())
                    .orElseThrow(() -> new AppException(ErrorCode.OPTION_NOT_FOUND));

            if (selectedOption.getIsCorrect()) {
                totalCorrect++;
            }
        }

        // 7. Tính điểm theo phần trăm dựa trên tổng số câu trong đề và làm tròn lên
        double percentage = (double) totalCorrect / totalQuestionsInExam * 100;
        int totalScore = (int) Math.ceil(percentage);

        // 8. Lưu kết quả thi
        StudentExamResult result = StudentExamResult.builder()
                .studentId(student.getStudentId())
                .classId(classEntity.getClassId())
                .examId(exam.getExamId())
                .totalScore(totalScore)
                .totalCorrect(totalCorrect)
                .totalQuestions(totalQuestionsInExam)
                .submissionTime(request.getSubmissionTime() != null ? request.getSubmissionTime() : LocalDateTime.now())
                .build();

        StudentExamResult savedResult = studentExamResultRepository.save(result);

        // 9. Cập nhật trạng thái hoàn thành thi cho học sinh trong lớp
        updateStudentClassCompletion(student.getStudentId(), classEntity.getClassId());


        return ExamSubmissionResponse.builder()
                .resultId(savedResult.getResultId())
                .studentName(request.getStudentName())
                .classCode(request.getClassCode())
                .examId(exam.getExamId())
                .totalScore(totalScore)
                .totalCorrect(totalCorrect)
                .totalQuestions(totalQuestionsInExam)
                .message("Bài thi đã được nộp thành công!")
                .build();
    }

    private void updateStudentClassCompletion(Long studentId, Long classId) {
        StudentClass studentClass =  studentClassRepository.findByStudentIdAndClassId(studentId,classId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED_ERROR));
        studentClass.setIsCompletedExam(true);
        studentClassRepository.save(studentClass);
    }
}
