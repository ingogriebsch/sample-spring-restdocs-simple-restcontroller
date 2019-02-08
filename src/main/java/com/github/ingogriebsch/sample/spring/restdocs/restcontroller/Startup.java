/*
 * Copyright 2019 Ingo Griebsch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.ingogriebsch.sample.spring.restdocs.restcontroller;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class Startup implements CommandLineRunner {

    @NonNull
    private final BookService bookService;

    @Override
    public void run(String... args) throws Exception {
        newHashSet(new BookInsert("0345391802", "The Hitchhiker's Guide to the Galaxy"),
            new BookInsert("9781451673319", "Fahrenheit 451"), new BookInsert("0062225677", "The Color of Magic")).stream()
                .forEach(p -> insert(p));
    }

    private void insert(BookInsert bookInsert) {
        Optional<Book> book = bookService.insert(bookInsert);
        if (book.isPresent()) {
            log.info("Inserting book '{}'...", book.get());
        }
    }

}
