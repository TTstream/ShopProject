package com.shopping.dto;

import com.shopping.constant.OrderStatus;
import com.shopping.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//주문 정보를 담을 클래스 생성
@Getter @Setter
public class OrderHistDto {

    public OrderHistDto(Order order){
        this.orderId=order.getId();
        this.orderDate=order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus=order.getOrderStatus();
    }

    private Long orderId;

    private String orderDate;

    private OrderStatus orderStatus;

    //주문 상품 리스트
    private List<OrderItemDto> orderItemDtoList=new ArrayList<>();

    public void addOrderItemDto(OrderItemDto orderItemDto){ //orderItemDto 객체를 주문 상품 리스트에 추가하는 메소드
        orderItemDtoList.add(orderItemDto);
    }
    
}
