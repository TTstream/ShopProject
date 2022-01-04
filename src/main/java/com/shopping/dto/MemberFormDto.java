package com.shopping.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
//회원 가입 화면으로부터 넘어오는 가입정보를 담을 DTO 생성
public class MemberFormDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name; //이름

    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email; //이메일

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 8,max = 16,message = "비밀번호 8자 이상. 16자 이하로 입력해주세요")
    private String password; //비밀번호

    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String address; //주소

}
