package com.shopping.service;

import com.shopping.dto.CartDetailDto;
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
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
    
    //현재 로그인한 회원의 정보를 이용하여 장바구니에 들어있는 상품을 조회하는 로직
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){

        List<CartDetailDto> cartDetailDtoList=new ArrayList<>();

        Member member=memberRepository.findByEmail(email);
        Cart cart=cartRepository.findByMemberId(member.getId()); //현재 로그인한 회원의 장바구니 엔티티 조회
        if(cart==null){ //장바구니에 상품을 한 번도 안 담았을 경우 빈 리스트 반환
            return cartDetailDtoList;
        }

        cartDetailDtoList=cartItemRepository.findCartDetailDtoList(cart.getId()); //장바구니에 담겨있는 상품 정보 조회

        return cartDetailDtoList;

    }
    
    /*
    자바스크립트 코드에서 업데이트할 장바구니 상품 번호는 조작이 가능하므로
    현재 로그인한 회원과 해당 장바구니 상품을 저장한 회원이 같은지 검사하는 로직
     */
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId,String email){
        Member cruMember=memberRepository.findByEmail(email); //현재 로그인한 회원 조회

        CartItem cartItem=cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        Member savedMemnber=cartItem.getCart().getMember(); //장바구니 상품을 저장한 회원 조회

        if(!StringUtils.equals(cruMember,savedMemnber)){
            //현재 로그인한 회원과 장바구니 상품을 저장한 회원이 다를 경우 false, 같으면 true 출력
            return false;
        }

        return true;
    }

    // 장바구니 상품의 수량을 업데이트 하는 로직
    public void updateCartItemCount(Long cartItemId,int count){
        CartItem cartItem=cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

}
