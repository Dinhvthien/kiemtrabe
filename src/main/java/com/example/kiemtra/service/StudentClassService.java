package com.example.kiemtra.service;

import com.example.kiemtra.dto.*;
import com.example.kiemtra.dto.request.StudentClassRequestDTO;
import com.example.kiemtra.entity.Student;
import com.example.kiemtra.entity.Class;
import com.example.kiemtra.entity.StudentClass;
import com.example.kiemtra.exception.AppException;
import com.example.kiemtra.exception.ErrorCode;
import com.example.kiemtra.repository.ClassRepository;
import com.example.kiemtra.repository.StudentClassRepository;
import com.example.kiemtra.repository.StudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentClassService {

     StudentRepository studentRepository;
     ClassRepository classRepository;
     StudentClassRepository studentClassRepository;

    @Transactional
    public ApiResponse<StudentClassResponseDTO> registerStudentToClass(StudentClassRequestDTO request) {
        // Kiểm tra học sinh tồn tại
        Optional<Student> studentOpt = studentRepository.findById(request.getStudentId());
        if (studentOpt.isEmpty()) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }

        // Kiểm tra lớp học tồn tại
        Optional<Class> classOpt = classRepository.findById(request.getClassId());
        if (classOpt.isEmpty()) {
            throw new AppException(ErrorCode.CLASS_NOT_FOUND);
        }

        // Kiểm tra học sinh đã đăng ký lớp chưa
        if (studentClassRepository.existsByStudentIdAndClassId(request.getStudentId(), request.getClassId())) {
            throw new AppException(ErrorCode.STUDENT_CLASS_EXISTS);
        }

        // Tạo bản ghi mới
        StudentClass studentClass = StudentClass.builder()
                .studentId(request.getStudentId())
                .classId(request.getClassId())
                .isCompletedExam(false)
                .build();

        StudentClass savedStudentClass = studentClassRepository.save(studentClass);

        // Tạo response
        Student student = studentOpt.get();
        Class clazz = classOpt.get();

        StudentDTO studentDTO = StudentDTO.builder()
                .studentId(student.getStudentId())
                .userName(student.getUserName())
                .phoneNumber(student.getPhoneNumber())
                .email(student.getEmail())
                .status(student.getStatus())
                .build();

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

        StudentClassResponseDTO responseDTO = StudentClassResponseDTO.builder()
                .studentClassId(savedStudentClass.getStudentClassId())
                .student(studentDTO)
                .clazz(classDTO)
                .isCompletedExam(savedStudentClass.getIsCompletedExam())
                .build();

        return ApiResponse.<StudentClassResponseDTO>builder()
                .message("Đăng ký học sinh vào lớp thành công")
                .result(responseDTO)
                .build();
    }

    public ApiResponse<List<StudentClassResponseDTO>> getStudentsByClass(Long classId) {
        // Kiểm tra lớp học tồn tại
        if (!classRepository.existsById(classId)) {
            return ApiResponse.<List<StudentClassResponseDTO>>builder()
                    .message("Lớp học không tồn tại")
                    .build();
        }

        List<StudentClass> studentClasses = studentClassRepository.findByClassId(classId);
        List<StudentClassResponseDTO> responseDTOs = studentClasses.stream()
                .map(sc -> {
                    Student student = studentRepository.findById(sc.getStudentId()).orElse(null);
                    Class clazz = classRepository.findById(sc.getClassId()).orElse(null);

                    if (student == null || clazz == null) {
                        return null;
                    }

                    StudentDTO studentDTO = StudentDTO.builder()
                            .studentId(student.getStudentId())
                            .userName(student.getUserName())
                            .phoneNumber(student.getPhoneNumber())
                            .email(student.getEmail())
                            .status(student.getStatus())
                            .build();

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

                    return StudentClassResponseDTO.builder()
                            .studentClassId(sc.getStudentClassId())
                            .student(studentDTO)
                            .clazz(classDTO)
                            .isCompletedExam(sc.getIsCompletedExam())
                            .build();
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

        return ApiResponse.<List<StudentClassResponseDTO>>builder()
                .message("Lấy danh sách học sinh theo lớp thành công")
                .result(responseDTOs)
                .build();
    }
}