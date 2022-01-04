package com.shopping.service;

import com.shopping.entity.ItemImg;
import com.shopping.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}") //application.properties파일에 등록한 itemImgLocation 값을 가져와 변수에 저장
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile)
            throws Exception{
        String oriImgName=itemImgFile.getOriginalFilename(); //업로드했던 상품 이미지 파일의 원래 이름
        String imgName=""; //실제 로컬에 저장된 상품 이미지 파일의 이름
        String imgUrl=""; //업로드 결과 로컬에 저장된 상품 이미지 파일을 불러오는 경로

        //파일업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName=fileService.uploadFile(itemImgLocation,oriImgName,itemImgFile.getBytes());
            imgUrl="/images/item/"+imgName;
        }

        //상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName,imgName,imgUrl);
        itemImgRepository.save(itemImg);
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{

        if(!itemImgFile.isEmpty()){
            ItemImg savedItemImg=itemImgRepository.findById(itemImgId) //기존에 저장했던 상품 이미지 엔티티를 조회
                    .orElseThrow(EntityNotFoundException::new);

            //기존 이미지 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())){
                fileService.deleteFile(itemImgLocation+"/"+savedItemImg.getImgName());
            }

            String oriImgName=itemImgFile.getOriginalFilename();
            String imgName=fileService.uploadFile(itemImgLocation,oriImgName,itemImgFile.getBytes());
            String imgUrl="/images/item/"+imgName;
            savedItemImg.updateItemImg(oriImgName,imgName,imgUrl);
        }

    }

}
