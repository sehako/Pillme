package com.ssafy.pillme.global.response;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
public class MessageUtil {
    private static MessageSource messageSource;

    public static void init(MessageSource messageSource) {
        MessageUtil.messageSource = messageSource;
    }

    public static String getMessage(String code) {
        Locale locale = LocaleContextHolder.getLocale();  // 현재 스레드의 locale을 자동으로 가져옴
        return messageSource.getMessage(code, null, locale);
    }

    public static String getMessage(String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();  // 현재 스레드의 locale을 자동으로 가져옴
        return messageSource.getMessage(code, args, locale);
    }
}