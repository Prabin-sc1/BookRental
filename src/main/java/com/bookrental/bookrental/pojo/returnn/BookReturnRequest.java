package com.bookrental.bookrental.pojo.returnn;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookReturnRequest {
    private Integer id;
    @NotNull
    private String code;
    /*
    @NotNull
    private Integer memberId;
    @NotNull
    private Integer bookId;*/
}
