package com.example.kiemtra.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
// data se tao r1a RequiredArgsConstructor phaỉ có các thuôcj tính như là final hoặc notnull mới thêm vào trong contructor
@NoArgsConstructor //
@AllArgsConstructor // 2 muc nay ghi de len cai RequiredArgsConstructor cua lombol
@Builder //cai nay de build mot object ma k can phai new
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     long id;
     long userId;
     long roleId;
}
