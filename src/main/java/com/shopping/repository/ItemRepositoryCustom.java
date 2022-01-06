package com.shopping.repository;

import com.shopping.dto.ItemSearchDto;
import com.shopping.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

//Querydsl을 Spring Data Jpa와 함께 사용하기 위해 사용자 정의 리포지토리 정의
/*
1. 사용자 정의 인터페이스 작성
2. 사용자 정의 인터페이스 구현
3. Spring Data Jpa 리포지토리에서 사용자 정의 인터페이스 상속
*/
public interface ItemRepositoryCustom {

    //상품 조회 조건을 담고 있는 itemSearchDto 객체와 페이징 정보를 담고 있는 pageable 객체를 파라미터로 받기
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

}
