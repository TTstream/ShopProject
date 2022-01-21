package com.shopping.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "cart_item")
@Data
public class CartItem extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int count;
    
    //장바구니에 담을 상품 엔티티를 생성하는 메소드와 장바구니에 담을 수량을 증가시켜 주는 메소드 추가
    public static CartItem createCartItem(Cart cart,Item item,int count){
        CartItem cartItem=new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    public void addCount(int count){ //장바구니 기존에 담겨있는 상품, 해당 상품을 추가로 장바구니에 담을 때 기존 수량에 현재수량 더해주는 메소드  
        this.count+=count;
    }

    //장바구니에서 상품의 수량을 변경할 경우 실시간으로 해당 회원의 장바구니 상품의 수량도 변경
    public void updateCount(int count){
        this.count=count;
    }
    
}
