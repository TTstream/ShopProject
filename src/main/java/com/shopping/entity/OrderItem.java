package com.shopping.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class OrderItem extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //하나의 상품은 여러 주문 상품으로 들어갈 수 있으므로 다대일 단방향 매핑
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY) //한 번의 주문에 여러 개의 상품을 주문할 수 있으므로 다대일 단방향 매핑
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문가격

    private int count; //수량

    //주문할 상품과 주문 수량을 통해 OrderItem 객체를 만드는 메소드 작성
    public static OrderItem createOrderItem(Item item,int count){
        OrderItem orderItem=new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice()); //현재 시간 기준으로 상품 가격을 주문 가격으로 세팅

        item.removeStock(count); //주문 수량만큼 상품의 재고 수량을 감소
        return orderItem;
    }

    public int getTotalPrice(){
        return orderPrice*count;
    }

}
