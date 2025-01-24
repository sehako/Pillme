package com.ssafy.pillme;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {
    @PostMapping("/test")
    public void test(@RequestBody @Valid TestRequest testRequest) {

    }
}
