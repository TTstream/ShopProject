package com.shopping.repository;

import com.shopping.dto.CartDetailDto;
import com.shopping.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    //장바구니에 들어갈 상품을 저장하거나 조회
    CartItem findByCartIdAndItemId(Long cartId,Long itemId);

    @Query("select new com.shopping.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) "+
            "from CartItem ci, ItemImg im "+ //생성자의 파라미터 순서는 DTO 클래스에 명시한 순서로 넣기
            "join ci.item i "+
            "where ci.cart.id = :cartId "+
            "and im.item.id = ci.item.id "+
            "and im.repimgYn = 'Y' "+
            "order by ci.regTime desc")
    List<CartDetailDto> findCartDetailDtoList(Long cartId);

}
