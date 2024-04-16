package jpabook.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.model.Member;
import jpabook.service.MemberService;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberApiController {

    private final MemberService memberService;

    public MemberApiController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Long id = memberService.join(request.toMember());
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;

        public Member toMember() {
            Member member = new Member();
            member.setName(name);
            return member;
        }
    }
    
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
