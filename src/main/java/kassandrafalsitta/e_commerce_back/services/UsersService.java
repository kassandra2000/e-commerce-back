package kassandrafalsitta.e_commerce_back.services;

import kassandrafalsitta.e_commerce_back.entities.Product;
import kassandrafalsitta.e_commerce_back.entities.User;
import kassandrafalsitta.e_commerce_back.enums.Role;
import kassandrafalsitta.e_commerce_back.exceptions.BadRequestException;
import kassandrafalsitta.e_commerce_back.exceptions.NotFoundException;
import kassandrafalsitta.e_commerce_back.payloads.UserDTO;
import kassandrafalsitta.e_commerce_back.payloads.UserProductListDTO;
import kassandrafalsitta.e_commerce_back.repositories.ProductRepository;
import kassandrafalsitta.e_commerce_back.repositories.UsersRepository;
import kassandrafalsitta.e_commerce_back.tools.MailgunSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PasswordEncoder bcrypt;

    @Autowired
    private MailgunSender mailgunSender;


    public Page<User> findAll(int page, int size, String sortBy) {
        if (page > 100) page = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return this.usersRepository.findAll(pageable);
    }

    public User saveUser(UserDTO body) {
        this.usersRepository.findByEmail(body.email()).ifPresent(
                employee -> {
                    throw new BadRequestException("L'email " + body.email() + " è già in uso!");
                }
        );
        User employee = new User(body.username(), body.name(), body.surname(), body.email(),bcrypt.encode(body.password()));
        User savedUser = this.usersRepository.save(employee);

        // 4. Invio email conferma registrazione
        mailgunSender.sendRegistrationEmail(savedUser);
        return savedUser ;
    }

    public User findById(UUID employeeId) {
        return this.usersRepository.findById(employeeId).orElseThrow(() -> new NotFoundException(employeeId));
    }

    public User findByIdAndUpdate(UUID employeeId, UserDTO updatedUser) {
        User found = findById(employeeId);
        found.setUsername(updatedUser.username());
        found.setName(updatedUser.name());
        found.setSurname(updatedUser.surname());
        found.setEmail(updatedUser.email());
        found.setPassword(updatedUser.password());
        return this.usersRepository.save(found);
    }

    public User findByIdAndUpdateRole(UUID employeeId) {
        User found = findById(employeeId);
        found.setRole(Role.ADMIN);
        return this.usersRepository.save(found);
    }

    public User findByIdAndUpdateProductList(UUID employeeId, UserProductListDTO updatedProductListUser) {
        User found = findById(employeeId);
        List<UUID> productsId = new ArrayList<>();
        for (String id :updatedProductListUser.productId()) {
            try {
                productsId.add(UUID.fromString(id));
            } catch (NumberFormatException e) {
                throw new BadRequestException("L'UUID del prodotto" + id + " non è corretto");
            }
        }
        List<Product> products = productRepository.findAllById(productsId);
        found.setProductList(products);
        return this.usersRepository.save(found);
    }



    public void findByIdAndDelete(UUID employeeId) {
        this.usersRepository.delete(this.findById(employeeId));
    }


    public User findByEmail(String email) {
        return usersRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("L'utente con l'email " + email + " non è stato trovato!"));
    }
}
