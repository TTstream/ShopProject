package com.shopping.service;

import com.shopping.dto.ItemFormDto;
import com.shopping.dto.ItemImgDto;
import com.shopping.dto.ItemSearchDto;
import com.shopping.dto.MainItemDto;
import com.shopping.entity.Item;
import com.shopping.entity.ItemImg;
import com.shopping.repository.ItemImgRepository;
import com.shopping.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
//상품을 등록하는 클래스
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto,
                         List<MultipartFile> itemImgFileList) throws Exception{

        //상품 등록
        Item item=itemFormDto.createItem();//상품 등록 폼으로부터 입력 받은 데이터를 이용하여 item 객체 생성 
        itemRepository.save(item); //상품 데이터 저장

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            ItemImg itemImg=new ItemImg();
            itemImg.setItem(item);
            if(i==0){ //첫 번째 이미지일 경우 대표 상품 이미지 여부 값을 "Y"로 세팅 나머지는 "N"
                itemImg.setRepimgYn("Y");
            }
            else{
                itemImg.setRepimgYn("N");
            }
            itemImgService.saveItemImg(itemImg,itemImgFileList.get(i));
        }

        return item.getId();
    }

    @Transactional(readOnly = true) // 상품 데이터를 읽어오는 트랜잭션을 읽기 전용으로 설정. 더티체킹을 수행하지 않아 성능향상
    public ItemFormDto getItemDtl(Long itemId){

        List<ItemImg> itemImgList=itemImgRepository.findByItemIdOrderByIdAsc(itemId); //상품이미지 조회
        
        List<ItemImgDto> itemImgDtoList=new ArrayList<>();

        for(ItemImg itemImg:itemImgList){ //ItemImg 엔티티를 -> ItmeImgDto객체로 만들어서 추가
            ItemImgDto itemImgDto=ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }
        
        Item item=itemRepository.findById(itemId) //상품의 아이디를 통해 상품 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);

        ItemFormDto itemFormDto=ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;

    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        
        //상품 수정
        Item item=itemRepository.findById(itemFormDto.getId()) //상품 등록 화면으로부터 전달 받은 아이디를 이용해 상품 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto); //변경 감지 기능 사용

        List<Long> itemImgIds=itemFormDto.getItemImgIds(); //상품 이미지 아이디 리스트 조회
        
        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),itemImgFileList.get(i));
        }

        return item.getId();
    }

    @Transactional(readOnly = true) //데이터 수정이 일어나지 않으므로 최적화를 위해 사용
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        //상품 조회 조건과 페이지 정보를 파라미터로 받아서 상품 데이터 조회
        return itemRepository.getAdminItemPage(itemSearchDto,pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto,  Pageable pageable){
        //메인 페이지에 상품 데이터 보여주기
        return itemRepository.getMainItemPage(itemSearchDto,pageable);
    }
    
}
