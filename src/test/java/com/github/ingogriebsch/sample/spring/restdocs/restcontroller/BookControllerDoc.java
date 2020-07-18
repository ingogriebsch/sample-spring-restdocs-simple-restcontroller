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

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

import static com.github.ingogriebsch.sample.spring.restdocs.restcontroller.BookController.PATH_DELETE;
import static com.github.ingogriebsch.sample.spring.restdocs.restcontroller.BookController.PATH_FIND_ALL;
import static com.github.ingogriebsch.sample.spring.restdocs.restcontroller.BookController.PATH_FIND_ONE;
import static com.github.ingogriebsch.sample.spring.restdocs.restcontroller.BookController.PATH_INSERT;
import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.lang3.StringUtils.join;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BookController.class)
public class BookControllerDoc {

    private MockMvc mockMvc;
    private RestDocumentationResultHandler documentationHandler;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Rule
    public JUnitRestDocumentation documentation = new JUnitRestDocumentation();

    @Before
    public void before() {
        documentationHandler =
            document("book/{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
        mockMvc = webAppContextSetup(webApplicationContext).apply(documentationConfiguration(documentation))
            .alwaysDo(documentationHandler).build();
    }

    @Test
    public void findAll() throws Exception {
        Set<Book> books = newHashSet(new Book("0345391802", "The Hitchhiker's Guide to the Galaxy"),
            new Book("9781451673319", "Fahrenheit 451"), new Book("0062225677", "The Color of Magic"));
        given(bookService.findAll()).willReturn(books);

        ResultActions actions = mockMvc.perform(get(PATH_FIND_ALL).accept(APPLICATION_JSON_UTF8));
        actions.andExpect(status().isOk());

        Snippet requestHeaders =
            requestHeaders(headerWithName(ACCEPT).description("The content type the client is able to understand."));

        Snippet responseHeaders =
            responseHeaders(headerWithName(CONTENT_TYPE).description("The content type of the content returned."));

        Snippet responseFields = responseFields(fieldWithPath("[].isbn").description("The isbn of the book."),
            fieldWithPath("[].title").description("The title of the book."));

        actions.andDo(documentationHandler.document(requestHeaders, responseFields, responseHeaders));
    }

    @Test
    public void findOne() throws Exception {
        Book book = new Book("0345391802", "The Hitchhiker's Guide to the Galaxy");
        given(bookService.findOne(book.getIsbn())).willReturn(of(book));

        ResultActions actions = mockMvc.perform(get(PATH_FIND_ONE, book.getIsbn()).accept(APPLICATION_JSON_UTF8));
        actions.andExpect(status().isOk());

        Snippet pathParameters = pathParameters(parameterWithName("isbn").description("The isbn of the requested book."));

        Snippet requestHeaders =
            requestHeaders(headerWithName(ACCEPT).description("The content type the client is able to understand."));

        Snippet responseHeaders =
            responseHeaders(headerWithName(CONTENT_TYPE).description("The content type of the content returned."));

        actions.andDo(documentationHandler.document(pathParameters, requestHeaders, responseHeaders));
    }

    @Test
    public void insert() throws Exception {
        BookInsert bookInsert = new BookInsert("0062225677", "The Color of Magic");
        given(bookService.insert(bookInsert)).willReturn(of(book(bookInsert)));

        String content = objectMapper.writeValueAsString(bookInsert);
        ResultActions actions =
            mockMvc.perform(post(PATH_INSERT).content(content).contentType(APPLICATION_JSON_UTF8).accept(APPLICATION_JSON_UTF8));
        actions.andExpect(status().isCreated());

        Snippet requestHeaders =
            requestHeaders(headerWithName(ACCEPT).description("The content type the client is able to understand."),
                headerWithName(CONTENT_TYPE).description("The content type of the content sent with the request."));

        Snippet responseHeaders =
            responseHeaders(headerWithName(CONTENT_TYPE).description("The content type of the content returned."));

        actions.andDo(documentationHandler.document(requestHeaders, responseHeaders));
    }

    @Test
    public void delete() throws Exception {
        String isbn = "0062225677";
        given(bookService.delete(isbn)).willReturn(true);

        ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.delete(PATH_DELETE, isbn));
        actions.andExpect(status().isOk());

        Snippet pathParameters = pathParameters(parameterWithName("isbn").description("The isbn of the book to be deleted."));

        actions.andDo(documentationHandler.document(pathParameters));
    }

    @Test
    public void bookInsertResource() throws Exception {
        BookInsert bookInsert = new BookInsert("0062225677", "The Color of Magic");
        given(bookService.insert(bookInsert)).willReturn(of(book(bookInsert)));

        String content = objectMapper.writeValueAsString(bookInsert);
        ResultActions actions =
            mockMvc.perform(post(PATH_INSERT).content(content).contentType(APPLICATION_JSON_UTF8).accept(APPLICATION_JSON_UTF8));
        actions.andExpect(status().isCreated());

        ConstraintDescriptions bookInsertConstraintDescriptions = new ConstraintDescriptions(BookInsert.class);

        Snippet requestFields = requestFields(
            fieldWithPath("isbn").description("The isbn of the book.")
                .attributes(key("constraints").value(contraintsValue("isbn", bookInsertConstraintDescriptions))),
            fieldWithPath("title").description("The title of the book.")
                .attributes(key("constraints").value(contraintsValue("title", bookInsertConstraintDescriptions))));

        actions.andDo(documentationHandler.document(requestFields));
    }

    @Test
    public void bookResource() throws Exception {
        Book book = new Book("0345391802", "The Hitchhiker's Guide to the Galaxy");
        given(bookService.findOne(book.getIsbn())).willReturn(of(book));

        ResultActions actions = mockMvc.perform(get(PATH_FIND_ONE, book.getIsbn()).accept(APPLICATION_JSON_UTF8));
        actions.andExpect(status().isOk());

        Snippet responseFields = responseFields(fieldWithPath("isbn").description("The isbn of the book."),
            fieldWithPath("title").description("The title of the book."));

        actions.andDo(documentationHandler.document(responseFields));
    }

    private static String contraintsValue(String key, ConstraintDescriptions constraintsDescriptions) {
        return join(constraintsDescriptions.descriptionsForProperty(key).stream().map(s -> s + ".").collect(toList()), " ");
    }

    private static Book book(BookInsert bookInsert) {
        return new Book(bookInsert.getIsbn(), bookInsert.getTitle());
    }
}
