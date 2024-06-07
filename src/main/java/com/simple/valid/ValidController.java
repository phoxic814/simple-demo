package com.simple.valid;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class ValidController {

    @GetMapping("valid")
    public void valid(@RequestBody @Valid List<ValidPojo> validPojo) {
        String s = "";
    }
}
