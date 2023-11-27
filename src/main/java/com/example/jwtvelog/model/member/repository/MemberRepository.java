package com.example.jwtvelog.model.member.repository;

import com.example.jwtvelog.model.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    // 이메일로 유저 정보를 찾아온다.
    Optional<MemberEntity> findByEmail(String email);

    // idx로 유저 정보를 찾아온다.
    Optional<MemberEntity> findByIdx(Long idx);
}
