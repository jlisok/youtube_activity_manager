package com.jlisok.youtube_activity_manager.registration.utils;

import com.jlisok.youtube_activity_manager.registration.exceptions.BadRegistrationRequestException;
import com.jlisok.youtube_activity_manager.registration.exceptions.FieldViolationBadRegistrationRequestException;
import com.jlisok.youtube_activity_manager.registration.exceptions.UnexpectedErrorBadRegistrationRequestException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;

public class BadRegistrationExceptionBuilder {

    public static void handleHibernateExceptionFromNestedStack(DataIntegrityViolationException e) throws BadRegistrationRequestException {

        String detailedMessage = "";

        if (e.getMostSpecificCause() instanceof PSQLException) {
            PSQLException mostSpecificCause = (PSQLException) e.getMostSpecificCause();
            String psqlState = mostSpecificCause.getSQLState();
            String message = mostSpecificCause.getMessage();
            detailedMessage = "PSQLState: " + psqlState;
            if (message.contains("email")) {
                throw new FieldViolationBadRegistrationRequestException("Registration failed", e, detailedMessage);
            }
        }
        throw new UnexpectedErrorBadRegistrationRequestException("Registration failed", e, detailedMessage);
    }

}
