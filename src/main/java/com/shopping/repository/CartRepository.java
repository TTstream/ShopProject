package com.shopping.repository;

import com.shopping.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {
    // 현재 로그인한 회원의 Cart 엔티티를 찾기 위해서 메소드 추가
    Cart findByMemberId(Long memberId);

}
