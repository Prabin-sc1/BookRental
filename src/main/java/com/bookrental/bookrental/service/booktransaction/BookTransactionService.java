package com.bookrental.bookrental.service.booktransaction;

import com.bookrental.bookrental.pojo.rent.BookRentRequest;
import com.bookrental.bookrental.pojo.returnn.BookReturnRequest;
import com.bookrental.bookrental.pojo.trasaction.BookTransactionOverdeuResponse;
import com.bookrental.bookrental.pojo.trasaction.BookTransactionResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
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

    // overdeu transactions
    List<BookTransactionOverdeuResponse> getOverdeuBookList();

    List<BookTransactionResponse> getAllTransactionRecordOfBook(Integer bookId);

    // transaction range with fromDate to toDate
    List<BookTransactionResponse> getTransactionWithinDateRange(LocalDate a, LocalDate b, LocalDate c, LocalDate d);
}
