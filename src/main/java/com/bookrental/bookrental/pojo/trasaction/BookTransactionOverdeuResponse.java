package com.bookrental.bookrental.pojo.trasaction;

import com.bookrental.bookrental.enums.RentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookTransactionOverdeuResponse {
    private Integer id;
    private String code;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String bookName;
    private String memberName;
}

