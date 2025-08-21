package com.dongnering.interest.domain.repository;


import com.dongnering.interest.domain.Interest;
import com.dongnering.interest.domain.InterestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    Boolean existsByInterestType(InterestType interestType);
    Optional<Interest> findByInterestType(InterestType interestType);
}
