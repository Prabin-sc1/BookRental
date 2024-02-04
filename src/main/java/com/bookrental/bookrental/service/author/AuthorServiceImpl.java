package com.bookrental.bookrental.service.author;

import com.bookrental.bookrental.config.CustomMessageSource;
import com.bookrental.bookrental.constants.ModuleNameConstants;
import com.bookrental.bookrental.enums.Message;
import com.bookrental.bookrental.exception.AppException;
import com.bookrental.bookrental.helpers.Helper;
import com.bookrental.bookrental.mapper.AuthorMapper;
import com.bookrental.bookrental.model.Author;
import com.bookrental.bookrental.pojo.author.AuthorRequestPojo;
import com.bookrental.bookrental.pojo.author.AuthorResponsePojo;
import com.bookrental.bookrental.repository.AuthorRepository;
import com.bookrental.bookrental.utils.NullAwareBeanUtilsBean;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private NullAwareBeanUtilsBean beanUtils = new NullAwareBeanUtilsBean();

    private final AuthorMapper authorMapper;

    private final CustomMessageSource customMessageSource;

    @Override
    public void createUpdateAuthor(AuthorRequestPojo authorRequestPojo) {
        Author author = new Author();
        if (authorRequestPojo.getId() != null) {
            author = authorRepository.findById(authorRequestPojo.getId()).orElse(author);
        }

        try {
            beanUtils.copyProperties(author, authorRequestPojo);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AppException(e.getMessage());
        }
        author.setEmail(authorRequestPojo.getEmail().toLowerCase());

        try {
            authorRepository.save(author);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(customMessageSource.get(Message.ALREADY_EXISTS.getCode(), ModuleNameConstants.AUTHOR));
        }
    }

    @Override
    public List<AuthorResponsePojo> getAllAuthor() {
//        List<Author> a = authorRepository.findAll();
//        List<AuthorResponsePojo> list1 = a.stream().map(e -> this.modelMapper.map(e, AuthorResponsePojo.class)).collect(Collectors.toList());
//        return list1;
        return authorMapper.getAllAuthor();
    }

    @Override
    public AuthorResponsePojo getAuthorById(Integer id) {
        // previously I used to write code this way
//        Author a = authorRepository.findById(id).orElseThrow(() -> new AppException(customMessageSource.
//                get(Message.ID_NOT_FOUND.getCode(), ModuleNameConstants.AUTHOR)));
//        return this.modelMapper.map(a, AuthorResponsePojo.class);
        // this is the latest one to do same thing
        return authorMapper.getSingleAuthor(id).orElseThrow(() -> new AppException(customMessageSource.
                get(Message.ID_NOT_FOUND.getCode(), ModuleNameConstants.AUTHOR)));
    }

    @Override
    public void deleteById(Integer id) {
        authorRepository.deleteById(id);
    }

    @Override
    public Author findAuthorById(Integer id) {
        return authorRepository.findById(id).orElseThrow(() ->
                new AppException(customMessageSource.get(Message.ID_NOT_FOUND.getCode(), ModuleNameConstants.AUTHOR)));
    }

    private static String SHEET_NAME = "author";

    public static String[] getHeaders(Class<?> className) {
        List<String> headers = new ArrayList<>();
        Field[] fields = className.getDeclaredFields();
        for (Field field : fields) {
            headers.add(field.getName());
        }
        return headers.toArray(new String[headers.size()]);
    }

    public ByteArrayInputStream getExcelData() throws IOException {
        List<Author> all = authorRepository.findAll();
        ByteArrayInputStream byteArrayInputStream = Helper.dataToExcel(all, SHEET_NAME, getHeaders(Author.class));
        return byteArrayInputStream;
    }

    public void save(MultipartFile file) {
        try {
            List<Author> list = Helper.convertExcelToList(Author.class, file.getInputStream());
            authorRepository.saveAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}