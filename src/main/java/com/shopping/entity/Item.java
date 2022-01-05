package com.shopping.entity;

import com.shopping.constant.ItemSellStatus;
import com.shopping.dto.ItemFormDto;
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

}
