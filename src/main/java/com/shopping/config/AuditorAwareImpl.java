package com.shopping.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

//현재 로그인한 사용자의 정보를 등록자와 수정자로 지정하기 위해
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication=
                SecurityContextHolder.getContext().getAuthentication();
        String userId="";
        if(authentication!=null){
            //현재 로그인 한 사용자의 정보를 조회하여 사용자의 이름을 등록자와 수정자로 지정
            userId=authentication.getName(); 
        }
        return Optional.of(userId);
    }

}
