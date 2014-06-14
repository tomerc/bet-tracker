package com.bettracker.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

/**
 * @author Tomer Cohen
 */
public abstract class Messages {
    private static final MessageSource messageSource = getMessageSource();

    public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }

    public static String getMessage(String code, Object[] args, String defaultMessage) {
        return getMessage(code, args, defaultMessage, Locale.getDefault());
    }

    public static String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return getMessage(code, args, "", locale);
    }

    public static String getMessage(String code, Object[] args) throws NoSuchMessageException {
        return getMessage(code, args, "", Locale.getDefault());
    }

    public static String getMessage(String code) throws NoSuchMessageException {
        return getMessage(code, null, "", Locale.getDefault());
    }

    private static MessageSource getMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }
}
