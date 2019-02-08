package com.github.ingogriebsch.sample.spring.restdocs.restcontroller;

import static com.github.ingogriebsch.sample.spring.restdocs.restcontroller.BookController.PATH_DELETE;
import static com.github.ingogriebsch.sample.spring.restdocs.restcontroller.BookController.PATH_FIND_ALL;
import static com.github.ingogriebsch.sample.spring.restdocs.restcontroller.BookController.PATH_FIND_ONE;
import static com.github.ingogriebsch.sample.spring.restdocs.restcontroller.BookController.PATH_INSERT;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Optional.of;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

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

        Snippet pathParameters = pathParameters(parameterWithName("isbn").description("The isbn of the book."));

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

        Snippet pathParameters = pathParameters(parameterWithName("isbn").description("The isbn of the book."));

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

        Snippet requestFields = requestFields(fieldWithPath("isbn").description("The isbn of the book."),
            fieldWithPath("title").description("The title of the book."));

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

    private static Book book(BookInsert bookInsert) {
        return new Book(bookInsert.getIsbn(), bookInsert.getTitle());
    }
}
