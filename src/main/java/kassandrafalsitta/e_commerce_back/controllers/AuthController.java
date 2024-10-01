package kassandrafalsitta.e_commerce_back.controllers;

import kassandrafalsitta.e_commerce_back.services.AuthService;
import kassandrafalsitta.e_commerce_back.exceptions.BadRequestException;
import kassandrafalsitta.e_commerce_back.payloads.UserDTO;
import kassandrafalsitta.e_commerce_back.payloads.UserLoginDTO;
import kassandrafalsitta.e_commerce_back.payloads.UserLoginRespDTO;
import kassandrafalsitta.e_commerce_back.payloads.UserRespDTO;
import kassandrafalsitta.e_commerce_back.services.UsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;


import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UsersService UsersService;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public UserLoginRespDTO login(@RequestBody UserLoginDTO payload) {
            return new UserLoginRespDTO(this.authService.checkCredentialsAndGenerateToken(payload));
    }


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRespDTO createUser(@RequestBody  @Validated UserDTO body, BindingResult validationResult) {
        if(validationResult.hasErrors())  {
            String messages = validationResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(". "));
            throw new BadRequestException("Ci sono stati errori nel payload. " + messages);
        } else {
            return new UserRespDTO(this.UsersService.saveUser(body).getId());
        }
    }
}
