package com.example.onlineEditorFront.exceptions;

import com.example.onlineEditorFront.enums.ResponseEnum;

public class UnauthorizedException extends RuntimeException {

    private ResponseEnum responseEnum;

    public UnauthorizedException() {
        super(ResponseEnum.UNAUTHORIZED.toString());
    }

    public void setResponseEnum(ResponseEnum responseEnum) {
        this.responseEnum = responseEnum;
    }

    public ResponseEnum getResponseEnum(){
        return responseEnum;
    }
}
