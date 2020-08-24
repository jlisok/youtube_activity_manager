package com.jlisok.youtube_activity_manager.registration.controllers;

import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.registration.services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/registration")
@CrossOrigin

public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public void addUser(@Valid @RequestBody RegistrationRequestDto registrationRequestDto) throws RegistrationException {
        registrationService.addUserToDatabase(registrationRequestDto);
    }

}
