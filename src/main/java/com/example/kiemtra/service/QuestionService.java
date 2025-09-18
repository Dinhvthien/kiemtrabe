package com.example.kiemtra.service;

import com.example.kiemtra.dto.request.QuestionRequest;
import com.example.kiemtra.dto.response.QuestionResponse;
import com.example.kiemtra.entity.Question;
import com.example.kiemtra.exception.AppException;
import com.example.kiemtra.exception.ErrorCode;
import com.example.kiemtra.repository.OptionRepository;
import com.example.kiemtra.repository.QuestionRepository;
import com.example.kiemtra.util.PageResponse;
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
public class QuestionService {

    QuestionRepository questionRepository;
    OptionRepository optionRepository;


    public QuestionResponse createQuestion(QuestionRequest questionRequest) {
        if (questionRepository.existsByContentAndExamId(questionRequest.getContent(), questionRequest.getExamId())) {
            throw new AppException(ErrorCode.QUESTION_ALREADY_EXISTS);
        }

        Question question = Question.builder()
                .content(questionRequest.getContent())
                .examId(questionRequest.getExamId())
                .build();

        Question savedQuestion = questionRepository.save(question);
        return QuestionResponse.builder()
                .questionId(savedQuestion.getQuestionId())
                .content(savedQuestion.getContent())
                .examId(savedQuestion.getExamId())
                .build();
    }

    public PageResponse<QuestionResponse> getQuestions(int page, int size, String search) {
        Sort sort = Sort.by(Sort.Direction.DESC, "questionId");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Question> questionPage = questionRepository.findBySearch(pageable, search);

        List<QuestionResponse> contentList = questionPage.getContent().stream()
                .map(question -> QuestionResponse.builder()
                        .questionId(question.getQuestionId())
                        .content(question.getContent())
                        .examId(question.getExamId())
                        .build())
                .collect(Collectors.toList());

        PageResponse<QuestionResponse> response = PageResponse.<QuestionResponse>builder()
                .content(contentList)
                .page(questionPage.getNumber())
                .size(questionPage.getSize())
                .totalElements(questionPage.getTotalElements())
                .totalPages(questionPage.getTotalPages())
                .first(questionPage.isFirst())
                .last(questionPage.isLast())
                .build();

        return response;
    }

    public QuestionResponse getById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
        return QuestionResponse.builder()
                .questionId(question.getQuestionId())
                .content(question.getContent())
                .examId(question.getExamId())
                .build();
    }

    public QuestionResponse update(Long id, QuestionRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        if (!question.getContent().equals(request.getContent()) ||
                !question.getExamId().equals(request.getExamId())) {
            if (questionRepository.existsByContentAndExamId(request.getContent(), request.getExamId())) {
                throw new AppException(ErrorCode.QUESTION_ALREADY_EXISTS);
            }
        }

        question.setContent(request.getContent());
        question.setExamId(request.getExamId());

        Question updatedQuestion = questionRepository.save(question);
        return QuestionResponse.builder()
                .questionId(updatedQuestion.getQuestionId())
                .content(updatedQuestion.getContent())
                .examId(updatedQuestion.getExamId())
                .build();
    }

    public void delete(Long id) {
        // Kiểm tra và lấy Question
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
        // Xóa tất cả Option liên quan trước
        optionRepository.deleteByQuestionId(question.getQuestionId());  // Giả sử có method này
        // Xóa Question
        questionRepository.delete(question);
    }


}