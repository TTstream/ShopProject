package com.shopping.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "cart")
@Data
public class Cart extends BaseEntity{

    //------------------------------------
    /* 이렇게 매핑을 맺어주면 장바구니 엔티티를 조회하면서
    회원 엔티티의 정보도 동시에 가져올 수 있다. */

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* 즉시로딩 : 엔티티를 조회할 때 해당 엔티티와 매핑된 엔티티도 한 번에
    * 조회하는 것 */
    @OneToOne(fetch = FetchType.LAZY) //(fetch=FetchType.EAGER) 생략되어있음
    @JoinColumn(name = "member_id")
    private Member member;

    //------------------------------------
    
    // 회원 한 명당 1개의 장바구니를 가지고 있으므로, 처음 장바구니 담을 때는 해당 회원의 장바구니를 생성
    public static Cart createCart(Member member){
        Cart cart=new Cart();
        cart.setMember(member);
        return cart;
    }

}
