package com.example.jwtvelog.auth.session;

import com.example.jwtvelog.model.member.entity.MemberEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter
public class CustomUserDetails implements UserDetails {
    // 유저 정보를 담을 필드
    private MemberEntity member;

    // 생성자로 MemberEntity를 받아서 CustomUserDetails를 생성한다.
    public CustomUserDetails(MemberEntity member) {
        this.member = member;
    }

    // 권한을 가져온다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collector = new ArrayList<>();
        collector.add(() -> member.getRole());
        return collector;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    // 계정이 만료되지 않았는지 확인한다.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겨있지 않은지 확인한다.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 계정의 인증 정보가 만료되지 않았는지 확인한다.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화되어있는지 확인한다.
    @Override
    public boolean isEnabled() {
        return true;
    }
}