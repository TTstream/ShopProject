package com.shopping.entity;

import com.shopping.constant.ItemSellStatus;
import com.shopping.dto.ItemFormDto;
import com.shopping.exception.OutOfStockException;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "item")
public class Item extends BaseEntity{

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id; //상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Column(nullable = false)
    private int price; //가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    private String itemDetail; //상품 상세설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    //상품을 업데이트 하는 로직 -> 엔티티 클래스에 비즈니스 로직을 추가하면 좀 더 객체지향적인 코딩과 코드 재활용 가능
    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm=itemFormDto.getItemNm();
        this.price=itemFormDto.getPrice();
        this.stockNumber=itemFormDto.getStockNumber();
        this.itemDetail=itemFormDto.getItemDetail();
        this.itemSellStatus=itemFormDto.getItemSellStatus();
    }

    //상품을 주문할 경우 상품의 재고를 감소시키는 로직
    public void removeStock(int stockNumber){
        int restStock=this.stockNumber-stockNumber; //상품의 재고 수량에서 주문 후 남은 재고 수량 구하기
        if(restStock<0){
            //상품의 재고가 주문 수량보다 작을 경우 재고 부족 예외를 발생시킨다.
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: "+this.stockNumber+")");
        }
        this.stockNumber=restStock; //주문 후 남은 재고 수량을 상품의 현재 재고 값으로 할당
    }

}
