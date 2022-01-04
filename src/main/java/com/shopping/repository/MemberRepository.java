package com.shopping.repository;

import com.shopping.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
    
    Member findByEmail(String email); //회원 가입 시 중복된 회원이 있는지 검사히기 위해

}
