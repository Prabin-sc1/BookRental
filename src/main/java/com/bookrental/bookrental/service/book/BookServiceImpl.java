package com.bookrental.bookrental.service.book;

import com.bookrental.bookrental.config.CustomMessageSource;
import com.bookrental.bookrental.constants.ModuleNameConstants;
import com.bookrental.bookrental.enums.Message;
import com.bookrental.bookrental.exception.AppException;
import com.bookrental.bookrental.helpers.Helper;
import com.bookrental.bookrental.mapper.BookMapper;
import com.bookrental.bookrental.model.Author;
import com.bookrental.bookrental.model.Book;
import com.bookrental.bookrental.model.Category;
import com.bookrental.bookrental.pojo.book.BookRequestPojo;
import com.bookrental.bookrental.pojo.book.BookResponsePojo;
import com.bookrental.bookrental.repository.AuthorRepository;
import com.bookrental.bookrental.repository.BookRepository;
import com.bookrental.bookrental.service.author.AuthorService;
import com.bookrental.bookrental.service.category.CategoryService;
import com.bookrental.bookrental.service.image.ImageService;
import com.bookrental.bookrental.utils.NullAwareBeanUtilsBean;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final NullAwareBeanUtilsBean utilsBean = new NullAwareBeanUtilsBean();

    private final AuthorRepository authorRepository;

    private final CustomMessageSource customMessageSource;

    private final CategoryService categoryService;

    private final ImageService imageService;

/*
    @Value("${project.images}")
    private String path;
*/

    @Override
    public void createUpdateBook(BookRequestPojo book) {
        List<Author> authors;
        Book b = new Book();

        if (book.getId() != null) {
            b = bookRepository.findById(book.getId()).orElse(b);
        }

        try {
            utilsBean.copyProperties(b, book);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AppException(e.getMessage());
        }

        Category tempCategory = categoryService.findCategoryById(book.getCategoryId());

        authors = book.getAuthorId().stream().map(e -> authorRepository.findById(e).
                orElseThrow(() -> new AppException(customMessageSource.get(Message.ID_NOT_FOUND.getCode(),
                        ModuleNameConstants.AUTHOR)))).toList();

        b.setAuthors(authors);
        b.setCategory(tempCategory);

        try {
            bookRepository.save(b);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(customMessageSource.get(Message.ALREADY_EXISTS.getCode(), ModuleNameConstants.BOOK));
        }

        /*if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            Path filePath = Paths.get(path, originalFilename);
            try {
                Files.write(filePath, file.getBytes());
            } catch (IOException e) {
                throw new AppException(e.getMessage());
            }
        }*/
    }

    @Override
    public List<BookResponsePojo> getAllBooks() {
        return bookMapper.getAllBook();
    }

    @Override
    public BookResponsePojo getBookById(Integer id) {
        return bookMapper.getBookById(id).orElseThrow(() -> new AppException(customMessageSource.
                get(Message.ID_NOT_FOUND.getCode(), ModuleNameConstants.BOOK)));
    }

    @Override
    public void deleteBook(Integer id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Book findBookById(Integer id) {
        return bookRepository.findById(id).orElseThrow(() ->
                new AppException(customMessageSource.get(Message.ID_NOT_FOUND.getCode(), ModuleNameConstants.BOOK)));
    }

    public static String SHEET_NAME = "book";

    public static String[] getHeaders(Class<?> className) {
        List<String> headers = new ArrayList<>();
        Field[] fields = className.getDeclaredFields();
        for (Field field : fields) {
            headers.add(field.getName());
        }
        return headers.toArray(new String[headers.size()]);
    }

    public ByteArrayInputStream getExcelData() throws IOException {
        List<BookResponsePojo> all = bookMapper.getAllBook();
        ByteArrayInputStream byteArrayInputStream = Helper.dataToExcel(all, SHEET_NAME, getHeaders(BookResponsePojo.class));
        return byteArrayInputStream;
    }

    //start
    /*public String upload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename(); // abc.png
        String randomID = UUID.randomUUID().toString();
        String newFileName = randomID.concat(originalFilename.substring(originalFilename.lastIndexOf(".")));
//      String filePath = path + File.separator + newFileName; // project/abc.png
        String filePath = Paths.get(path, newFileName).toString();

        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }
        Files.copy(file.getInputStream(), Paths.get(filePath));
        return newFileName;
    }*/
    //end
}
