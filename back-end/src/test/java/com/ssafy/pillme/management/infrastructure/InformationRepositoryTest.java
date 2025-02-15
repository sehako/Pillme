package com.ssafy.pillme.management.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import com.ssafy.pillme.management.domain.Information;
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

}