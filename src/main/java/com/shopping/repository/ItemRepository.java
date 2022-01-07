package com.shopping.repository;

import com.shopping.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long>,
        QuerydslPredicateExecutor<Item> , ItemRepositoryCustom{
    // ItemRepositoryCustom인터페이스 상속해야 Querydsl로 구현한 상품 관리 페이지 목록을 불러오는 getAdminItemPage메소드 사용 가능
    
    List<Item> findByItemNm(String itemNm);

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    List<Item> findByPriceLessThan(Integer price);

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
    //오름차순 => OrderBy + 속성명 + Asc
    //내림차순 => OrderBy + 속성명 + Desc

    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail")String itemDetail);

    @Query(value = "select * from item i where i.item_detail like %:itemDetail% order by i.price desc",nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail")String itemDetail);

}