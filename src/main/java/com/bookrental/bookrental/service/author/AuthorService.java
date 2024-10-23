package com.bookrental.bookrental.service.author;

import com.bookrental.bookrental.model.Author;
import com.bookrental.bookrental.pojo.author.AuthorRequestPojo;
import com.bookrental.bookrental.pojo.author.AuthorResponsePojo;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface AuthorService {
    void createUpdateAuthor(AuthorRequestPojo authorRequestPojo);

    List<AuthorResponsePojo> getAllAuthor();

    AuthorResponsePojo getAuthorById(Integer id);


    AuthorResponsePojo getAuthorByEmail(String email);

    void deleteById(Integer id);

    Author findAuthorById(Integer id);

    ByteArrayInputStream getExcelData() throws IOException;

    void save(MultipartFile file);

}
