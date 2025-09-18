package com.example.kiemtra.service;

import com.example.kiemtra.dto.request.listquestion.ExamQuestionRequest;
import com.example.kiemtra.dto.request.listquestion.QuestionRequest;
import com.example.kiemtra.dto.response.listquestion.QuestionResponse;
import com.example.kiemtra.dto.response.listquestion.OptionResponse;
import com.example.kiemtra.dto.response.listquestion.ExamQuestionResponse;
import com.example.kiemtra.entity.Exam;
import com.example.kiemtra.entity.Question;
import com.example.kiemtra.entity.Option;
import com.example.kiemtra.exception.AppException;
import com.example.kiemtra.exception.ErrorCode;
import com.example.kiemtra.repository.ExamRepository;
import com.example.kiemtra.repository.QuestionRepository;
import com.example.kiemtra.repository.OptionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamQuestionService {

    ExamRepository examRepository;
    QuestionRepository questionRepository;
    OptionRepository optionRepository;

    // ExamQuestionService.java

    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
        optionRepository.deleteByQuestionId(questionId);
        questionRepository.delete(question);
    }

    public ExamQuestionResponse updateQuestion(Long questionId, QuestionRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        question.setContent(request.getContent());
        questionRepository.save(question);

        optionRepository.deleteByQuestionId(questionId);

        List<OptionResponse> optionResponses = request.getOptions().stream().map(oReq -> {
            Option option = Option.builder()
                    .questionId(questionId)
                    .optionLabel(oReq.getOptionLabel())
                    .content(oReq.getContent())
                    .isCorrect(oReq.getIsCorrect() != null ? oReq.getIsCorrect() : false)
                    .build();
            Option savedOption = optionRepository.save(option);

            return OptionResponse.builder()
                    .optionId(savedOption.getOptionId())
                    .optionLabel(savedOption.getOptionLabel())
                    .content(savedOption.getContent())
                    .isCorrect(savedOption.getIsCorrect())
                    .build();
        }).collect(Collectors.toList());

        QuestionResponse updatedQuestion = QuestionResponse.builder()
                .questionId(question.getQuestionId())
                .content(question.getContent())
                .options(optionResponses)
                .build();

        return ExamQuestionResponse.builder()
                .examId(question.getExamId())
                .questions(List.of(updatedQuestion))
                .build();
    }
    public ExamQuestionResponse getQuestionsByExamId(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        List<Question> questions = questionRepository.findByExamId(examId);

        List<QuestionResponse> questionResponses = questions.stream().map(q -> {
            List<Option> options = optionRepository.findByQuestionId(q.getQuestionId());
            List<OptionResponse> optionResponses = options.stream().map(o -> OptionResponse.builder()
                    .optionId(o.getOptionId())
                    .optionLabel(o.getOptionLabel())
                    .content(o.getContent())
                    .isCorrect(o.getIsCorrect())
                    .build()).collect(Collectors.toList());

            return QuestionResponse.builder()
                    .questionId(q.getQuestionId())
                    .content(q.getContent())
                    .options(optionResponses)
                    .build();
        }).collect(Collectors.toList());

        return ExamQuestionResponse.builder()
                .examId(exam.getExamId())
                .questions(questionResponses)
                .build();
    }

    // tao 1
    public QuestionResponse createQuestion(QuestionRequest request) {
        // Kiểm tra xem Exam có tồn tại không
        examRepository.findById(request.getExamId())
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        Question question = Question.builder()
                .content(request.getContent())
                .examId(request.getExamId())
                .build();
        Question savedQuestion = questionRepository.save(question);

        List<OptionResponse> optionResponses = request.getOptions().stream()
                .map(optionRequest -> {

                    Option option = Option.builder()
                            .questionId(savedQuestion.getQuestionId())
                            .optionLabel(optionRequest.getOptionLabel())
                            .content(optionRequest.getContent())
                            .isCorrect(optionRequest.getIsCorrect() != null ? optionRequest.getIsCorrect() : false)
                            .build();
                    Option savedOption = optionRepository.save(option);

                    return OptionResponse.builder()
                            .optionId(savedOption.getOptionId())
                            .optionLabel(savedOption.getOptionLabel())
                            .content(savedOption.getContent())
                            .isCorrect(savedOption.getIsCorrect())
                            .build();
                })
                .collect(Collectors.toList());

        return QuestionResponse.builder()
                .questionId(savedQuestion.getQuestionId())
                .content(savedQuestion.getContent())
                .options(optionResponses)
                .build();
    }

    // tao nhieu
    public ExamQuestionResponse createExamQuestions(ExamQuestionRequest request) {
        // Kiểm tra xem Exam có tồn tại không
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        List<QuestionResponse> questionResponses = request.getQuestions().stream()
                .map(questionRequest -> {
                    // Kiểm tra xem câu hỏi đã tồn tại chưa
                    Question question = Question.builder()
                            .content(questionRequest.getContent())
                            .examId(request.getExamId())
                            .build();
                    Question savedQuestion = questionRepository.save(question);

                    List<OptionResponse> optionResponses = questionRequest.getOptions().stream()
                            .map(optionRequest -> {
                                // Kiểm tra xem option đã tồn tại chưa

                                Option option = Option.builder()
                                        .questionId(savedQuestion.getQuestionId())
                                        .optionLabel(optionRequest.getOptionLabel())
                                        .content(optionRequest.getContent())
                                        .isCorrect(optionRequest.getIsCorrect() != null ? optionRequest.getIsCorrect() : false)
                                        .build();
                                Option savedOption = optionRepository.save(option);

                                return OptionResponse.builder()
                                        .optionId(savedOption.getOptionId())
                                        .optionLabel(savedOption.getOptionLabel())
                                        .content(savedOption.getContent())
                                        .isCorrect(savedOption.getIsCorrect())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return QuestionResponse.builder()
                            .questionId(savedQuestion.getQuestionId())
                            .content(savedQuestion.getContent())
                            .options(optionResponses)
                            .build();
                })
                .collect(Collectors.toList());

        return ExamQuestionResponse.builder()
                .examId(exam.getExamId())
                .questions(questionResponses)
                .build();
    }
}