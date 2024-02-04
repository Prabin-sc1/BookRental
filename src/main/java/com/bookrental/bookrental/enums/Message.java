package com.bookrental.bookrental.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Message {
    SAVE("success.save"),
    TOKEN_GENERATE("token.generate"),
    RETRIEVE("success.retrieve"),
    RETRIVE_ALL("success.retrieve.all"),
    UPDATE("success.update"),
    PASSWORD_UPDATE("password.update"),
    GENERATE_OTP("generate.otp"),
    VERIFY_OTP("verify.otp"),
    NOT_GENERATE_OTP("not.generate.otp"),
    PASSWORD_NOT_MATCH("password.not.match"),
    DELETE("success.delete"),
    ID_NOT_FOUND("id.notfound"),
    INVALID_CODE_MEMBER_BOOK("invalid.code.member.book"),
    USER_NOT_FOUND("user.notfound"),
    ALREADY_EXISTS("already.exists"),
    ALREADY_RENT("already.rent"),
    NOT_ACTIVE("not.active"),
    FAILED("failed"),
    FAILED_EMAIL("failed.email"),
    OUT_OF_STOCK("out.of.stock"),
    CREDENTIAL_INVALID("credential.invalid");

    public String code;
}
