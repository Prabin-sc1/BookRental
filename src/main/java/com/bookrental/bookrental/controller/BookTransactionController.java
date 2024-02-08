package com.bookrental.bookrental.controller;

import com.bookrental.bookrental.constants.ModuleNameConstants;
import com.bookrental.bookrental.enums.Message;
import com.bookrental.bookrental.generic.GlobalApiResponse;
import com.bookrental.bookrental.model.BookTransaction;
import com.bookrental.bookrental.pojo.rent.BookRentRequest;
import com.bookrental.bookrental.pojo.returnn.BookReturnRequest;
import com.bookrental.bookrental.pojo.trasaction.BookTransactionOverdeuResponse;
import com.bookrental.bookrental.pojo.trasaction.BookTransactionResponse;
import com.bookrental.bookrental.service.booktransaction.BookTransactionService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/booktransaction")
@Tag(name = ModuleNameConstants.TRANSACTION)
public class BookTransactionController extends MyBaseController {
    private final BookTransactionService bookTransactionService;

    public BookTransactionController(BookTransactionService bookTransactionService) {
        this.bookTransactionService = bookTransactionService;
        this.module = ModuleNameConstants.TRANSACTION;
    }

    @PostMapping("/rent")
    @Operation(
            summary = "Rent transaction",
            description = "This end point used to create transaction for renting purpose.",
            responses = @ApiResponse(responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = BookRentRequest.class))
                    }
            )
    )
    public ResponseEntity<GlobalApiResponse> rentTransaction(@RequestBody @Valid BookRentRequest bookRentRequest) {
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.SAVE.getCode(), module),
                bookTransactionService.addBookTransaction(bookRentRequest)));
    }


    @PostMapping("/return")
    @Operation(
            summary = "Return Transaction",
            description = "This end point used to update transaction for returning book.",
            responses = @ApiResponse(responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = BookReturnRequest.class))
                    }
            )
    )
    public ResponseEntity<GlobalApiResponse> returnTransaction(@RequestBody BookReturnRequest bookRentRequest) {
        bookTransactionService.returnBookTransaction(bookRentRequest);
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.UPDATE.getCode(), module), null));
    }

    @GetMapping
    @Operation(
            summary = "Retrieve all transaction",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content
                            (array = @ArraySchema
                                    (schema = @Schema(implementation = BookTransactionResponse.class)))},
                            description = "This end point fetch all transaction"
                    )
            }
    )
    public ResponseEntity<GlobalApiResponse> getAllTransaction() {
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.RETRIVE_ALL.getCode(), module),
                bookTransactionService.getAllTransaction()));
    }


    @GetMapping("/all-overdeu-transaction")
    @Operation(
            summary = "Retrieve all overdeu transaction",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content
                            (array = @ArraySchema
                                    (schema = @Schema(implementation = BookTransactionOverdeuResponse.class)))},
                            description = "This end point fetch all transaction"
                    )
            }
    )
    public ResponseEntity<GlobalApiResponse> getAllOverdeuTransaction() {
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.RETRIVE_ALL.getCode(), module),
                bookTransactionService.getOverdeuBookList()));
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Get transaction by id",
            description = "This end point can be used for getting transaction by id",
            responses = @ApiResponse(responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = BookTransactionResponse.class))
                    }
            )
    )
    public ResponseEntity<GlobalApiResponse> getSingleTransaction(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.RETRIEVE.getCode(), module), bookTransactionService.getSingleTransactionById(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete transaction",
            description = "This end point is used to delete transaction",
            responses = @ApiResponse(responseCode = "200"
            )
    )
    public ResponseEntity<BookTransaction> delete(@PathVariable("id") Integer id) {
        bookTransactionService.deleteTransactionById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/member/{id}")
    @Operation(
            summary = "Get transaction by member id",
            description = "This end point can be used for getting transaction member by id",
            responses = @ApiResponse(responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = BookTransactionResponse.class))
                    }
            )
    )
    public ResponseEntity<List<BookTransactionResponse>> getAllTransactionByMemberId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(bookTransactionService.getAllTransactionByMember(id));
    }

    @GetMapping("/download-excel-data")
    @Operation(
            summary = "Retrieve all transaction in excel",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content
                            (array = @ArraySchema
                                    (schema = @Schema(implementation = BookTransactionResponse.class)))},
                            description = "This end point fetch all transaction"
                    )
            }
    )
    public ResponseEntity<Resource> download() throws IOException {
        String fileName = "transaction.xlsx";
        ByteArrayInputStream bis = bookTransactionService.getExcelData();
        InputStreamResource file = new InputStreamResource(bis);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    /*@PostMapping("/upload")
    public ResponseEntity<GlobalApiResponse> saveTransaction(@RequestParam("file") MultipartFile multipartFile) {
        bookTransactionService.save(multipartFile);
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.SAVE.getCode(), module),
                null));
    }*/
}
