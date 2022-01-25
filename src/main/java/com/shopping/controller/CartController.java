package com.shopping.controller;

import com.shopping.dto.CartDetailDto;
import com.shopping.dto.CartItemDto;
import com.shopping.dto.CartOrderDto;
import com.shopping.service.CartService;
import lombok.RequiredArgsConstructor;
import org.dom4j.rule.Mode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody
    ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                         BindingResult bindingResult, Principal principal){

        if(bindingResult.hasErrors()){ //장바구니에 담을 상품 정보 에러가 있는지 검사
            StringBuilder sb=new StringBuilder();
            List<FieldError> fieldErrors=bindingResult.getFieldErrors();
            for(FieldError fieldError: fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email=principal.getName(); //현재 로그인한 회원의 이메일 정보를 변수에 저장
        Long cartItemId;

        try {
            //화면으로부터 넘어온 장바구니에 담을 상품 정보와 현재 로그인한 회원의 이메일 정보를 이용하여 장바구니에 상품을 담는 로직 호출
            cartItemId=cartService.addCart(cartItemDto,email);
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(cartItemId,HttpStatus.OK);
    }

    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){
        
        //현재 로그인한 사용자의 이메일 정보를 이용해 장바구니에 담겨있는 상품 정보 조회
        List<CartDetailDto> cartDetailDtoList=cartService.getCartList(principal.getName());

        //조회한 장바구니 상품 정보를 뷰에 전달
        model.addAttribute("cartItems",cartDetailDtoList);
        return "cart/cartList";
        
    }
    
    //장바구니 상품의 수량을 업데이트하는 요청을 처리하는 로직
    @PatchMapping(value = "/cartItem/{cartItemId}") //자원의 일부를 업데이트할 때 Patch 사용
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       int count, Principal principal){
        if(count<=0){ // 장바구니에 담겨있는 상품 개수 0개 이하로 업데이트 요청이 올 때 에러 반환
            return new ResponseEntity<String>("최소 1개 이상 담아주세요",HttpStatus.BAD_REQUEST);
        }else if(!cartService.validateCartItem(cartItemId,principal.getName())){ //수정 권한 체크
            return new ResponseEntity<String>("수정 권한이 없습니다.",HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId,count); //장바구니 상품의 개수를 업데이트
        return new ResponseEntity<Long>(cartItemId,HttpStatus.OK);
    }

    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal){

        if(!cartService.validateCartItem(cartItemId, principal.getName())){ //수정 권한 체크
            return new ResponseEntity<String>("수정 권한이 없습니다.",HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId); //해당 장바구니 상품 삭제
        return new ResponseEntity<Long>(cartItemId,HttpStatus.OK);
    }

    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal){
        List<CartOrderDto> cartOrderDtoList=cartOrderDto.getCartOrderDtoList();

        if(cartOrderDtoList==null || cartOrderDtoList.size()==0){ //주문할 상품을 선택하지 않았는지
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }
        
        for(CartOrderDto cartOrder:cartOrderDtoList){ //주문 권한 체크 
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){
                return new ResponseEntity<String>("주문 권한이 없습니다.",HttpStatus.FORBIDDEN);
            }
        }

        //주문 로직 호출 결과 생성된 주문 번호 반환받기
        Long orderId=cartService.orderCartItem(cartOrderDtoList,principal.getName());
        
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);

    }

}
