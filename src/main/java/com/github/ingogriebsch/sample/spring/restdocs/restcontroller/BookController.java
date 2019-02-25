/*-
 * #%L
 * Spring Web simple REST controller sample
 * %%
 * Copyright (C) 2018 - 2019 Ingo Griebsch
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.ingogriebsch.sample.spring.restdocs.restcontroller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

import java.util.Optional;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class BookController {

    static final String PATH_FIND_ALL = "/books";
    static final String PATH_FIND_ONE = "/books/{isbn}";
    static final String PATH_INSERT = "/books";
    static final String PATH_DELETE = "/books/{isbn}";

    @NonNull
    private final BookService bookService;

    @GetMapping(path = PATH_FIND_ALL, produces = APPLICATION_JSON_UTF8_VALUE)
    public Set<Book> findAll() {
        return bookService.findAll();
    }

    @GetMapping(path = PATH_FIND_ONE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Book> findOne(@PathVariable String isbn) {
        return bookService.findOne(isbn).map(ResponseEntity::ok).orElse(notFound().build());
    }

    @PostMapping(path = PATH_INSERT, produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Book> insert(@RequestBody @Validated BookInsert bookInsert) {
        Optional<Book> inserted = bookService.insert(bookInsert);
        return inserted.isPresent() ? status(CREATED).body(inserted.get()) : badRequest().build();
    }

    @DeleteMapping(path = PATH_DELETE)
    public ResponseEntity<Void> delete(@PathVariable String isbn) {
        return bookService.delete(isbn) ? ok().build() : notFound().build();
    }
}
