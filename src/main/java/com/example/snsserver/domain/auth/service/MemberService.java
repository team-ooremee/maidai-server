package com.example.snsserver.domain.auth.service;

import com.example.snsserver.common.service.BaseImageService;
import com.example.snsserver.domain.auth.repository.MemberRepository;
import com.example.snsserver.domain.auth.entity.Member;
import com.example.snsserver.dto.auth.response.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService extends BaseImageService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberResponseDto getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("로그인 유저 정보가 없습니다."));
        return new MemberResponseDto(member);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto getMemberInfo(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));
        return new MemberResponseDto(member);
    }

    @Transactional(readOnly = true)
    public Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));
    }

    @Transactional
    public MemberResponseDto updateProfileImage(MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("로그인 유저 정보가 없습니다."));

        deleteImage(member.getProfileImagePath());

        String profileImagePath = uploadImage(file, "profile");
        member.updateProfileImage(profileImagePath);

        memberRepository.save(member);
        return new MemberResponseDto(member);
    }
}