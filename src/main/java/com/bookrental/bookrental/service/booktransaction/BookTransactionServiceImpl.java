package com.bookrental.bookrental.service.booktransaction;

import com.bookrental.bookrental.config.CustomMessageSource;
import com.bookrental.bookrental.constants.ModuleNameConstants;
import com.bookrental.bookrental.enums.Message;
import com.bookrental.bookrental.enums.RentType;
import com.bookrental.bookrental.exception.AppException;
import com.bookrental.bookrental.helpers.Helper;
import com.bookrental.bookrental.mapper.BookTransactionMapper;
import com.bookrental.bookrental.model.Book;
import com.bookrental.bookrental.model.BookTransaction;
import com.bookrental.bookrental.model.Category;
import com.bookrental.bookrental.model.Member;
import com.bookrental.bookrental.pojo.rent.BookRentRequest;
import com.bookrental.bookrental.pojo.returnn.BookReturnRequest;
import com.bookrental.bookrental.pojo.trasaction.BookTransactionOverdeuResponse;
import com.bookrental.bookrental.pojo.trasaction.BookTransactionResponse;
import com.bookrental.bookrental.repository.*;
import com.bookrental.bookrental.service.book.BookService;
import com.bookrental.bookrental.service.member.MemberService;
import com.bookrental.bookrental.utils.NullAwareBeanUtilsBean;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BookTransactionServiceImpl implements BookTransactionService {

    private final BookTransactionRepository bookTransactionRepository;

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    private final NullAwareBeanUtilsBean beanUtils = new NullAwareBeanUtilsBean();

    private final BookTransactionMapper bookTransactionMapper;
    private final Random r = new Random();

    private final BookService bookService;

    private final CustomMessageSource customMessageSource;

    private final MemberRepository memberRepository;

    @Override
    public String addBookTransaction(BookRentRequest bookRentRequest) {
        BookTransaction bookTransaction = new BookTransaction();
        if (bookRentRequest.getId() != null)
            bookTransaction = bookTransactionRepository.findById(bookRentRequest.getId()).orElse(bookTransaction);

        try {
            beanUtils.copyProperties(bookTransaction, bookRentRequest);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AppException(e.getMessage());
        }

//        Book book = bookService.findBookById(bookRentRequest.getBookId());
        Book book = bookRepository.findByName(bookRentRequest.getBookName())
                .orElseThrow(() -> new AppException(customMessageSource.get(Message.ID_NOT_FOUND.getCode(), ModuleNameConstants.BOOK)));

//        Member member = memberService.findMemberById(bookRentRequest.getMemberId());
        Member member = memberRepository.findByName(bookRentRequest.getMemberName())
                .orElseThrow(() -> new AppException(customMessageSource.get(Message.ID_NOT_FOUND.getCode(), ModuleNameConstants.BOOK)));

        int overdewBooks = bookTransactionMapper.countTransactionsByMemberAndRentStatus(member.getId(), String.valueOf(RentType.RENT));
        if (overdewBooks > 0) {
            throw new AppException(customMessageSource.get(Message.ALREADY_RENT.getCode(), ModuleNameConstants.TRANSACTION));
        }

        bookTransaction.setFromDate(LocalDate.now());
        bookTransaction.setToDate(LocalDate.now().plusDays(10));
        bookTransaction.setRentStatus(RentType.RENT);
        if (Boolean.TRUE.equals(member.isActive())) {
            bookTransaction.setMember(member);
        } else {
            throw new AppException(customMessageSource.get(Message.NOT_ACTIVE.getCode(), ModuleNameConstants.MEMBER));
        }
        if (Boolean.TRUE.equals(book.isActive())) {
            bookTransaction.setBook(book);
        } else {
            throw new AppException(customMessageSource.get(Message.NOT_ACTIVE.getCode(), ModuleNameConstants.BOOK));

        }
        bookTransaction.setCode("#" + r.nextInt());
        book.setStockCount(book.getStockCount() - 1);
        bookTransaction.setActive(true);

        if (book.getStockCount() < 1)
            throw new AppException(customMessageSource.get(Message.OUT_OF_STOCK.getCode(), ModuleNameConstants.BOOK));
        else
            bookRepository.save(book);
        bookTransactionRepository.save(bookTransaction);
        return bookTransaction.getCode();
    }

    @Override
    public void returnBookTransaction(@Valid @RequestBody BookReturnRequest bookReturnRequest) {
        BookTransaction bookTransaction = findBookTransaction(bookReturnRequest);
//        updateBookStockCount(bookReturnRequest.getBookId());
        updateBookStockCount(bookTransaction.getBook().getId());
        updateBookTransaction(bookTransaction);
    }

    /*private BookTransaction findBookTransaction(BookReturnRequest bookReturnRequest) {
        return bookTransactionRepository.findByCodeAndMemberIdAndBookId(
                bookReturnRequest.getCode(),
                bookReturnRequest.getMemberId(),
                bookReturnRequest.getBookId()
        ).orElseThrow(() -> new AppException(customMessageSource.get(Message.INVALID_CODE_MEMBER_BOOK.getCode(), ModuleNameConstants.TRANSACTION)));
    }*/

    private BookTransaction findBookTransaction(BookReturnRequest bookReturnRequest) {
        return bookTransactionRepository.findTransactionByCode(bookReturnRequest.getCode())
                .orElseThrow(() -> new AppException(customMessageSource.
                        get(Message.INVALID_CODE_MEMBER_BOOK.getCode(), ModuleNameConstants.TRANSACTION)));
    }


    private void updateBookStockCount(Integer id) {
        Book book = bookService.findBookById(id);
        book.setStockCount(book.getStockCount() + 1);
        bookRepository.save(book);
    }

    private void updateBookTransaction(BookTransaction bookTransaction) {
        bookTransaction.setToDate(LocalDate.now());
        bookTransaction.setRentStatus(RentType.RETURN);
        bookTransaction.setActive(true);
        bookTransactionRepository.save(bookTransaction);
    }

    @Override
    public List<BookTransactionResponse> getAllTransaction() {
        return bookTransactionMapper.getAll();
    }

    @Override
    public BookTransactionResponse getSingleTransactionById(Integer id) {
        return bookTransactionMapper.getById(id).orElseThrow(() ->
                new AppException(customMessageSource.get(Message.ID_NOT_FOUND.getCode(), ModuleNameConstants.TRANSACTION)));
    }

    @Override
    public void deleteTransactionById(Integer id) {
        bookTransactionRepository.deleteById(id);
    }

    @Override
    public List<BookTransactionResponse> getAllTransactionByMember(Integer id) {
        return bookTransactionMapper.getAllTransactionByMemberId(id);
    }

    public void save(MultipartFile file) {
        try {
            List<Category> list = Helper.convertExcelToList(Category.class, file.getInputStream());
            categoryRepository.saveAll(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String SHEET_NAME = "book_transaction";

    public static String[] getHeaders(Class<?> className) {
        List<String> headers = new ArrayList<>();
        Field[] fields = className.getDeclaredFields();
        for (Field field : fields) {
            headers.add(field.getName());
        }
        return headers.toArray(new String[headers.size()]);
    }

    public ByteArrayInputStream getExcelData() throws IOException {
        List<BookTransactionResponse> all = bookTransactionMapper.getAll();
        ByteArrayInputStream byteArrayInputStream = Helper.dataToExcel(all, SHEET_NAME, getHeaders(BookTransactionResponse.class));
        return byteArrayInputStream;
    }
    @Override
    public List<BookTransactionOverdeuResponse> getOverdeuBookList() {
        return bookTransactionMapper.overdeuList();
    }
}
