package kassandrafalsitta.e_commerce_back.services;

import kassandrafalsitta.e_commerce_back.entities.Order;
import kassandrafalsitta.e_commerce_back.entities.User;
import kassandrafalsitta.e_commerce_back.enums.Status;
import kassandrafalsitta.e_commerce_back.exceptions.BadRequestException;
import kassandrafalsitta.e_commerce_back.exceptions.NotFoundException;
import kassandrafalsitta.e_commerce_back.payloads.OrderDTO;
import kassandrafalsitta.e_commerce_back.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
public class OrdersService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UsersService usersService;

    public Page<Order> findAll(int page, int size, String sortBy) {
        if (page > 100) page = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return this.orderRepository.findAll(pageable);
    }

    public Order saveOrder(OrderDTO body) {

        LocalDate dateAdded = null;
        try {
            dateAdded = LocalDate.parse(body.dateAdded());
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Il formato della data non è valido: " + body.dateAdded() + " inserire nel seguente formato: AAAA-MM-GG");
        }


        UUID userId = null;
        try {
            userId = UUID.fromString(body.userId());
        } catch (NumberFormatException e) {
            throw new BadRequestException("L'UUID del dipendente non è corretto");
        }
        Status status = null;
        try {
            status = Status.valueOf(body.status().toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Lo stato non è corretto");
        }

        User user = usersService.findById(userId);

        Order product = new Order(dateAdded, status, Double.parseDouble(body.total()), user);

        return this.orderRepository.save(product);
    }

    public Order findById(UUID productId) {
        return this.orderRepository.findById(productId).orElseThrow(() -> new NotFoundException(productId));
    }

    public Order findByIdAndUpdate(UUID productId, OrderDTO updatedOrder) {
        Order found = findById(productId);

        LocalDate dateAdded = null;
        try {
            dateAdded = LocalDate.parse(updatedOrder.dateAdded());
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Il formato della data non è valido: " + updatedOrder.dateAdded() + " inserire nel seguente formato: AAAA-MM-GG");
        }


        UUID userId = null;
        try {
            userId = UUID.fromString(updatedOrder.userId());
        } catch (NumberFormatException e) {
            throw new BadRequestException("L'UUID del dipendente non è corretto");
        }
        Status status = null;
        try {
            status = Status.valueOf(updatedOrder.status().toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Lo stato non è corretto");
        }

        User user = usersService.findById(userId);


        found.setUser(user);
        found.setTotal(Double.parseDouble(updatedOrder.total()));
        found.setStatus(status);
        found.setDateAdded(dateAdded);
        found.setDateModified(LocalDate.now());


        return this.orderRepository.save(found);
    }

    public void findByIdAndDelete(UUID productId) {
        this.orderRepository.delete(this.findById(productId));
    }


    public List<Order> findByUser(User user) {
        return this.orderRepository.findByUser(user);
    }
}
