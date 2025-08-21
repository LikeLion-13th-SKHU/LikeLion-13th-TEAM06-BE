package com.dongnering.configuration;


import com.dongnering.interest.domain.Interest;
import com.dongnering.interest.domain.InterestType;
import com.dongnering.interest.domain.repository.InterestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


//    "정책_정부", "산업_기업", "연구_기술", "규제_제도",
//    "수출_글로벌", "투자_금융", "인사_조직", "사회", "기타"


//policy_Government,
//industry_Company,
//research_Technology,
//regulation_System,
//export_Global,
//investment_Finance,
//hR_Organization,
//society,
//others


@Configuration
public class InterestInitializer {

    @Bean
    @Order(1)
    public CommandLineRunner initInterestData(InterestRepository interestRepository)
    {return args -> {

        Interest policy_Government = Interest.builder()
                .interestType(InterestType.POLICY_GOVERNMENT)
                .build();


        Interest industry_Company = Interest.builder()
                .interestType(InterestType.INDUSTRY_COMPANY)
                .build();


        Interest research_Technology = Interest.builder()
                .interestType(InterestType.RESEARCH_TECHNOLOGY)
                .build();

        Interest regulation_System = Interest.builder()
                .interestType(InterestType.REGULATION_SYSTEM)
                .build();

        Interest export_Global = Interest.builder()
                .interestType(InterestType.EXPORT_GLOBAL)
                .build();

        Interest investment_Finance = Interest.builder()
                .interestType(InterestType.INVESTMENT_FINANCE)
                .build();

        Interest hR_Organization = Interest.builder()
                .interestType(InterestType.HR_ORGANIZATION)
                .build();

        Interest society = Interest.builder()
                .interestType(InterestType.SOCIETY)
                .build();

        Interest others = Interest.builder()
                .interestType(InterestType.OTHERS)
                .build();

        if (interestRepository.count() == 0){

            interestRepository.save(policy_Government);
            interestRepository.save(industry_Company);
            interestRepository.save(research_Technology);
            interestRepository.save(regulation_System);
            interestRepository.save(export_Global);
            interestRepository.save(investment_Finance);
            interestRepository.save(hR_Organization);
            interestRepository.save(society);
            interestRepository.save(others);
        }



    };
    }


}
