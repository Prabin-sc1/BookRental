package com.bookrental.bookrental.service.booktransaction;

import com.bookrental.bookrental.pojo.rent.BookRentRequest;
import com.bookrental.bookrental.pojo.returnn.BookReturnRequest;
import com.bookrental.bookrental.pojo.trasaction.BookTransactionResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface BookTransactionService {
    String addBookTransaction(BookRentRequest bookRentRequest);

    void returnBookTransaction(BookReturnRequest bookReturnRequest);

    List<BookTransactionResponse> getAllTransaction();

    BookTransactionResponse getSingleTransactionById(Integer id);

    void deleteTransactionById(Integer id);

    List<BookTransactionResponse> getAllTransactionByMember(Integer id);

    void save(MultipartFile file);

    ByteArrayInputStream getExcelData() throws IOException;

//    List<BookTransactionResponse> getTransactionsByBook(Integer bookId);

    // overdeu transactions

}
