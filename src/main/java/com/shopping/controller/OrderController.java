package com.shopping.controller;

import com.shopping.dto.OrderDto;
import com.shopping.dto.OrderHistDto;
import com.shopping.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.Optional;

//상품 주문에서 웹 페이지의 새로 고침 없이 서버에 주문을 요청하기 위해 비동기 방식 사용
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult,
                                              Principal principal){
        if(bindingResult.hasErrors()){ //주문 정보를 받는 orderDto 객체에 데이터 바인딩 시 에러가 있는지 검사
            StringBuilder sb=new StringBuilder();
            List<FieldError> fieldErrors=bindingResult.getFieldErrors();
            for(FieldError fieldError:fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST); //에러 정보를 ResponseEntity에 담아 반환
        }

        String email=principal.getName(); // 현재 로그인 유저의 정보를 얻기 위해 사용
        Long orderId;

        try {
            orderId=orderService.order(orderDto,email); //화면으로부터 넘어온 주문 정보와 회원의 이메일 정보를 이용해 주문 로직 호출
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId,HttpStatus.OK); //결과값으로 생성된 주문 번호와 요청이 성공했다는 HTTP 응답 상태 코드 반환
        
    }

    @GetMapping(value = {"/orders","/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model){
        Pageable pageable= PageRequest.of(page.isPresent() ? page.get() : 0,4); //한번에 가지고 올 주문의 개수 4개로 설정

        //현재 로그인한 회원은 이메일과 페이징 객체를 파라미터로 전달하여 화면에 전달한 주문 목록 데이터를 리턴 값으로 받는다.
        Page<OrderHistDto> orderHistDtoList=orderService.getOrderList(principal.getName(),pageable);

        model.addAttribute("orders",orderHistDtoList);
        model.addAttribute("page",pageable.getPageNumber());
        model.addAttribute("maxPage",5);

        return "order/orderHist";

    }

}
