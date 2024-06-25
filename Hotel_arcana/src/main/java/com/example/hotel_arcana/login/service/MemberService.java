package com.example.hotel_arcana.login.service;

import com.example.hotel_arcana.login.dto.MemberDTO;

public interface MemberService {
    static class IdExistException extends Exception {
    }

    MemberDTO findMemberById(String USER_ID);
    void insertMember(MemberDTO memberDTO);
}
