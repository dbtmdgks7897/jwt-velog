package com.example.jwtvelog.auth.session;

import com.example.jwtvelog.model.member.entity.MemberEntity;
import com.example.jwtvelog.model.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserByIdxService {

    // 유저 정보를 찾아오기 위해 MemberRepository를 주입받는다.
    private final MemberRepository memberRepository;

    public CustomUserDetails loadUserByIdx(Long idx) {
        // idx로 유저 정보를 찾아온다.
        // 존재하지 않는다면 UsernameNotFoundException을 발생시킨다.
        MemberEntity userEntity = memberRepository.findByIdx(idx).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + idx)
        );
        // 유저 정보를 기반으로 CustomUserDetails를 생성하여 반환한다.
        return new CustomUserDetails(userEntity);
    }
}

