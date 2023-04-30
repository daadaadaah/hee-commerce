package com.hcommerce.heecommerce.member;

import com.hcommerce.heecommerce.common.BadRequestException;
import com.hcommerce.heecommerce.common.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class MemberController {
    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }

        if (id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
        }

        if (id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello " + id);
    }

    @GetMapping("/api/response-status-ex1")
    public String responseStatusEx1() {
        throw new BadRequestException();
    }

    @GetMapping("/api/response-status-ex2/{case_id}")
    public String responseStatusEx2(@PathVariable("case_id") String case_id) {
        if(case_id.equals("1")) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "찾을 수 없는 요청입니다.", new IllegalArgumentException());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청 입니다.", new IllegalArgumentException());
        }
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
