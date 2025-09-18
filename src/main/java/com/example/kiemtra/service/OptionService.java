package com.example.kiemtra.service;

import com.example.kiemtra.dto.request.OptionRequest;
import com.example.kiemtra.dto.response.OptionResponse;

import com.example.kiemtra.entity.Option;
import com.example.kiemtra.exception.AppException;
import com.example.kiemtra.exception.ErrorCode;
import com.example.kiemtra.repository.OptionRepository;
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
public class OptionService {

    OptionRepository optionRepository;

    public OptionResponse createOption(OptionRequest optionRequest) {
        if (optionRepository.existsByQuestionIdAndOptionLabel(optionRequest.getQuestionId(), optionRequest.getOptionLabel())) {
            throw new AppException(ErrorCode.OPTION_ALREADY_EXISTS);
        }

        Option option = Option.builder()
                .questionId(optionRequest.getQuestionId())
                .optionLabel(optionRequest.getOptionLabel())
                .content(optionRequest.getContent())
                .isCorrect(optionRequest.getIsCorrect() != null ? optionRequest.getIsCorrect() : false)
                .build();

        Option savedOption = optionRepository.save(option);
        return OptionResponse.builder()
                .optionId(savedOption.getOptionId())
                .questionId(savedOption.getQuestionId())
                .optionLabel(savedOption.getOptionLabel())
                .content(savedOption.getContent())
                .isCorrect(savedOption.getIsCorrect())
                .build();
    }

    public PageResponse<OptionResponse> getOptions(int page, int size, String search) {
        Sort sort = Sort.by(Sort.Direction.DESC, "optionId");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Option> optionPage = optionRepository.findBySearch(pageable, search);

        List<OptionResponse> contentList = optionPage.getContent().stream()
                .map(option -> OptionResponse.builder()
                        .optionId(option.getOptionId())
                        .questionId(option.getQuestionId())
                        .optionLabel(option.getOptionLabel())
                        .content(option.getContent())
                        .isCorrect(option.getIsCorrect())
                        .build())
                .collect(Collectors.toList());

        PageResponse<OptionResponse> response = PageResponse.<OptionResponse>builder()
                .content(contentList)
                .page(optionPage.getNumber())
                .size(optionPage.getSize())
                .totalElements(optionPage.getTotalElements())
                .totalPages(optionPage.getTotalPages())
                .first(optionPage.isFirst())
                .last(optionPage.isLast())
                .build();

        return response;
    }

    public OptionResponse getById(Long id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.OPTION_NOT_FOUND));
        return OptionResponse.builder()
                .optionId(option.getOptionId())
                .questionId(option.getQuestionId())
                .optionLabel(option.getOptionLabel())
                .content(option.getContent())
                .isCorrect(option.getIsCorrect())
                .build();
    }

    public OptionResponse update(Long id, OptionRequest request) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.OPTION_NOT_FOUND));

        if (!option.getOptionLabel().equals(request.getOptionLabel()) ||
                !option.getQuestionId().equals(request.getQuestionId())) {
            if (optionRepository.existsByQuestionIdAndOptionLabel(request.getQuestionId(), request.getOptionLabel())) {
                throw new AppException(ErrorCode.OPTION_ALREADY_EXISTS);
            }
        }

        option.setQuestionId(request.getQuestionId());
        option.setOptionLabel(request.getOptionLabel());
        option.setContent(request.getContent());
        option.setIsCorrect(request.getIsCorrect() != null ? request.getIsCorrect() : false);

        Option updatedOption = optionRepository.save(option);
        return OptionResponse.builder()
                .optionId(updatedOption.getOptionId())
                .questionId(updatedOption.getQuestionId())
                .optionLabel(updatedOption.getOptionLabel())
                .content(updatedOption.getContent())
                .isCorrect(updatedOption.getIsCorrect())
                .build();
    }

    public void delete(Long id) {
        if (!optionRepository.existsById(id)) {
            throw new AppException(ErrorCode.OPTION_NOT_FOUND);
        }
        optionRepository.deleteById(id);
    }
}