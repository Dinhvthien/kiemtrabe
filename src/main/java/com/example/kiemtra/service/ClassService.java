package com.example.kiemtra.service;

import com.example.kiemtra.dto.request.ClassRequest;
import com.example.kiemtra.dto.response.ClassResponse;
import com.example.kiemtra.entity.Class;
import com.example.kiemtra.entity.ClassExam;
import com.example.kiemtra.entity.Exam;
import com.example.kiemtra.exception.AppException;
import com.example.kiemtra.exception.ErrorCode;
import com.example.kiemtra.repository.ClassExamRepository;
import com.example.kiemtra.repository.ClassRepository;
import com.example.kiemtra.repository.ExamRepository;
import com.example.kiemtra.util.PageResponse;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassService {

    ClassRepository classRepository;
    ExamRepository examRepository;
    ClassExamRepository classExamRepository;

    // CREATE
    public ClassResponse createClass(ClassRequest classRequest) {
        // 1. Check classCode
        if (classRepository.existsByClassCode(classRequest.getClassCode())) {
            throw new AppException(ErrorCode.CLASS_CODE_EXISTS);
        }

        // 2. Check toàn bộ examIds trước khi tạo
        if (classRequest.getExamIds() != null && !classRequest.getExamIds().isEmpty()) {
            for (Long examId : classRequest.getExamIds()) {
                if (!examRepository.existsById(examId)) {
                    throw new AppException(ErrorCode.EXAM_NOT_FOUND);
                }
            }
        }

        // 3. Tạo Class
        Class classEntity = Class.builder()
                .classCode(classRequest.getClassCode())
                .className(classRequest.getClassName())
                .description(classRequest.getDescription())
                .startDate(classRequest.getStartDate())
                .endDate(classRequest.getEndDate())
                .isDeleted(false)
                .imageUrl(classRequest.getImageUrl())
                .build();

        Class savedClass = classRepository.save(classEntity);

        // 4. Tạo ClassExam
        if (classRequest.getExamIds() != null && !classRequest.getExamIds().isEmpty()) {
            for (Long examId : classRequest.getExamIds()) {
                ClassExam classExam = ClassExam.builder()
                        .classId(savedClass.getClassId())
                        .examId(examId)
                        .build();
                classExamRepository.save(classExam);
            }
        }

        // 5. Trả về
        return ClassResponse.builder()
                .classId(savedClass.getClassId())
                .classCode(savedClass.getClassCode())
                .className(savedClass.getClassName())
                .description(savedClass.getDescription())
                .startDate(savedClass.getStartDate())
                .endDate(savedClass.getEndDate())
                .imageUrl(savedClass.getImageUrl())
                .build();
    }

    // READ - Get all with pagination and search
    public PageResponse<ClassResponse> getClasses(int page, int size, String search) {
        Sort sort = Sort.by(Sort.Direction.DESC, "classId");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Class> classPage = classRepository.findBySearchAndIsDeletedFalse(pageable, search);
        List<ClassResponse> contentList = classPage.getContent().stream()
                .map(classEntity -> ClassResponse.builder()
                        .classId(classEntity.getClassId())
                        .classCode(classEntity.getClassCode())
                        .className(classEntity.getClassName())
                        .description(classEntity.getDescription())
                        .startDate(classEntity.getStartDate())
                        .endDate(classEntity.getEndDate())
                        .imageUrl(classEntity.getImageUrl()) // Thêm imageUrl
                        .build())
                .collect(Collectors.toList());

        PageResponse<ClassResponse> response = PageResponse.<ClassResponse>builder()
                .content(contentList)
                .page(classPage.getNumber())
                .size(classPage.getSize())
                .totalElements(classPage.getTotalElements())
                .totalPages(classPage.getTotalPages())
                .first(classPage.isFirst())
                .last(classPage.isLast())
                .build();

        return response;
    }

    // READ - Get by id
    public ClassResponse getById(Long id) {
        Class classEntity = classRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        return ClassResponse.builder()
                .classId(classEntity.getClassId())
                .classCode(classEntity.getClassCode())
                .className(classEntity.getClassName())
                .description(classEntity.getDescription())
                .startDate(classEntity.getStartDate())
                .endDate(classEntity.getEndDate())
                .imageUrl(classEntity.getImageUrl()) // Thêm imageUrl
                .build();
    }

    // UPDATE
    @Transactional
    public ClassResponse update(Long id, ClassRequest classRequest) {
        // 1. Tìm class
        Class classEntity = classRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        // 2. Check trùng classCode
        if (!classEntity.getClassCode().equals(classRequest.getClassCode()) &&
                classRepository.existsByClassCode(classRequest.getClassCode())) {
            throw new AppException(ErrorCode.CLASS_CODE_EXISTS);
        }

        // 3. Cập nhật thông tin cơ bản
        classEntity.setClassCode(classRequest.getClassCode());
        classEntity.setClassName(classRequest.getClassName());
        classEntity.setDescription(classRequest.getDescription());
        classEntity.setStartDate(classRequest.getStartDate());
        classEntity.setEndDate(classRequest.getEndDate());
        classEntity.setImageUrl(classRequest.getImageUrl());

        // 4. Cập nhật danh sách exam
        if (classRequest.getExamIds() != null) {
            // Xóa quan hệ cũ
             classExamRepository.deleteByClassId(classEntity.getClassId());

            // Check examId hợp lệ
            List<Exam> exams = examRepository.findAllById(classRequest.getExamIds());
            if (exams.size() != classRequest.getExamIds().size()) {
                throw new AppException(ErrorCode.EXAM_NOT_FOUND);
            }

            // Thêm lại quan hệ mới
            List<ClassExam> classExams = exams.stream()
                    .map(exam -> ClassExam.builder()
                            .classId(classEntity.getClassId())
                            .examId(exam.getExamId())
                            .build())
                    .toList();

            classExamRepository.saveAll(classExams);
        }

        // 5. Lưu lại class
        Class updatedClass = classRepository.save(classEntity);

        // 6. Build response (trả kèm examIds)
        return ClassResponse.builder()
                .classId(updatedClass.getClassId())
                .classCode(updatedClass.getClassCode())
                .className(updatedClass.getClassName())
                .description(updatedClass.getDescription())
                .startDate(updatedClass.getStartDate())
                .endDate(updatedClass.getEndDate())
                .imageUrl(updatedClass.getImageUrl())// gắn lại danh sách examIds
                .build();
    }

    // DELETE
     @Transactional
    public void delete(Long id) {
        // Kiểm tra xem bản ghi có tồn tại và chưa bị xóa mềm
        Class classEntity = classRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        // Đặt isDeleted = true
        classEntity.setIsDeleted(true);
        classRepository.save(classEntity);
    }
}