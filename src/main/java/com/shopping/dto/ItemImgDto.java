package com.shopping.dto;

import com.shopping.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter @Setter
//상품 저장 후 상품 이미지에 대한 데이터를 전달할 DTO 클래스 생성
public class ItemImgDto {

    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;

    private static ModelMapper modelMapper=new ModelMapper();
    //멤버 변수로 ModelMapper 객체 추가

    public static ItemImgDto of(ItemImg itemImg){
        return modelMapper.map(itemImg,ItemImgDto.class);
    }

}
