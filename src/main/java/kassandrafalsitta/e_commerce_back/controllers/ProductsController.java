package kassandrafalsitta.e_commerce_back.controllers;

import kassandrafalsitta.e_commerce_back.entities.Product;
import kassandrafalsitta.e_commerce_back.exceptions.BadRequestException;
import kassandrafalsitta.e_commerce_back.payloads.ProductDTO;
import kassandrafalsitta.e_commerce_back.payloads.ProductRespDTO;
import kassandrafalsitta.e_commerce_back.services.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductsService productService;

    @GetMapping
    public Page<Product> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "id") String sortBy) {
        return this.productService.findAll(page, size, sortBy);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductRespDTO createProduct(@RequestBody @Validated ProductDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            String messages = validationResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(". "));
            throw new BadRequestException("Ci sono stati errori nel payload. " + messages);
        } else {
            return new ProductRespDTO(this.productService.saveProduct(body).getId());
        }
    }

    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable UUID productId) {
        return productService.findById(productId);
    }

    @PutMapping("/{productId}")
    public Product findProductByIdAndUpdate(@PathVariable UUID productId, @RequestBody @Validated ProductDTO body) {
        return productService.findByIdAndUpdate(productId, body);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findProductByIdAndDelete(@PathVariable UUID productId) {
        productService.findByIdAndDelete(productId);
    }

    @PostMapping("/{productId}/img")
    public Product uploadCover(@PathVariable UUID productId, @RequestParam(name = "img", required = false) MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Ci sono stati errori nel payload dell'immagine ");
        }
        return this.productService.uploadImage(productId, image);
    }
}
