package com.bookrental.bookrental.service.author;

import com.bookrental.bookrental.Exception.AppException;
import com.bookrental.bookrental.config.CustomMessageSource;
import com.bookrental.bookrental.constants.ModuleNameConstants;
import com.bookrental.bookrental.enums.Message;
import com.bookrental.bookrental.model.Author;
import com.bookrental.bookrental.pojo.author.AuthorRequestPojo;
import com.bookrental.bookrental.repository.AuthorRepository;
import com.bookrental.bookrental.utils.NullAwareBeanUtilsBean;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    // just because i don't put final keyword before AuthorRepository it is giving me error
    private NullAwareBeanUtilsBean beanUtils = new NullAwareBeanUtilsBean();

    private CustomMessageSource customMessageSource;

    @Override
    public void createUpdateAuthor(AuthorRequestPojo authorRequestPojo) {
        Author author = new Author();
        if (authorRequestPojo.getId() != null) {
            System.out.println("hello there");
            author = authorRepository.findById(authorRequestPojo.getId()).orElse(author);
        }

        try {
            beanUtils.copyProperties(author, authorRequestPojo);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AppException(e.getMessage(), e);
        }
        authorRepository.save(author);

    }

    @Override
    public List<Author> getAllAuthor() {
        return authorRepository.findAll();
    }

    @Override
    public Author getAuthorById(Integer id) {
        return authorRepository.findById(id).orElseThrow(() -> new AppException(customMessageSource.
                get(Message.ID_NOT_FOUND.getCode(), ModuleNameConstants.AUTHOR)));
    }

    @Override
    public void deleteById(Integer id) {
        authorRepository.deleteById(id);
    }
}
