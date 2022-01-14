package com.shopping.repository;

import com.shopping.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// @Query 어노테이션을 이용하여 주문 이력을 조회하는 쿼리 작성
public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query("select o from Order o "+
            "where o.member.email = :email "+
            "order by o.orderDate desc")
    List<Order> findOrders(@Param("email") String email, Pageable pageable); //현재 로그인한 사용자의 주문 데이터를 조회

    @Query("select count(o) from Order o "+
            "where o.member.email = :email")
    Long countOrder(@Param("email") String email); //현재 로그인한 회원의 주문 개수가 몇 개인지 조회


}
