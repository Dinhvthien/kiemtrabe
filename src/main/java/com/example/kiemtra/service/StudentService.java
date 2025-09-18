package com.example.kiemtra.service;

import com.example.kiemtra.dto.*;
import com.example.kiemtra.dto.request.UpdateStudentRequest;
import com.example.kiemtra.entity.Class;
import com.example.kiemtra.entity.Student;
import com.example.kiemtra.entity.StudentClass;
import com.example.kiemtra.exception.AppException;
import com.example.kiemtra.exception.ErrorCode;
import com.example.kiemtra.repository.ClassRepository;
import com.example.kiemtra.repository.StudentClassRepository;
import com.example.kiemtra.repository.StudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentService {
    StudentRepository studentRepository;
    ClassRepository classRepository;
    StudentClassRepository studentClassRepository;

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
                    .findByUserNameContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(search, search, pageable);
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
        if (!studentRepository.existsById(studentId)) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }

        // Xóa các StudentClass liên quan trước
        studentClassRepository.deleteByStudentId(studentId);

        // Xóa sinh viên
        studentRepository.deleteById(studentId);

        return ApiResponse.<Void>builder()
                .message("Xóa học sinh thành công")
                .build();
    }
    public ApiResponse<StudentDTO> getStudentByPhoneNumber(String phoneNumber) {
        Optional<Student> studentOpt = studentRepository.findByPhoneNumber(phoneNumber);
        if (studentOpt.isEmpty()) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }

        Student student = studentOpt.get();
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



}