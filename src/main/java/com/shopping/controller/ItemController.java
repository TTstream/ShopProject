package com.shopping.controller;

import com.shopping.dto.ItemFormDto;
import com.shopping.dto.ItemSearchDto;
import com.shopping.entity.Item;
import com.shopping.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto",new ItemFormDto());
        return "/item/itemForm";
    }

    private final ItemService itemService;

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model
    , @RequestParam("itemImgFile")List<MultipartFile> itemImgFileList){

        if(bindingResult.hasErrors()){
            return "item/itemForm"; //상품 필수 값이 없다면 다시 상품 등록 페이지로 이동
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId()==null){
            model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto,itemImgFileList);
        }catch(Exception e){
            model.addAttribute("errorMessage","상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        
        return "redirect:/"; //상품이 정상적으로 등록되었다면 메인화면으로 이동
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId,Model model){

        try {
            ItemFormDto itemFormDto=itemService.getItemDtl(itemId); // 조회한 상품 데이터를 모델에 담아 뷰로 전달
            model.addAttribute("itemFormDto",itemFormDto);
        }catch (EntityNotFoundException e){
            model.addAttribute("errorMessage","존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto",new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model
            , @RequestParam("itemImgFile")List<MultipartFile> itemImgFileList){

        if(bindingResult.hasErrors()){
            return "item/itemForm"; //상품 필수 값이 없다면 다시 상품 등록 페이지로 이동
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId()==null){
            model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto,itemImgFileList); //상품 수정 로직 호출
        }catch(Exception e){
            model.addAttribute("errorMessage","상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/"; //상품이 정상적으로 등록되었다면 메인화면으로 이동
    }

    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page")Optional<Integer> page,Model model){
        Pageable pageable= PageRequest.of(page.isPresent() ? page.get() : 0 , 3);
        Page<Item> items=
                itemService.getAdminItemPage(itemSearchDto,pageable);
        model.addAttribute("items",items);
        model.addAttribute("itemSearchDto",itemSearchDto); //페이지 전환 시 기존 검색 조건 유지
        model.addAttribute("maxPage",5); //페이지 번호 최대 개수
        return "item/itemMng";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto=itemService.getItemDtl(itemId);
        model.addAttribute("item",itemFormDto);
        return "item/itemDtl";
    }
}
