package com.example.kiemtra.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
// data se tao r1a RequiredArgsConstructor phaỉ có các thuôcj tính như là final hoặc notnull mới thêm vào trong contructor
@NoArgsConstructor //
@AllArgsConstructor // 2 muc nay ghi de len cai RequiredArgsConstructor cua lombol
@Builder //cai nay de build mot object ma k can phai new
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // trường này có tác dụng khi nào mà chuyển object này sang json các trường bị null
    public class ApiResponse<T> {         // thì sẽ bị ẩn đi
    Integer  code;
    String message;
    T result;
    }
