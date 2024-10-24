package kassandrafalsitta.e_commerce_back.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import kassandrafalsitta.e_commerce_back.entities.Product;
import kassandrafalsitta.e_commerce_back.exceptions.BadRequestException;
import kassandrafalsitta.e_commerce_back.exceptions.NotFoundException;
import kassandrafalsitta.e_commerce_back.payloads.ProductDTO;
import kassandrafalsitta.e_commerce_back.payloads.ProductQuantityDTO;
import kassandrafalsitta.e_commerce_back.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductsService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private Cloudinary cloudinaryUploader;


    public Page<Product> findAll(int page, int size, String sortBy) {
        if (page > 100) page = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return this.productRepository.findAll(pageable);
    }

    public Product saveProduct(ProductDTO body) {

        Optional<Product> titleAndSubtitle = productRepository.findByTitleAndSubtitle(body.title(), body.subtitle());
        if (titleAndSubtitle.isPresent()) {
            throw new BadRequestException("Il titolo " + body.title() + " e il subtitolo " + body.subtitle() + " sono già in uso!");
        }


        Product product = new Product( body.title(), body.subtitle(), Double.parseDouble(body.price()), Integer.parseInt(body.stock()));

        return this.productRepository.save(product);
    }

    public Product findById(UUID productId) {
        return this.productRepository.findById(productId).orElseThrow(() -> new NotFoundException(productId));
    }

    public Product findByIdAndUpdate(UUID productId, ProductDTO updatedProduct) {
        Product found = findById(productId);

        found.setStock(Integer.parseInt(updatedProduct.stock()));
        found.setTitle(updatedProduct.title());
        found.setSubtitle(updatedProduct.subtitle());
        found.setPrice(Double.parseDouble(updatedProduct.price()));


        return this.productRepository.save(found);
    }
    public Product findByIdAndUpdateQuantity(UUID productId, ProductQuantityDTO updatedProduct) {
        Product found = findById(productId);
        found.setQuantity(updatedProduct.quantity());

        return this.productRepository.save(found);
    }

    public void findByIdAndDelete(UUID productId) {
        try {
            this.productRepository.delete(this.findById(productId));
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Attenzione, il prodotto è presente in uno o più ordini.");
        }
    }

    public Product uploadImage(UUID productId, MultipartFile file) throws IOException {
        Product found = findById(productId);
        String img = (String) cloudinaryUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("url");
        found.setImg(img);
        return this.productRepository.save(found);
    }

    //query
    public List<Product> findProductsBySubtitle(String subtitle) {
        return productRepository.findBySubtitleContainingIgnoreCase(subtitle);
    }


}
