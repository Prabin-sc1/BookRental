package com.bookrental.bookrental.controller;

import com.bookrental.bookrental.constants.ModuleNameConstants;
import com.bookrental.bookrental.enums.Message;
import com.bookrental.bookrental.generic.GlobalApiResponse;
import com.bookrental.bookrental.pojo.book.BookRequestPojo;
import com.bookrental.bookrental.pojo.book.BookResponsePojo;
import com.bookrental.bookrental.service.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/book")
@Tag(name = ModuleNameConstants.BOOK)
public class BookController extends MyBaseController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
        this.module = ModuleNameConstants.BOOK;
    }

    @PostMapping
    @Operation(
            summary = "Create and Update Book",
            description = "This end point used to create and update book",
            responses = @ApiResponse(responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = BookRequestPojo.class))
                    }
            )
    )
    public ResponseEntity<GlobalApiResponse> create(@Valid @RequestBody BookRequestPojo bookRequestPojo) {
        bookService.createUpdateBook(bookRequestPojo);
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.SAVE.getCode(), module),
                null));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get book by id",
            description = "This end point can be used for getting book by id",
            responses = @ApiResponse(responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = BookResponsePojo.class))
                    }
            )
    )
    public ResponseEntity<GlobalApiResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.RETRIEVE.getCode(), module), bookService.getBookById(id)));
    }


    @GetMapping
    @Operation(
            summary = "Retrieve all books",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content
                            (array = @ArraySchema
                                    (schema = @Schema(implementation = BookResponsePojo.class)))},
                            description = "This end point fetch all books."
                    )
            }
    )
    public ResponseEntity<GlobalApiResponse> getAll() {
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.RETRIVE_ALL.getCode(), module), bookService.getAllBooks()));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete book",
            description = "This end point can be used to delete book",
            responses = @ApiResponse(responseCode = "200"
            )
    )
    public ResponseEntity<GlobalApiResponse> delete(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.DELETE.getCode(), module), null));
    }


    @GetMapping("/download-excel-data")
    @Operation(
            summary = "Retrieve all books in excel",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content
                            (array = @ArraySchema
                                    (schema = @Schema(implementation = BookResponsePojo.class)))},
                            description = "This end point fetch all books"
                    )
            }
    )
    public ResponseEntity<Resource> download() throws IOException {
        String fileName = "book.xlsx";
        ByteArrayInputStream bis = bookService.getExcelData();
        InputStreamResource file = new InputStreamResource(bis);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }
}
