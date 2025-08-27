package com.lmelectronica.ecommerce.product;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.lmelectronica.ecommerce.shared.dtos.ProductDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateProductRequest;
import com.lmelectronica.ecommerce.shared.exceptions.BusinessRuleException;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    public ProductDTO createProduct(ProductDTO productDTO){
        if(productRepository.existsByName(productDTO.getName())){
            throw BusinessRuleException.duplicateResource("Product", "Name", productDTO.getName());
        } 
        Product product = modelMapper.map(productDTO, Product.class); 

        Product newProduct = productRepository.save(product);

        return modelMapper.map(newProduct, ProductDTO.class);
    }

    public ProductDTO getProductById(Long id){
        Product productFound = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        return modelMapper.map(productFound, ProductDTO.class);
    }

    public ProductDTO getProductByName(String name){
        Product productFound = productRepository.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("Product", name));
        
            return modelMapper.map(productFound, ProductDTO.class);
    }

    public Page<ProductDTO> getAllProducts(int page, int size, Sort sort){
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productRepository.findAll(pageable);

        return products.map(product -> modelMapper.map(product, ProductDTO.class));
    }
    
    public void updateProduct(Long id, UpdateProductRequest request){
        Product productFound = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        
        if (request.getName() != null) {
            productFound.setName(request.getName());
        }

        if (request.getPrice() != null && request.getPrice() >= 0) {
            productFound.setPrice(request.getPrice());
        }

        if (request.getStock() != null && request.getStock() >= 0) {
            productFound.setStock(request.getStock());
        }

        if (request.getDescription() != null) {
            productFound.setDescription(request.getDescription());
        }

        productFound.setUpdatedAt(new Date());

        productRepository.save(productFound);
    }

    public void deleteProduct(Long id){
        Product productFound = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        
        productRepository.delete(productFound);
        
    }
} 
