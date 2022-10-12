package com.zerobase.parkinglot.auth;

import com.zerobase.parkinglot.member.entity.Member;
import java.util.Arrays;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithAuthUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthUser> {

    @Override
    public SecurityContext createSecurityContext(WithAuthUser annotation) {
        String email = annotation.email();
        String role = annotation.role();

        Member member = Member.builder()
            .email(email)
            .role(role)
            .build();

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(member,
            "password", Arrays.asList(new SimpleGrantedAuthority(role)));

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);

        return context;
    }
}
