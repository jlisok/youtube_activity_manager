package com.jlisok.youtube_activity_manager.registration.services;

import com.jlisok.youtube_activity_manager.login.utils.TokenCreator;
import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.BadRegistrationRequestException;
import com.jlisok.youtube_activity_manager.registration.exceptions.PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException;
import com.jlisok.youtube_activity_manager.registration.utils.BadRegistrationExceptionBuilder;
import com.jlisok.youtube_activity_manager.registration.utils.DtoToUserTranslator;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final DtoToUserTranslator dtoToUserTranslator;
    private final TokenCreator tokenCreator;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public RegistrationService(UserRepository userRepository, DtoToUserTranslator dtoToUserTranslator, TokenCreator tokenCreator) {
        this.tokenCreator = tokenCreator;
        logger.debug("Registration service - initialization.");
        this.userRepository = userRepository;
        this.dtoToUserTranslator = dtoToUserTranslator;
    }

    public String addUserToDatabase(RegistrationRequestDto registrationRequestDto) throws PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException, BadRegistrationRequestException {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        User user = dtoToUserTranslator.translate(registrationRequestDto, now, id);
        try {
            saveUser(user);
        } catch (DataIntegrityViolationException e) {
            BadRegistrationExceptionBuilder.handleHibernateExceptionFromNestedStack(e);
        }
        String jwToken = tokenCreator.create(user.getId().toString(), now, user.checkIfEverAuthorized());
        logger.info("Registration service - success.");
        return jwToken;
    }

    @Transactional
    private void saveUser(User user) {
        userRepository.saveAndFlush(user);
    }

}
