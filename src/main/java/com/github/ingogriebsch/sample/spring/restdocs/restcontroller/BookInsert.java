package com.github.ingogriebsch.sample.spring.restdocs.restcontroller;

import org.hibernate.validator.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookInsert {

    @NotBlank
    private String isbn;
    @NotBlank
    private String title;
}
