package com.shopping.entity;

import com.shopping.constant.OrderStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
    orphanRemoval = true, fetch = FetchType.LAZY)
    // 부모 엔티티의 영속성 상태 변화를 자식 엔티티에 모두 전이하는 CascadeType.ALL 설정 
    private List<OrderItem> orderItems=new ArrayList<>();

    /*생성한 주문 상품 객체를 이용해 주문 객체를 만드는 메소드들*/

    public void addOrderItem(OrderItem orderItem){ //orderItems에 주문 상품 정보들을 담아준다.
        orderItems.add(orderItem);
        orderItem.setOrder(this); //Order 엔티티와 OrderItem 엔티티가 양방향 참고 관계이므로, orderItem객체에도 order객체 세팅
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order=new Order();
        order.setMember(member); //상품을 주문한 회원의 정보 세팅
        for(OrderItem orderItem:orderItemList){ //상품 페이지에서는 1개의 상품을 주문하지만 한 번에 여러 개의 상품 주문할 수가 있다.
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER); //주문 상태를 "ORDER"로 세팅
        order.setOrderDate(LocalDateTime.now()); //현재 시간을 주문 시간으로 세팅
        return order;
    }

    public int getTotalPrice(){ //총 주문 금액을 구하는 메소드
        int totalPrice=0;
        for(OrderItem orderItem:orderItems){
            totalPrice=orderItem.getTotalPrice();
        }
        return totalPrice;
    }
    
    //Item 클래스에 주문 취소 시 주문 수량을 상품의 재고에 더해주는 로직과 주문 상태를 취소 상태로 바꿔주는 메소드
    public void cancelOrder(){
        this.orderStatus=OrderStatus.CANCEL;

        for(OrderItem orderItem:orderItems){
            orderItem.cancel();
        }
    }

}
