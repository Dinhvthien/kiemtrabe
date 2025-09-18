package com.example.kiemtra.service;

import com.example.kiemtra.dto.request.ClassExamRequest;
import com.example.kiemtra.dto.response.ClassExamResponse;
import com.example.kiemtra.entity.Class;
import com.example.kiemtra.entity.ClassExam;
import com.example.kiemtra.exception.AppException;
import com.example.kiemtra.exception.ErrorCode;
import com.example.kiemtra.repository.ClassExamRepository;
import com.example.kiemtra.repository.ClassRepository;
import com.example.kiemtra.repository.ExamRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassExamService {

    ClassExamRepository classExamRepository;
    ClassRepository classRepository;
    ExamRepository examRepository;
    public ClassExamResponse createClassExam(ClassExamRequest request) {
        classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        // 2. Check exam tồn tại
        examRepository.findById(request.getExamId())
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        if (classExamRepository.existsByClassIdAndExamId(request.getClassId(), request.getExamId())) {
            throw new AppException(ErrorCode.CLASS_EXAM_ALREADY_EXISTS);
        }

        ClassExam classExam = ClassExam.builder()
                .classId(request.getClassId())
                .examId(request.getExamId())
                .examDate(request.getExamDate())
                .build();

        ClassExam savedClassExam = classExamRepository.save(classExam);
        return ClassExamResponse.builder()
                .classExamId(savedClassExam.getClassExamId())
                .classId(savedClassExam.getClassId())
                .examId(savedClassExam.getExamId())
                .examDate(savedClassExam.getExamDate())
                .build();
    }

    public ClassExamResponse getById(Long id) {
        ClassExam classExam = classExamRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_EXAM_NOT_FOUND));
        return ClassExamResponse.builder()
                .classExamId(classExam.getClassExamId())
                .classId(classExam.getClassId())
                .examId(classExam.getExamId())
                .examDate(classExam.getExamDate())
                .build();
    }
    public List<ClassExamResponse> getall() {
        List<ClassExam> classExams = classExamRepository.findAll();
        return classExams.stream()
                .map(classExam -> ClassExamResponse.builder()
                        .classExamId(classExam.getClassExamId())
                        .classId(classExam.getClassId())
                        .examId(classExam.getExamId())
                        .examDate(classExam.getExamDate())
                        .build())
                .toList();
    }

    public List<ClassExamResponse> getallbyclassid(Long classId) {
        List<ClassExam> classExams = classExamRepository.findByClassId(classId);
        return classExams.stream()
                .map(classExam -> ClassExamResponse.builder()
                        .classExamId(classExam.getClassExamId())
                        .classId(classExam.getClassId())
                        .examId(classExam.getExamId())
                        .examDate(classExam.getExamDate())
                        .build())
                .toList();
    }
    public ClassExamResponse update(Long id, ClassExamRequest request) {
        ClassExam classExam = classExamRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_EXAM_NOT_FOUND));

        if (!classExam.getClassId().equals(request.getClassId()) ||
                !classExam.getExamId().equals(request.getExamId())) {
            if (classExamRepository.existsByClassIdAndExamId(request.getClassId(), request.getExamId())) {
                throw new AppException(ErrorCode.CLASS_EXAM_ALREADY_EXISTS);
            }
        }

        classExam.setClassId(request.getClassId());
        classExam.setExamId(request.getExamId());
        classExam.setExamDate(request.getExamDate());

        ClassExam updatedClassExam = classExamRepository.save(classExam);
        return ClassExamResponse.builder()
                .classExamId(updatedClassExam.getClassExamId())
                .classId(updatedClassExam.getClassId())
                .examId(updatedClassExam.getExamId())
                .examDate(updatedClassExam.getExamDate())
                .build();
    }

    public void delete(Long id) {
        if (!classExamRepository.existsById(id)) {
            throw new AppException(ErrorCode.CLASS_EXAM_NOT_FOUND);
        }
        classExamRepository.deleteById(id);
    }
}