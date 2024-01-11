package com.bookrental.bookrental.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Message {
    SAVE("success.save"),
    RETRIEVE("success.retrieve"),
    RETRIVE_ALL("success.retrieve.all"),
    UPDATE("success.update"),
    DELETE("success.delete"),
    ID_NOT_FOUND("id.notfound");

    public String code;
}