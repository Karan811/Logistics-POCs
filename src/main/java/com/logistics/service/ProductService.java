package com.logistics.service;

import com.logistics.model.Product;
import com.logistics.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    @Autowired
    private ProductRepo repo;
    private final AlertEmitterService alertEmitterService;

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product getProductById(int id){
        return repo.findById(id).orElse(null);
    }

    public Product addProduct(Product product, MultipartFile imageFile) throws IOException {
        product.setImageName(imageFile.getOriginalFilename());
        product.setImageType(imageFile.getContentType());
        product.setImageDate(imageFile.getBytes());
        return repo.save(product);
    }

    public Product updateProduct(int id, Product product, MultipartFile imageFile) throws IOException {
        product.setImageDate(imageFile.getBytes());
        product.setImageName(imageFile.getOriginalFilename());
         product.getImageType();
        return repo.save(product);
    }

    public String updateStatus(Long packageId, String newStatus) {


        // Trigger alert only if status becomes 'lost'
        if (!"lost".equalsIgnoreCase("In Transit") && "lost".equalsIgnoreCase(newStatus)) {
            alertEmitterService.sendAlert("📦 Package ID " + packageId + " marked as LOST!");
        }

        return "Status updated to " + newStatus + " for package ID " + packageId;
    }

    public void deleteProduct(int id) {
        repo.deleteById(id);
    }

    public List<Product> searchProducts(String keyword) {
        return repo.searchProducts(keyword);
    }
}
