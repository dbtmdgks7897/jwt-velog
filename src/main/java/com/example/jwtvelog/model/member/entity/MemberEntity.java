package com.example.jwtvelog.model.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "idx", callSuper = false)
@Entity
@Table(name = "`MEMBER`")
@DynamicInsert
@DynamicUpdate
public class MemberEntity {

    // Idx
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", updatable = false)
    private Long idx;

    // 이메일
    @Column(name = "email", nullable = false)
    private String email;

    // 패스워드
    // 인코딩된 문자열
    @Column(name = "password", nullable = false)
    private String password;

    // role
    @Column(name = "role", nullable = false)
    private String role;

}
