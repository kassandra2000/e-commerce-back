package kassandrafalsitta.e_commerce_back.services;

import kassandrafalsitta.e_commerce_back.entities.User;
import kassandrafalsitta.e_commerce_back.exceptions.UnauthorizedException;
import kassandrafalsitta.e_commerce_back.payloads.UserLoginDTO;
import kassandrafalsitta.e_commerce_back.security.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UsersService usersService;

    @Autowired
    private JWTTools jwtTools;

    @Autowired
    private PasswordEncoder bcrypt;

    public String checkCredentialsAndGenerateToken(UserLoginDTO body) {

        User found = this.usersService.findByEmail(body.email());
        if (bcrypt.matches(body.password(), found.getPassword())) {
            return jwtTools.createToken(found);
        } else {
            throw new UnauthorizedException("Credenziali errate!");
        }


    }
}
