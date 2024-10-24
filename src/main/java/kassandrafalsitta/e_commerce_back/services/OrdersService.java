package kassandrafalsitta.e_commerce_back.services;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import kassandrafalsitta.e_commerce_back.entities.CheckoutResponse;
import kassandrafalsitta.e_commerce_back.entities.Order;
import kassandrafalsitta.e_commerce_back.entities.Product;
import kassandrafalsitta.e_commerce_back.entities.User;
import kassandrafalsitta.e_commerce_back.enums.Status;
import kassandrafalsitta.e_commerce_back.exceptions.BadRequestException;
import kassandrafalsitta.e_commerce_back.exceptions.NotFoundException;
import kassandrafalsitta.e_commerce_back.payloads.OrderDTO;
import kassandrafalsitta.e_commerce_back.payloads.OrderIdRespDTO;
import kassandrafalsitta.e_commerce_back.payloads.SessionIdDTO;
import kassandrafalsitta.e_commerce_back.repositories.OrderRepository;
import kassandrafalsitta.e_commerce_back.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class OrdersService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UsersService usersService;

    @Autowired
    private ProductRepository productRepository;




    public Page<Order> findAll(int page, int size, String sortBy) {
        if (page > 100) page = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return this.orderRepository.findAll(pageable);
    }

    public CheckoutResponse saveOrder(OrderDTO body) {
        LocalDate dateAdded;
        try {
            dateAdded = LocalDate.parse(body.dateAdded());
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Il formato della data non è valido: " + body.dateAdded() + " inserire nel seguente formato: AAAA-MM-GG");
        }

        UUID userId;
        try {
            userId = UUID.fromString(body.userId());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("L'UUID del dipendente non è corretto");
        }

        Status status;
        try {
            status = Status.valueOf(body.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Lo stato non è corretto");
        }

        User user = usersService.findById(userId);


        List<UUID> productsId = new ArrayList<>();
        for (String id : body.productId()) {
            try {
                productsId.add(UUID.fromString(id));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("L'UUID del prodotto " + id + " non è corretto");
            }
        }

        List<Product> products = productRepository.findAllById(productsId);
        if (products.isEmpty()) {
            throw new BadRequestException("Nessun prodotto trovato con gli ID forniti");
        }

        Order order = new Order(dateAdded, status, user, products);
        order.setTotal(Double.parseDouble(body.total()));

        // Creazione della sessione di pagamento di Stripe
        try {
            SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:5173/success?sessionId={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost:5173/errore");
            for (Product product : products) {
                builder.addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount((long) (product.getPrice() * 100))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(product.getTitle())
                                                                .build())
                                                .build())
                                .setQuantity((long) product.getQuantity())
                                .build());
            }

            SessionCreateParams params = builder.build();
            Session session = Session.create(params);
            return new CheckoutResponse(session.getId(), order);
        } catch (StripeException e) {

            throw new BadRequestException("Errore durante la creazione della sessione di pagamento: " + e.getMessage());
        }
    }

    public Order saveOrderOnDatabase(SessionIdDTO body) {
        LocalDate dateAdded;
        try {
            dateAdded = LocalDate.parse(body.dateAdded());
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Il formato della data non è valido: " + body.dateAdded() + " inserire nel seguente formato: AAAA-MM-GG");
        }

        UUID userId;
        try {
            userId = UUID.fromString(body.userId());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("L'UUID del dipendente non è corretto");
        }

        Status status;
        try {
            status = Status.valueOf(body.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Lo stato non è corretto");
        }

        User user = usersService.findById(userId);


        List<UUID> productsId = new ArrayList<>();
        for (String id : body.productId()) {
            try {
                productsId.add(UUID.fromString(id));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("L'UUID del prodotto " + id + " non è corretto");
            }
        }

        List<Product> products = productRepository.findAllById(productsId);
        if (products.isEmpty()) {
            throw new BadRequestException("Nessun prodotto trovato con gli ID forniti");
        }

        Order order = new Order(dateAdded, status, user, products);
        order.setTotal(Double.parseDouble(body.total()));
        return this.orderRepository.save(order);
    };

    public boolean checkIfPaymentSucceeded(SessionIdDTO sessionId) {
        try {
            Session session = Session.retrieve(sessionId.sessionId());
            if ("paid".equals(session.getPaymentStatus())) {
                Optional<Order> order = orderRepository.findBySessionId(sessionId.sessionId());
                if (order.isPresent()) {
                    Order savedOrder = this.orderRepository.save(order.get());
                }
                return true;
            }
            return false;
        } catch (StripeException e) {
            throw new BadRequestException("Errore nel recupero dello stato del pagamento: " + e.getMessage());
        }
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

        found.setStatus(status);
        found.setDateAdded(dateAdded);
        found.setDateModified(LocalDate.now());
        List<UUID> productsId = new ArrayList<>();
        for (String id : updatedOrder.productId()) {
            try {
                productsId.add(UUID.fromString(id));
            } catch (NumberFormatException e) {
                throw new BadRequestException("L'UUID del prodotto" + id + " non è corretto");
            }
        }

        List<Product> products = productRepository.findAllById(productsId);
        double total = 0;
        for (Product product : products) {
            total += product.getPrice() * product.getQuantity();

        }
        found.setTotal(total);

        found.setProductList(products);

        return this.orderRepository.save(found);
    }


    public void findByIdAndDelete(UUID orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.clearProductList();
            orderRepository.save(order);
            orderRepository.delete(order);
        } else {
            throw new NotFoundException("Ordine non trovato");
        }
    }


    public List<Order> findByUser(User user) {
        return this.orderRepository.findByUser(user);
    }
}
