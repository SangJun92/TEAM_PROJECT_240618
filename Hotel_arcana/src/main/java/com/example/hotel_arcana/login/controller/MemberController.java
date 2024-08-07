package com.example.hotel_arcana.login.controller;

import com.example.hotel_arcana.login.dto.MemberDTO;
import com.example.hotel_arcana.login.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.security.Principal;

@Controller
@Log4j2
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/")
    public String index(Principal principal, Model model) {
        log.info(principal.getName());
        MemberDTO memberDTO = memberService.findMemberById(principal.getName());
        model.addAttribute("memberDTO", memberDTO);
        log.info(memberDTO);
        return "MyPage";
    }

    //회원만 쓸 수 있어서 로그인이 필요한 페이지에 넣기
    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("member");
        return "/join";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute MemberDTO memberDTO) {
        log.info(memberDTO);
        memberService.insertMember(memberDTO);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "/login";
    }


    //    @PreAuthorize("isAuthenticated()")
    @GetMapping("/login/memberRead/{USER_ID}")
    public String read(@PathVariable String USER_ID, Model model) {
        MemberDTO memberDTO = memberService.memberRead(USER_ID);
        model.addAttribute("memberDTO", memberDTO);
        return "login/memberRead";
    }


    //    @PreAuthorize("isAuthenticated()")
    @GetMapping("/login/memberModify/{USER_ID}")
    public String modify(@PathVariable String USER_ID, Model model) {
        MemberDTO memberDTO = memberService.memberRead(USER_ID);
        model.addAttribute("memberDTO", memberDTO);
        return "login/memberModify";
    }


    //    @PreAuthorize("isAuthenticated()")
    @PostMapping("/login/memberModify")
    public String update(@ModelAttribute MemberDTO memberDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "/login/memberModify";
        }
        try {
            memberService.updateMember(memberDTO);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "/login/memberModify";
        }
        model.addAttribute("memberDTO", memberDTO);

        return "redirect:/login/memberRead/" + memberDTO.getUSER_ID();

//        memberService.updateMember(memberDTO);
//        return "redirect:login/memberRead/" + memberDTO.getUSER_ID(); // 수정 후 상세 조회 페이지로 리다이렉트
    }


//    @GetMapping("/login/memberRemove/{USER_ID}")
//    public String MemberRemove(@PathVariable("USER_ID") String USER_ID, Model model) {
//        memberService.deleteMember(USER_ID);
//        return "redirect:/register"; // 삭제 후 리다이렉트할 페이지 설정
//    }

    @GetMapping("/login/memberRemove/{USER_ID}")
    public String memberRemove(@PathVariable("USER_ID") String USER_ID, Model model) {
        MemberDTO memberDTO = memberService.memberRead(USER_ID);
        model.addAttribute("memberDTO", memberDTO);
        return "login/memberRemove";
    }

    @PostMapping("/login/memberRemove")
    public String deleteMember(@ModelAttribute("memberDTO") MemberDTO memberDTO, @RequestParam("USER_PW") String USER_PW) {
        if (checkPassword(memberDTO, USER_PW)) {
            memberService.deleteMember(memberDTO.getUSER_ID());
            return "redirect:/register";
        } else {
            // 비밀번호가 틀릴 경우 처리
            return "error-page";
        }
    }

    private boolean checkPassword(MemberDTO memberDTO, String USER_PW) {
        // 비밀번호 확인 로직
        return memberDTO.getUSER_PW().equals(USER_PW);
    }

    @GetMapping("/testfile")
    public String test (){
        return "/testfile";
    }

    @GetMapping("/manager/manage")
    public String manage(Model model) {
        int totalMembersCount = memberService.getTotalMembersCount();
        model.addAttribute("totalMembersCount", totalMembersCount);

        List<MemberDTO> members = memberService.getAllMembers();
        model.addAttribute("members", members);

        return "manager/manage"; // manage.html을 불러오는 경로
    }

    @GetMapping("/manager/manageUser")
    public String manageUser(Model model) {
        int totalMembersCount = memberService.getTotalMembersCount();
        model.addAttribute("totalMembersCount", totalMembersCount);

        List<MemberDTO> members = memberService.getAllMembers();
        model.addAttribute("members", members);

        return "manager/manageUser"; // manageUser.html을 불러오는 경로
    }

    @DeleteMapping("/manager/delete/{userId}")
    public ResponseEntity<String> deleteMember(@PathVariable String userId) {
        log.info("---------------userId : " + userId);
        try {
            memberService.deleteMember(userId);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 삭제 실패: " + e.getMessage());
        }
    }

}

