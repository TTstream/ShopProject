package com.shopping.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shopping.constant.ItemSellStatus;
import com.shopping.dto.ItemSearchDto;
import com.shopping.dto.MainItemDto;
import com.shopping.dto.QMainItemDto;
import com.shopping.entity.Item;
import com.shopping.entity.QItem;
import com.shopping.entity.QItemImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDataType){
        LocalDateTime dateTime=LocalDateTime.now();

        if(StringUtils.equals("all",searchDataType)||searchDataType==null){
            return null;
        }else if(StringUtils.equals("1d",searchDataType)){
            dateTime=dateTime.minusDays(1);
        }else if(StringUtils.equals("1w",searchDataType)){
            dateTime=dateTime.minusWeeks(1);
        }else if(StringUtils.equals("1m",searchDataType)){
            dateTime=dateTime.minusMonths(1);
        }else if(StringUtils.equals("6m",searchDataType)){
            dateTime=dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);

    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if(StringUtils.equals("itemNm",searchBy)){
            return QItem.item.itemNm.like("%"+searchQuery+"%");
        } else if (StringUtils.equals("createBy", searchBy)) {
            return QItem.item.createBy.like("%"+searchQuery+"%");
        }
        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QueryResults<Item> results=queryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Item> content=results.getResults();
        long total=results.getTotal();
        return new PageImpl<>(content,pageable,total);
    }
    
    //검색어가 null이 아니면 검색어가 포홤되는 상품을 조회하는 조건 반환
    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%"+searchQuery+"%");
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QItem item= QItem.item;
        QItemImg itemImg=QItemImg.itemImg;

        QueryResults<MainItemDto> results= queryFactory
                .select(
                    new QMainItemDto( 
                            /* 
                            QMainItemDto의 생성자에 반환할 값 넣어주기
                            @QueryProjection을 사용하면 DTO로 바로 조회가 가능
                            엔티티 조회 후 DTO로 변환하는 과정을 줄일 수 있다.
                             */
                            item.id,
                            item.itemNm,
                            item.itemDetail,
                            itemImg.imgUrl,
                            item.price)
                )
                .from(itemImg)
                .join(itemImg.item(),item) //itemImg와 item을 내부 조인
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content=results.getResults();
        long total=results.getTotal();
        return new PageImpl<>(content,pageable,total);

    }

}
