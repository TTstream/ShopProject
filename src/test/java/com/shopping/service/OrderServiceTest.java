package com.shopping.service;

import com.shopping.constant.ItemSellStatus;
import com.shopping.constant.OrderStatus;
import com.shopping.dto.OrderDto;
import com.shopping.entity.Item;
import com.shopping.entity.Member;
import com.shopping.entity.Order;
import com.shopping.entity.OrderItem;
import com.shopping.repository.ItemRepository;
import com.shopping.repository.MemberRepository;
import com.shopping.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    public Item saveItem(){ //주문할 상품 메소드 생성
        Item item=new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    public Member saveMember(){ //회원 정보를 저장하는 메소드 생성
        Member member=new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("주문 테스트")
    public void order(){

        Item item=saveItem();
        Member member=saveMember();
        
        OrderDto orderDto=new OrderDto(); //주문할 상품과 상품 수량을 세팅
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());

        Long orderId= orderService.order(orderDto, member.getEmail()); //주문 로직 호출 결과 생성된 주문 번호를 변수에 저장

        Order order=orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        List<OrderItem> orderItems=order.getOrderItems();

        int totalPrice=orderDto.getCount()*item.getPrice(); //주문한 상품의 총 가격

        assertEquals(totalPrice,order.getTotalPrice()); //주문한 상품의 총 가격과 DB에 저장된 상품의 가격을 비교
        
    }

    @Test
    @DisplayName("주문 취소 테스트")
    public void cancelOrder(){
        //상품과 회원 데이터 생성
        Item item=saveItem();
        Member member=saveMember();

        OrderDto orderDto=new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());
        Long orderId=orderService.order(orderDto,member.getEmail()); //주문 데이터 생성

        Order order=orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new); //생성한 주문 엔티티 조회
        orderService.cancelOrder(orderId); //해당 주문 취소

        assertEquals(OrderStatus.CANCEL,order.getOrderStatus()); //주문의 상태가 취소 상태라면 테스트 통과
        assertEquals(100,item.getStockNumber()); //취소 후 상품의 재고가 처음 재고 개수인 100개와 동일하면 테스트 통과

    }

}