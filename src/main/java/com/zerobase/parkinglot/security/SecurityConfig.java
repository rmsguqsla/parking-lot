package com.zerobase.parkinglot.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter authenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        http
            .httpBasic().disable() // rest api 이므로 기본설정 사용안함. 기본설정은 비인증시 로그인폼 화면으로 리다이렉트
            .csrf().disable() // rest api 이므로 crsf 보안이 필요없음
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Jwt를 사용하므로 세션 필요없음(Jwt는 상태를 가지고 있지 않음)
            .and()
                .authorizeRequests()
                    .antMatchers("/**/signup", "/**/signin").permitAll() // 회원가입과 로그인은 모두 접근 허용
            .and()
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(final WebSecurity web) throws Exception{
        web.ignoring()
            .antMatchers("/swagger-ui/**");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
    
}
