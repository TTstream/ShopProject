package com.shopping.service;

import com.shopping.dto.CartItemDto;
import com.shopping.entity.Cart;
import com.shopping.entity.CartItem;
import com.shopping.entity.Item;
import com.shopping.entity.Member;
import com.shopping.repository.CartItemRepository;
import com.shopping.repository.CartRepository;
import com.shopping.repository.ItemRepository;
import com.shopping.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public Long addCart(CartItemDto cartItemDto,String email){
        Item item=itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new); //장바구니에 담을 상품 엔티티 조회
        Member member=memberRepository.findByEmail(email); //현재 로그인한 회원 엔티티 조회

        Cart cart=cartRepository.findByMemberId(member.getId()); //현재 로그인한 회원의 장바구니 엔티티 조회
        if(cart==null){//상품을 처음으로 장바구니에 담을 경우 해당 회원의 장바구니 엔티티 생성
            cart=Cart.createCart(member);
            cartRepository.save(cart);
        }

        //현재 상품이 장바구니에 이미 들어가 있는지 조회
        CartItem savedCartItem=cartItemRepository.findByCartIdAndItemId(cart.getId(),item.getId());
        if(savedCartItem!=null){
            savedCartItem.addCount(cartItemDto.getCount()); //장바구니에 이미 있던 상품일 경우 기존 수량에 현재 장바구니에 담을 수량 만큼을 더해준다.
            return savedCartItem.getId();
        }else{
            CartItem cartItem=
                    CartItem.createCartItem(cart,item, cartItemDto.getCount()); //장바구니 엔티티,상품 엔티티, 장바구니에 담을 수량을 이용
            cartItemRepository.save(cartItem); //장바구니에 들어갈 상품 저장
            return cartItem.getId();
        }

    }

}
