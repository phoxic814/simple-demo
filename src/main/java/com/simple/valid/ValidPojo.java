package com.simple.valid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ValidPojo {

    @NotNull
    private Integer i;
    @NotBlank
    private String str;
}
