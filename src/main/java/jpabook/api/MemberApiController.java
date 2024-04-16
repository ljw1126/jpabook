package jpabook.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.model.Member;
import jpabook.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
public class MemberApiController {

    private final MemberService memberService;

    public MemberApiController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/api/v1/members")
    public List<Member> memberV1() {
        return memberService.findAll();
    }

    @GetMapping("/api/v2/members")
    public Result<List<MemberDto>> memberV2() {
        List<MemberDto> memberDtos = memberService.findAll()
                .stream()
                .map(Member::getName)
                .map(MemberDto::new)
                .collect(toList());
        return new Result<>(memberDtos.size(), memberDtos);
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

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member member = memberService.findById(id);
        return new UpdateMemberResponse(member);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int size;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;

        public UpdateMemberResponse(Member member) {
            this(member.getId(), member.getName());
        }
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
