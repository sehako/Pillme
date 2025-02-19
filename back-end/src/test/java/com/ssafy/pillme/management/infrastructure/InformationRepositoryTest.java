package com.ssafy.pillme.management.infrastructure;

import com.ssafy.pillme.management.domain.Information;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InformationRepositoryTest {

    @Autowired
    private InformationRepository informationRepository;

    @Test
    void informationMembersFetchTest() {
        Information information = informationRepository.findByIdMemberFetchJoin(4L)
                .orElseThrow(RuntimeException::new);

        information.getReader();
        information.getWriter();
    }

    @Test
    void informationYearAndMonthFetchTest() {
        List<Information> informationList = informationRepository.findAllByDate(LocalDate.now(), 9L);

        for (Information information : informationList) {
            System.out.println(information);
        }
    }

}