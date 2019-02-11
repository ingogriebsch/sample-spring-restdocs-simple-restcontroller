package com.github.ingogriebsch.sample.spring.restdocs.restcontroller;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookInsert {

    @Pattern(regexp = "[0-9]{10}")
    private String isbn;
    @NotBlank
    private String title;
}
