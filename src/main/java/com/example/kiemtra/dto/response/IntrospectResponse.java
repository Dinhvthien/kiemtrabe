package com.example.kiemtra.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
// data se tao ra RequiredArgsConstructor phaỉ có các thuôcj tính như là final hoặc notnull mới thêm vào trong contructor
@NoArgsConstructor //
@AllArgsConstructor // 2 muc nay ghi de len cai RequiredArgsConstructor cua lombol
@Builder //cai nay de build mot object ma k can phai new
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectResponse {
    boolean valid;
}
