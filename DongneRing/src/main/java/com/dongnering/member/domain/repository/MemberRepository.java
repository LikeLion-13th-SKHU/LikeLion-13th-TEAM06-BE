package com.dongnering.member.domain.repository;

import com.dongnering.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Object> findByRefreshToken(String refreshToken);
}