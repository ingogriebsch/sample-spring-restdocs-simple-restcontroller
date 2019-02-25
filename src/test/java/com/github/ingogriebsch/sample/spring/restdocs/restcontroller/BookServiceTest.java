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

import static java.util.UUID.randomUUID;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class BookServiceTest {

    private static final Set<BookInsert> bookInserts =
        newHashSet(new BookInsert("0345391802", "The Hitchhiker's Guide to the Galaxy"),
            new BookInsert("9781451673319", "Fahrenheit 451"), new BookInsert("0062225677", "The Color of Magic"));

    private BookService bookService;

    @Before
    public void before() {
        bookService = new BookService();
    }

    @Test
    public void findAll_should_return_available_books() throws Exception {
        Set<Book> books = bookInserts.stream().map(p -> bookService.insert(p).get()).collect(Collectors.toSet());
        assertThat(bookService.findAll()).isNotNull().containsAll(books);
    }

    @Test
    public void findOne_should_return_matching_book_if_available() throws Exception {
        Set<Book> books = bookInserts.stream().map(p -> bookService.insert(p).get()).collect(Collectors.toSet());

        Book book = books.iterator().next();
        Optional<Book> optional = bookService.findOne(book.getIsbn());
        assertThat(optional).isNotNull();
        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get()).isEqualTo(book);
    }

    @Test
    public void findOne_should_return_empty_optional_if_not_available() throws Exception {
        bookInserts.stream().forEach(p -> bookService.insert(p));

        Optional<Book> optional = bookService.findOne(randomUUID().toString());
        assertThat(optional).isNotNull();
        assertThat(optional.isPresent()).isFalse();
    }

    @Test(expected = NullPointerException.class)
    public void findOne_should_throw_exception_if_called_with_null() throws Exception {
        bookService.findOne(null);
    }

    @Test(expected = NullPointerException.class)
    public void delete_should_throw_exception_if_called_with_null() throws Exception {
        bookService.delete(null);
    }

    @Test
    public void delete_should_return_true_if_book_is_known() throws Exception {
        Set<Book> books = bookInserts.stream().map(p -> bookService.insert(p).get()).collect(Collectors.toSet());

        Book book = books.iterator().next();
        assertThat(bookService.delete(book.getIsbn())).isTrue();

        Optional<Book> optional = bookService.findOne(book.getIsbn());
        assertThat(optional).isNotNull();
        assertThat(optional.isPresent()).isFalse();
    }

    @Test
    public void delete_should_return_false_if_book_is_not_known() throws Exception {
        bookInserts.stream().forEach(p -> bookService.insert(p));
        assertThat(bookService.delete(randomUUID().toString())).isFalse();
    }

    @Test
    public void insert_should_return_true_if_book_is_not_known_before() throws Exception {
        bookInserts.stream().forEach(p -> bookService.insert(p));

        Optional<Book> book = bookService.insert(new BookInsert("9781501144189", "Christine"));
        assertThat(book).isNotNull();
        assertThat(book.isPresent()).isTrue();

        Set<Book> all = bookService.findAll();
        assertThat(all.size()).isEqualTo(bookInserts.size() + 1);
        assertThat(all).contains(book.get());
    }

    @Test
    public void insert_should_return_false_if_book_is_already_known() throws Exception {
        bookInserts.stream().forEach(p -> bookService.insert(p));

        Optional<Book> book = bookService.insert(bookInserts.iterator().next());
        assertThat(book).isNotNull();
        assertThat(book.isPresent()).isFalse();

        assertThat(bookService.findAll().size()).isEqualTo(bookInserts.size());
    }

    @Test(expected = NullPointerException.class)
    public void insert_should_throw_exception_if_called_with_null() throws Exception {
        bookService.insert(null);
    }

}
