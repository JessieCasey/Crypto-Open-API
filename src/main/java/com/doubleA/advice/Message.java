package com.doubleA.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class Message {
    private int statusCode;
    private Date timestamp;
    private String message;
    private String description;
}