package com.shopping.service;

import com.shopping.dto.OrderDto;
import com.shopping.entity.Item;
import com.shopping.entity.Member;
import com.shopping.entity.Order;
import com.shopping.entity.OrderItem;
import com.shopping.repository.ItemRepository;
import com.shopping.repository.MemberRepository;
import com.shopping.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    public Long order(OrderDto orderDto,String email){

        Item item=itemRepository.findById(orderDto.getItemId()) //주문할 상품 조회
                .orElseThrow(EntityNotFoundException::new);
        Member member=memberRepository.findByEmail(email); //현재 로그인한 회원의 이메일 정보를 이용해서 회원 정보 조회

        List<OrderItem> orderItemList=new ArrayList<>();
        OrderItem orderItem=OrderItem.createOrderItem(item, orderDto.getCount()); //주문할 상품 엔티티와 주문 수량을 이용해 주문 상품 엔티티 생성
        orderItemList.add(orderItem);

        Order order=Order.createOrder(member,orderItemList); //회원 정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티를 생성
        orderRepository.save(order); //생성한 주문 엔티티 저장

        return order.getId();

    }

}
