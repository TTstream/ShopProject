package com.shopping.config;

import com.shopping.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration // 현재 java 클래스는 Spring의 환경설정과 관련된 파일이라는것을 알려주게 된다.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    MemberService memberService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // http요청에 대한 보안을 설정
        // 페이지 권한 설정, 로그인 페이지 설정, 로그아웃 메소드 등에 대한 설정

        http.sessionManagement() //이 설정을 해줘야 회원가입 페이지가 보인다.
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
        //ALWAYS 스프링시큐리티가 항상 세션을 생성
        //IF_REQUIRED 스프링시큐리티가 필요시 생성
        //NEVER 스프링시큐리티가 생성하지않지만, 기존에 존재하면 사용
        //STATELESS 스프링시큐리티가 생성하지도 않고 기존것을 사용하지도 않음

        http.formLogin()
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
                .usernameParameter("email")
                .failureUrl("/members/login/error")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/");

        http.authorizeRequests()
                .mvcMatchers("/","/members/**",
                        "/item/**","/images/**").permitAll()
                /* permitALL()을 통해 모든 사용자가 인증없이 해당 경로에 접근할 수 있도록 설정*/
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                /* /admin으로 시작하는 경로는 해당 계정이 ADMIN Role일 경우에만 접근 가능하도록 설정*/
                .anyRequest().authenticated(); /* 위의 설정해준 경로를 제외한 나머지
                경로들은 모두 인증을 요구하도록 설정 */

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
        /* 인증되지 않은 사용자가 리소스에 접근하였을 때 수행되는 핸들러를 등록 */
    }

    @Bean //직접 제어가 불가능한 외부 라이브러리등을 Bean으로 만들려할 때 사용된다.
    public PasswordEncoder passwordEncoder(){ 
        // 비밀번호를 그대로 저장했을 경우, 그대로 노출되기 때문에
        // BCryptPasswordEncoder의 해시 함수를 이용해 비밀번호를 암호화하여 저장
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**","/js/**","/img/**");
        /* static 디렉터리의 하위 파일은 인증을 무시하도록 설정 */
    }
}
