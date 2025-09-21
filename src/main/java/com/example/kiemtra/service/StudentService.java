package com.example.kiemtra.service;

import com.example.kiemtra.dto.*;
import com.example.kiemtra.dto.request.UpdateStudentRequest;
import com.example.kiemtra.entity.*;
import com.example.kiemtra.entity.Class;
import com.example.kiemtra.exception.AppException;
import com.example.kiemtra.exception.ErrorCode;
import com.example.kiemtra.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentService {
    StudentRepository studentRepository;
    ClassRepository classRepository;
    StudentClassRepository studentClassRepository;
    ExamRepository examRepository;
    StudentAnswerRepository studentAnswerRepository;
    QuestionRepository questionRepository;
    OptionRepository optionRepository;
    @Transactional
    public ApiResponse<StudentDTO> createStudent(CreateStudentRequest request) {
        // Kiểm tra số điện thoại đã tồn tại chưa
        if (studentRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.DUPLICATE_PHONE_IN_REQUEST);
        }

        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.DUPLICATE_EMAIL_IN_REQUEST);
        }

        // Tạo học sinh mới
        Student student = Student.builder()
                .userName(request.getUserName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .status(true)
                .build();

        Student savedStudent = studentRepository.save(student);

        // Đăng ký học sinh vào các lớp học (nếu có)
        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            List<Long> validClassIds = request.getClassIds().stream()
                    .filter(classId -> classRepository.existsById(classId))
                    .collect(Collectors.toList());

            if (!validClassIds.isEmpty()) {
                List<StudentClass> studentClasses = validClassIds.stream()
                        .map(classId -> StudentClass.builder()
                                .studentId(savedStudent.getStudentId())
                                .classId(classId)
                                .isCompletedExam(false)
                                .build())
                        .collect(Collectors.toList());

                studentClassRepository.saveAll(studentClasses);
            }
        }

        // Tạo response
        StudentDTO studentDTO = StudentDTO.builder()
                .studentId(savedStudent.getStudentId())
                .userName(savedStudent.getUserName())
                .phoneNumber(savedStudent.getPhoneNumber())
                .email(savedStudent.getEmail())
                .status(savedStudent.getStatus())
                .build();

        return ApiResponse.<StudentDTO>builder()
                .message("Tạo học sinh và đăng ký lớp học thành công")
                .result(studentDTO)
                .build();
    }
    public ApiResponse<Page<StudentDTO>> getAllStudents(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> studentPage;

        if (search != null && !search.isEmpty()) {
            studentPage = studentRepository
                    .findBySearchAndStatusTrue(pageable, search);
        } else {
            studentPage = studentRepository.findAll(pageable);
        }

        Page<StudentDTO> dtoPage = studentPage.map(student -> StudentDTO.builder()
                .studentId(student.getStudentId())
                .userName(student.getUserName())
                .phoneNumber(student.getPhoneNumber())
                .email(student.getEmail())
                .status(student.getStatus())
                .build());

        return ApiResponse.<Page<StudentDTO>>builder()
                .message("Lấy danh sách học sinh thành công")
                .result(dtoPage)
                .build();
    }
    @Transactional
    public ApiResponse<StudentDTO> updateStudentClasses(Long studentId, List<Long> classIds) {
        // Kiểm tra học sinh tồn tại
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }

        Student student = studentOpt.get();

        // Xóa tất cả các đăng ký lớp hiện tại của học sinh
        studentClassRepository.deleteByStudentId(studentId);

        // Đăng ký học sinh vào các lớp mới
        if (classIds != null && !classIds.isEmpty()) {
            List<Long> validClassIds = classIds.stream()
                    .filter(classId -> classRepository.existsById(classId))
                    .collect(Collectors.toList());

            if (!validClassIds.isEmpty()) {
                List<StudentClass> studentClasses = validClassIds.stream()
                        .map(classId -> StudentClass.builder()
                                .studentId(studentId)
                                .classId(classId)
                                .isCompletedExam(false)
                                .build())
                        .collect(Collectors.toList());

                studentClassRepository.saveAll(studentClasses);
            }
        }

        // Tạo response
        StudentDTO studentDTO = StudentDTO.builder()
                .studentId(student.getStudentId())
                .userName(student.getUserName())
                .phoneNumber(student.getPhoneNumber())
                .email(student.getEmail())
                .status(student.getStatus())
                .build();

        return ApiResponse.<StudentDTO>builder()
                .message("Cập nhật lớp học cho học sinh thành công")
                .result(studentDTO)
                .build();
    }
    @Transactional
    public ApiResponse<StudentWithClassesDTO> getStudentWithClasses(Long studentId) {
        // Kiểm tra học sinh tồn tại
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }

        Student student = studentOpt.get();

        // Lấy danh sách lớp học của học sinh
        List<StudentClass> studentClasses = studentClassRepository.findByStudentId(studentId);
        List<ClassDTO> classDTOs = new ArrayList<>();

        for (StudentClass sc : studentClasses) {
            Optional<Class> classOpt = classRepository.findById(sc.getClassId());
            if (classOpt.isPresent()) {
                Class clazz = classOpt.get();
                ClassDTO classDTO = ClassDTO.builder()
                        .classId(clazz.getClassId())
                        .classCode(clazz.getClassCode())
                        .className(clazz.getClassName())
                        .description(clazz.getDescription())
                        .isActive(clazz.getIsActive())
                        .startDate(clazz.getStartDate().toString())
                        .endDate(clazz.getEndDate().toString())
                        .imageUrl(clazz.getImageUrl())
                        .build();

                classDTOs.add(classDTO);
            }
        }

        StudentDTO studentDTO = StudentDTO.builder()
                .studentId(student.getStudentId())
                .userName(student.getUserName())
                .phoneNumber(student.getPhoneNumber())
                .email(student.getEmail())
                .status(student.getStatus())
                .build();

        StudentWithClassesDTO responseDTO = StudentWithClassesDTO.builder()
                .student(studentDTO)
                .classes(classDTOs)
                .build();

        return ApiResponse.<StudentWithClassesDTO>builder()
                .message("Lấy thông tin học sinh và lớp học thành công")
                .result(responseDTO)
                .build();
    }
    @Transactional
    public ApiResponse<StudentDTO> updateStudent(UpdateStudentRequest request) {
        Optional<Student> studentOpt = studentRepository.findById(request.getStudentId());
        if (studentOpt.isEmpty()) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }

        Student student = studentOpt.get();
        student.setUserName(request.getUserName());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setEmail(request.getEmail());
        student.setStatus(request.getStatus() != null ? request.getStatus() : student.getStatus());

        Student updatedStudent = studentRepository.save(student);

        StudentDTO studentDTO = StudentDTO.builder()
                .studentId(updatedStudent.getStudentId())
                .userName(updatedStudent.getUserName())
                .phoneNumber(updatedStudent.getPhoneNumber())
                .email(updatedStudent.getEmail())
                .status(updatedStudent.getStatus())
                .build();

        return ApiResponse.<StudentDTO>builder()
                .message("Cập nhật học sinh thành công")
                .result(studentDTO)
                .build();
    }
    @Transactional
    public ApiResponse<Void> deleteStudent(Long studentId) {
        // Kiểm tra xem sinh viên có tồn tại và status = true
        Student student = studentRepository.findByIdAndStatusTrue(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        // Đặt status = false để xóa mềm
        student.setStatus(false);
        studentRepository.save(student);

        return ApiResponse.<Void>builder()
                .message("Xóa học sinh thành công")
                .build();
    }
    @Transactional
    public ApiResponse<Void> activeStudent(Long studentId) {
        // Kiểm tra xem sinh viên có tồn tại và status = true
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        // Đặt status = false để xóa mềm
        student.setStatus(true);
        studentRepository.save(student);

        return ApiResponse.<Void>builder()
                .message("Học sinh đã hoạt động")
                .build();
    }
    public ApiResponse<StudentDTO> getStudentByPhoneNumber(String phoneNumber) {
        Student student = studentRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        if (!student.getStatus()) {
            throw new AppException(ErrorCode.STUDENT_NOT_ACTIVE);
        }

        StudentDTO studentDTO = StudentDTO.builder()
                .studentId(student.getStudentId())
                .userName(student.getUserName())
                .phoneNumber(student.getPhoneNumber())
                .email(student.getEmail())
                .status(student.getStatus())
                .build();

        return ApiResponse.<StudentDTO>builder()
                .message("Lấy thông tin học sinh thành công")
                .result(studentDTO)
                .build();
    }


    public StudentExamDetailDTO getStudentExamDetail(Long studentId) {
        // 1. Lấy thông tin student
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        StudentDTO studentDTO = StudentDTO.builder()
                .studentId(student.getStudentId())
                .userName(student.getUserName())
                .phoneNumber(student.getPhoneNumber())
                .email(student.getEmail())
                .status(student.getStatus())
                .build();

        // 2. Lấy danh sách lớp học hiện tại của học sinh
        List<StudentClass> currentClasses = studentClassRepository.findByStudentId(studentId);
        Set<Long> currentClassIds = currentClasses.stream()
                .map(StudentClass::getClassId)
                .collect(Collectors.toSet());

        // 3. Lấy câu trả lời của student, chỉ cho những lớp học hiện tại
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findByStudentId(studentId)
                .stream()
                .filter(answer -> currentClassIds.contains(answer.getClassId()))
                .collect(Collectors.toList());

        log.info("Filtered student answers: " + studentAnswers.toString());

        // 4. Group by examId và classId
        Map<String, List<StudentAnswer>> examAnswersMap = studentAnswers.stream()
                .collect(Collectors.groupingBy(answer ->
                        answer.getExamId() + "_" + answer.getClassId()));

        // 5. Xây dựng danh sách ExamResultDTO
        List<ExamResultDTO> examResults = new ArrayList<>();

        for (Map.Entry<String, List<StudentAnswer>> entry : examAnswersMap.entrySet()) {
            String[] keys = entry.getKey().split("_");
            Long examId = Long.valueOf(keys[0]);
            Long classId = Long.valueOf(keys[1]);

            List<StudentAnswer> answers = entry.getValue();

            // Lấy thông tin exam và class
            Exam exam = examRepository.findById(examId).orElse(null);
            Class clazz = classRepository.findById(classId).orElse(null);

            if (exam != null && clazz != null) {
                // Xây dựng danh sách questions
                List<QuestionResultDTO> questionResults = buildQuestionResults(answers);

                ExamResultDTO examResult = ExamResultDTO.builder()
                        .examId(examId.toString())
                        .examName(exam.getTitle())
                        .className(clazz.getClassName())
                        .questions(questionResults)
                        .build();

                examResults.add(examResult);
            }
        }

        // 6. Trả về kết quả
        return StudentExamDetailDTO.builder()
                .student(studentDTO)
                .exams(examResults)
                .build();
    }

    // Method để lấy tất cả lịch sử kỳ thi (bao gồm cả lớp học cũ)
    public StudentExamDetailDTO getStudentExamHistory(Long studentId) {
        // 1. Lấy thông tin student
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        StudentDTO studentDTO = StudentDTO.builder()
                .studentId(student.getStudentId())
                .userName(student.getUserName())
                .phoneNumber(student.getPhoneNumber())
                .email(student.getEmail())
                .status(student.getStatus())
                .build();

        // 2. Lấy tất cả câu trả lời của student (không filter theo lớp)
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findByStudentId(studentId);

        // 3. Group by examId và classId
        Map<String, List<StudentAnswer>> examAnswersMap = studentAnswers.stream()
                .collect(Collectors.groupingBy(answer ->
                        answer.getExamId() + "_" + answer.getClassId()));

        // 4. Xây dựng danh sách ExamResultDTO
        List<ExamResultDTO> examResults = new ArrayList<>();

        for (Map.Entry<String, List<StudentAnswer>> entry : examAnswersMap.entrySet()) {
            String[] keys = entry.getKey().split("_");
            Long examId = Long.valueOf(keys[0]);
            Long classId = Long.valueOf(keys[1]);

            List<StudentAnswer> answers = entry.getValue();

            // Lấy thông tin exam và class
            Exam exam = examRepository.findById(examId).orElse(null);
            Class clazz = classRepository.findById(classId).orElse(null);

            if (exam != null && clazz != null) {
                // Xây dựng danh sách questions
                List<QuestionResultDTO> questionResults = buildQuestionResults(answers);

                ExamResultDTO examResult = ExamResultDTO.builder()
                        .examId(examId.toString())
                        .examName(exam.getTitle())
                        .className(clazz.getClassName())
                        .questions(questionResults)
                        .build();

                examResults.add(examResult);
            }
        }

        return StudentExamDetailDTO.builder()
                .student(studentDTO)
                .exams(examResults)
                .build();
    }
    private List<QuestionResultDTO> buildQuestionResults(List<StudentAnswer> studentAnswers) {
        List<QuestionResultDTO> questionResults = new ArrayList<>();

        for (StudentAnswer studentAnswer : studentAnswers) {
            // Lấy thông tin question
            Question question = questionRepository.findById(studentAnswer.getQuestionId())
                    .orElse(null);

            if (question != null) {
                // Lấy đáp án đúng
                Option correctOption = optionRepository.findByQuestionIdAndIsCorrect(
                        question.getQuestionId(), true);

                // Lấy đáp án học sinh chọn
                Option studentOption = optionRepository.findById(studentAnswer.getOptionId())
                        .orElse(null);

                String correctAnswer = correctOption != null ? correctOption.getContent() : "";
                String studentAnswerContent = studentOption != null ? studentOption.getContent() : "";

                QuestionResultDTO questionResult = QuestionResultDTO.builder()
                        .content(question.getContent())
                        .correctAnswer(correctAnswer)
                        .studentAnswer(studentAnswerContent)
                        .build();

                questionResults.add(questionResult);
            }
        }

        return questionResults;
    }

    // Method alternatif nếu muốn lấy kết quả cho một exam cụ thể
    public StudentExamDetailDTO getStudentExamDetailByExam(Long studentId, Long examId, Long classId) {
        // Lấy thông tin student
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        StudentDTO studentDTO = StudentDTO.builder()
                .studentId(student.getStudentId())
                .userName(student.getUserName())
                .phoneNumber(student.getPhoneNumber())
                .email(student.getEmail())
                .status(student.getStatus())
                .build();

        // Lấy câu trả lời cho exam cụ thể
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findByStudentId(studentId)
                .stream()
                .filter(answer -> answer.getExamId().equals(examId) && answer.getClassId().equals(classId))
                .collect(Collectors.toList());

        // Lấy thông tin exam và class
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        Class clazz = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        // Xây dựng questions
        List<QuestionResultDTO> questionResults = buildQuestionResults(studentAnswers);

        ExamResultDTO examResult = ExamResultDTO.builder()
                .examId(examId.toString())
                .examName(exam.getTitle())
                .className(clazz.getClassName())
                .questions(questionResults)
                .build();

        return StudentExamDetailDTO.builder()
                .student(studentDTO)
                .exams(Arrays.asList(examResult))
                .build();
    }


}