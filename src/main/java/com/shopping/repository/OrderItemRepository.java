package com.shopping.repository;

import com.shopping.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

//주문 데이터를 DB에 저장하고, 저장한 주문 상품 데이터 조회
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

}
