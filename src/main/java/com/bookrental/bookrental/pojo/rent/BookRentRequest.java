package com.bookrental.bookrental.pojo.rent;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRentRequest {
    private Integer id;
    //    @NotNull(message = "Book ID can't be null")
//    private Integer bookId;
//    @NotNull(message = "Member ID can't be null")
//    private Integer memberId;

    private String memberName;
    private String bookName;
//    private String memberName;

    // af
}
