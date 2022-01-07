package com.shopping.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shopping.constant.ItemSellStatus;
import com.shopping.dto.ItemSearchDto;
import com.shopping.entity.Item;
import com.shopping.entity.QItem;
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
}
