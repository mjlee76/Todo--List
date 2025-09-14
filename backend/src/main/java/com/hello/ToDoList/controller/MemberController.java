package com.hello.ToDoList.controller;

import com.hello.ToDoList.dto.MemberDto;
import com.hello.ToDoList.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members/login")
    public String login() {
        return "members/memberLogin";
    }

    //회원가입 폼
    @GetMapping("/members/signup")
    public String signupForm(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        return "members/createMemberForm";
    }

    //회원가입 처리
    @PostMapping("/members/createMemberForm")
    public String create(@Valid @ModelAttribute("memberDto") MemberDto memberDto,
                         //@Valid: DTO에 선언된 검증 애노테이션 실행
                         //@ModelAttribute("memberDto"): HTML 폼에서 전송된 데이터를 DTO 객체에 바인딩
                         BindingResult bindingResult, //DTO 유효성 검사에서 생긴 오류를 담는 객체
                         Model model) {

        // 1) 폼 검증 실패 시 폼으로 다시
        if (bindingResult.hasErrors()) {
            return "members/createMemberForm";
        }
        try {
            // 2) 서비스에서: id 중복검사 + 비번 암호화 + 저장
            memberService.join(memberDto);
        } catch (IllegalStateException e) {
            // ex) 이미 존재하는 ID 입니다.
            bindingResult.addError(new FieldError(
                    "memberDto", "id", e.getMessage()
            ));
            return "members/createMemberForm";
        }

        return "redirect:/";
    }

    @GetMapping("/members/login/success")
    public String loginSuccess() {
        return "todo/todolist";
    }
}
