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

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final Set<Book> books = new HashSet<>();

    public Set<Book> findAll() {
        return books;
    }

    public Optional<Book> findOne(@NonNull String isbn) {
        return books.stream().filter(p -> p.getIsbn().equals(isbn)).limit(1).findAny();
    }

    public Optional<Book> insert(@NonNull BookInsert bookInsert) {
        String isbn = bookInsert.getIsbn();
        for (Book p : books) {
            if (p.getIsbn().equals(isbn)) {
                return empty();
            }
        }

        Book book = book(bookInsert);
        books.add(book);
        return of(book);
    }

    public boolean delete(@NonNull String isbn) {
        return books.removeIf(p -> isbn.equals(p.getIsbn()));
    }

    private static Book book(BookInsert bookInsert) {
        return new Book(bookInsert.getIsbn(), bookInsert.getTitle());
    }

}
