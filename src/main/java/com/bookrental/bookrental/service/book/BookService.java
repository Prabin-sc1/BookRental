package com.bookrental.bookrental.service.book;

import com.bookrental.bookrental.model.Book;
import com.bookrental.bookrental.pojo.book.BookRequestPojo;
import com.bookrental.bookrental.pojo.book.BookResponsePojo;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface BookService {
    //    void createUpdateBook(BookRequestPojo book, MultipartFile file);
    void createUpdateBook(BookRequestPojo book);

    void deleteBook(Integer id);

    List<BookResponsePojo> getAllBooks();

    BookResponsePojo getBookById(Integer id);

    Book findBookById(Integer id);

    Book findBookByName(String name);

    ByteArrayInputStream getExcelData() throws IOException;

}
