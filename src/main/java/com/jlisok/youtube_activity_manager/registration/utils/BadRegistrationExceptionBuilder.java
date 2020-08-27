package com.jlisok.youtube_activity_manager.registration.utils;

import com.jlisok.youtube_activity_manager.registration.exceptions.BadRegistrationRequestException;
import com.jlisok.youtube_activity_manager.registration.exceptions.FieldViolationBadRegistrationRequestException;
import com.jlisok.youtube_activity_manager.registration.exceptions.UnexpectedErrorBadRegistrationRequestException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;

public class BadRegistrationExceptionBuilder {

    public static void handleHibernateExceptionFromNestedStack(DataIntegrityViolationException e) throws BadRegistrationRequestException {

        if (e.getMostSpecificCause() instanceof PSQLException) {
            PSQLException mostSpecificCause = (PSQLException) e.getMostSpecificCause();
            String message = mostSpecificCause.getMessage();
            if (message.contains("ERROR: duplicate key value violates unique constraint \"users_email_key\"")) {
                throw new FieldViolationBadRegistrationRequestException("Registration failed", e);
            }
        }
        throw new UnexpectedErrorBadRegistrationRequestException("Registration failed", e);
    }

}
