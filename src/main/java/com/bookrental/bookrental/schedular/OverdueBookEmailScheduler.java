package com.bookrental.bookrental.schedular;

import com.bookrental.bookrental.mapper.BookTransactionMapper;
import com.bookrental.bookrental.model.Book;
import com.bookrental.bookrental.model.Member;
import com.bookrental.bookrental.pojo.trasaction.BookTransactionOverdeuResponse;
import com.bookrental.bookrental.service.book.BookService;
import com.bookrental.bookrental.service.email.NewEmailService;
import com.bookrental.bookrental.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OverdueBookEmailScheduler {

    private final BookTransactionMapper bookTransactionMapper;
    private final NewEmailService emailService;
    private final BookService bookService;
    private final MemberService memberService;


//    @Scheduled(cron = "0/1 * * * * *") // Run every second
    public void sendOverdueEmails() {
        List<BookTransactionOverdeuResponse> list = bookTransactionMapper.overdeuList();
        for (BookTransactionOverdeuResponse bookTransaction : list) {
            Book bookByName = bookService.findBookByName(bookTransaction.getBookName());
            Member memberByName = memberService.findMemberByName(bookTransaction.getMemberName());
            String subject = "Overdue Book Reminder";
            String message = "Dear " + memberByName.getName() + " ,\n\n" + " This is reminder that the book with the name " + bookByName.getName()
                    + " is overdue. Please return ASAP. \n\n Thank you! ";
            String emailMember = memberByName.getEmail();
            try {
                emailService.sendEmail(emailMember, subject, message);
                System.out.println("first one");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    @Scheduled(cron = "0/1 * * * * *") // Run every second
    public void sendOverdueReminderEmails() {
        List<BookTransactionOverdeuResponse> list = bookTransactionMapper.overdeuListForReminderBeforeOneDay();
        for (BookTransactionOverdeuResponse bookTransaction : list) {
            Book bookByName = bookService.findBookByName(bookTransaction.getBookName());
            Member memberByName = memberService.findMemberByName(bookTransaction.getMemberName());
            String subject = "Overdue Book Reminder";
            String message = "Dear " + memberByName.getName() + " ,\n\n" + " This is reminder that the book with the name " + bookByName.getName()
                    + " will be overdue in 1 day. Please return ASAP. \n\n Thank you! ";
            String emailMember = memberByName.getEmail();
            try {
                emailService.sendEmail(emailMember, subject, message);
                System.out.println("secondone");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
