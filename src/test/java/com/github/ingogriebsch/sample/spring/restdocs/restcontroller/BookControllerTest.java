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

import static com.github.ingogriebsch.sample.spring.restdocs.restcontroller.BookController.PATH_DELETE;
import static com.github.ingogriebsch.sample.spring.restdocs.restcontroller.BookController.PATH_FIND_ALL;
import static com.github.ingogriebsch.sample.spring.restdocs.restcontroller.BookController.PATH_FIND_ONE;
import static com.github.ingogriebsch.sample.spring.restdocs.restcontroller.BookController.PATH_INSERT;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    public void findOne_shoud_return_status_ok_and_book_resource_if_available() throws Exception {
        Book book = new Book("0345391802", "The Hitchhiker's Guide to the Galaxy");
        given(bookService.findOne(book.getIsbn())).willReturn(of(book));

        ResultActions actions = mockMvc.perform(get(PATH_FIND_ONE, book.getIsbn()).accept(APPLICATION_JSON_UTF8));
        actions.andExpect(status().isOk());

        actions.andExpect(content().contentType(APPLICATION_JSON_UTF8));
        actions.andExpect(jsonPath("$.isbn", is(book.getIsbn())));
        actions.andExpect(jsonPath("$.title", is(book.getTitle())));

        verify(bookService, times(1)).findOne(book.getIsbn());
        verifyNoMoreInteractions(bookService);
    }

    @Test
    public void findOne_should_return_status_not_found_if_not_available() throws Exception {
        String isbn = randomNumeric(8);
        given(bookService.findOne(isbn)).willReturn(empty());

        ResultActions actions = mockMvc.perform(get(PATH_FIND_ONE, isbn).accept(APPLICATION_JSON_UTF8));
        actions.andExpect(status().isNotFound());
        actions.andExpect(content().string(EMPTY));

        verify(bookService, times(1)).findOne(isbn);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    public void findAll_should_return_status_ok_and_book_resources_if_available() throws Exception {
        Set<Book> books = newHashSet(new Book("0345391802", "The Hitchhiker's Guide to the Galaxy"),
            new Book("9781451673319", "Fahrenheit 451"), new Book("0062225677", "The Color of Magic"));
        given(bookService.findAll()).willReturn(books);

        ResultActions actions = mockMvc.perform(get(PATH_FIND_ALL).accept(APPLICATION_JSON_UTF8));
        actions.andExpect(status().isOk());
        actions.andExpect(content().contentType(APPLICATION_JSON_UTF8));
        actions.andExpect(jsonPath("$", not(empty())));

        verify(bookService, times(1)).findAll();
        verifyNoMoreInteractions(bookService);
    }

    @Test
    public void findAll_should_return_status_ok_but_no_resources_if_not_available() throws Exception {
        given(bookService.findAll()).willReturn(newHashSet());

        ResultActions actions = mockMvc.perform(get(PATH_FIND_ALL).accept(APPLICATION_JSON_UTF8));
        actions.andExpect(status().isOk());
        actions.andExpect(content().contentType(APPLICATION_JSON_UTF8));
        actions.andExpect(jsonPath("$", Matchers.empty()));

        verify(bookService, times(1)).findAll();
        verifyNoMoreInteractions(bookService);
    }

    @Test
    public void insert_should_return_status_created_if_not_known() throws Exception {
        BookInsert bookInsert = new BookInsert(randomNumeric(10), "Fahrenheit 451");
        Book book = new Book(bookInsert.getIsbn(), bookInsert.getTitle());
        given(bookService.insert(bookInsert)).willReturn(of(book));

        String content = objectMapper.writeValueAsString(bookInsert);
        ResultActions actions = mockMvc.perform(post(PATH_INSERT).contentType(APPLICATION_JSON_UTF8).content(content));
        actions.andExpect(status().isCreated());
        actions.andExpect(jsonPath("$.isbn").value(book.getIsbn()));
        actions.andExpect(jsonPath("$.title").value(book.getTitle()));

        verify(bookService, times(1)).insert(bookInsert);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    public void insert_should_return_status_bad_request_if_already_known() throws Exception {
        BookInsert bookInsert = new BookInsert(randomNumeric(10), "Fahrenheit 451");
        given(bookService.insert(bookInsert)).willReturn(empty());

        String content = objectMapper.writeValueAsString(bookInsert);
        ResultActions actions = mockMvc.perform(post(PATH_INSERT).contentType(APPLICATION_JSON_UTF8).content(content));
        actions.andExpect(status().isBadRequest());
        actions.andExpect(content().string(EMPTY));

        verify(bookService, times(1)).insert(bookInsert);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    public void insert_should_return_status_bad_request_if_book_insert_is_not_valid() throws Exception {
        BookInsert bookInsert = new BookInsert(randomNumeric(8), null);

        String content = objectMapper.writeValueAsString(bookInsert);
        ResultActions actions = mockMvc.perform(post(PATH_INSERT).contentType(APPLICATION_JSON_UTF8).content(content));
        actions.andExpect(status().isBadRequest());
        actions.andExpect(content().string(EMPTY));

        verifyNoMoreInteractions(bookService);
    }

    @Test
    public void delete_should_return_status_ok_if_known() throws Exception {
        String isbn = randomNumeric(8);
        given(bookService.delete(isbn)).willReturn(true);

        ResultActions actions = mockMvc.perform(delete(PATH_DELETE, isbn));
        actions.andExpect(status().isOk());
        actions.andExpect(content().string(EMPTY));

        verify(bookService, times(1)).delete(isbn);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    public void delete_should_return_not_found_if_not_known() throws Exception {
        String isbn = randomNumeric(8);
        given(bookService.delete(isbn)).willReturn(false);

        ResultActions actions = mockMvc.perform(delete(PATH_DELETE, isbn));
        actions.andExpect(status().isNotFound());
        actions.andExpect(content().string(EMPTY));

        verify(bookService, times(1)).delete(isbn);
        verifyNoMoreInteractions(bookService);
    }

}
