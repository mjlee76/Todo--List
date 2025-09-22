package com.hello.ToDoList.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@ToString(exclude = "password")
public class MemberDto {

    @NotBlank(message = "아이디는 필수입니다.")
    private String id;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;
}
