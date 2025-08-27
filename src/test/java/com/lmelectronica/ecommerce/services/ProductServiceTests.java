package com.lmelectronica.ecommerce.services;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.lmelectronica.ecommerce.product.Product;
import com.lmelectronica.ecommerce.product.ProductRepository;
import com.lmelectronica.ecommerce.product.ProductService;
import com.lmelectronica.ecommerce.shared.dtos.ProductDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateProductRequest;
import com.lmelectronica.ecommerce.shared.exceptions.BusinessRuleException;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;
    
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ProductRepository productRepository;

    @Test
    void createProduct_validData_returnProductDTO(){
        Product product = new Product();
        product.setName("Samsung Essential S3");
        product.setPrice(179.99);
        product.setStock(1);
        product.setDescription("27 Inches");

        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Samsung Essential S3");
        productDTO.setPrice(179.99);
        productDTO.setStock(1);
        productDTO.setDescription("27 Inches");

        when(productRepository.existsByName("Samsung Essential S3")).thenReturn(false);
        when(modelMapper.map(any(ProductDTO.class), eq(Product.class))).thenReturn(product);

        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);
        
        ProductDTO result = productService.createProduct(productDTO);

        assertEquals("Samsung Essential S3", result.getName());
        assertEquals(179.99, result.getPrice());
        assertEquals(1, result.getStock());
        assertEquals("27 Inches", result.getDescription());
    }

    @Test
    void createProduct_existingName_throwException(){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Samsung Essential S3");
        productDTO.setPrice(179.99);
        productDTO.setDescription("27 Inches");

        when(productRepository.existsByName("Samsung Essential S3")).thenReturn(true);
        
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> productService.createProduct(productDTO));

        String expectedMessage = String.format("Product with Name '%s' already exists. ", productDTO.getName());


        assertEquals(ex.getMessage(), expectedMessage);
    }

    @Test
    void getProductById_validData_returnProductDTO(){
        Long id = 1L;

        Product product = new Product();
        product.setName("Samsung Essential S3");
        product.setPrice(179.99);
        product.setDescription("27 Inches");

        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Samsung Essential S3");
        productDTO.setPrice(179.99);
        productDTO.setDescription("27 Inches");

        when(productRepository.findById(id)).thenReturn((Optional.of(product)));
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);
        
        ProductDTO result = productService.getProductById(id);

        assertEquals("Samsung Essential S3", result.getName());
        assertEquals(179.99, result.getPrice());
        assertEquals("27 Inches", result.getDescription());
    }

    @Test
    void getProductById_idNotFound_throwException() {
        Long id = 1L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(id));
    
        String expectedMessage = String.format("Product with id '%s' not found. ", id);

        assertEquals(ex.getMessage(), expectedMessage);
        
    }

    @Test
    void getProductByName_validData_returnProductDTO(){
        String name = "Deco";

        Product product = new Product();
        product.setName("Deco");
        product.setPrice(254.99);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Deco");
        productDTO.setPrice(254.99);

        when(productRepository.findByName(name)).thenReturn(Optional.of(product));
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        ProductDTO result = productService.getProductByName(name); 
        assertEquals("Deco", result.getName());
        assertEquals(254.99, result.getPrice());
    }

    @Test
    void getProductByName_nameNotExists_returnProductByNameException(){
        String name = "Deco";

        when(productRepository.findByName(name)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.getProductByName(name));

        String messageExpected = String.format("Product with id '%s' not found. ", name);

        assertEquals(ex.getMessage(), messageExpected);
    }

    @Test
    void getAllProducts_validData_returnPageProductDTO(){
        Product product1 = new Product();
        product1.setName("Samsung Essential S3");
        product1.setPrice(149.99);
        product1.setStock(5);
        product1.setDescription("32 Inches");

        Product product2 = new Product();
        product2.setName("Monitor Deco");
        product2.setPrice(243.99);
        product2.setStock(20);
        product2.setDescription("27 Inches");

        List<Product> productList = List.of(product1, product2);

        ProductDTO dto1 = new ProductDTO();
        dto1.setName("Samsung Essential E3");
        dto1.setPrice(149.99);
        dto1.setStock(5);
        dto1.setDescription("32 Inches");

        ProductDTO dto2 = new ProductDTO();
        dto2.setName("Monitor Deco");
        dto2.setPrice(243.99);
        dto2.setStock(20);
        dto2.setDescription("27 Inches");

        Sort sort = Sort.by("id").ascending();
        PageRequest pageable = PageRequest.of(0, 10, sort);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(modelMapper.map(product1, ProductDTO.class)).thenReturn(dto1);
        when(modelMapper.map(product2, ProductDTO.class)).thenReturn(dto2);

        Page<ProductDTO> result = productService.getAllProducts(0, 10, sort);

        assertNotNull(result);
        assertThat(result.getContent())
                    .hasSize(2)
                    .extracting("name")
                    .contains("Samsung Essential E3", "Monitor Deco");
    }
    
    @Test
    void getAllProducts_emptyList_returnEmptyPage() {
        Sort sort = Sort.by("id").ascending();
        PageRequest pageable = PageRequest.of(0, 10, sort);
        Page<Product> productPage = Page.empty();


        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<ProductDTO> result = productService.getAllProducts(0, 10, sort);

        assertNotNull(result);
        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    void updateProduct_validChanges(){
        Long id = 1L;

        UpdateProductRequest request = new UpdateProductRequest();
        request.setDescription("27 Inches");
        request.setStock(25);
        
        Product existingProduct = new Product();
        existingProduct.setName("Samsung Essential E3");
        existingProduct.setPrice(149.99);
        existingProduct.setStock(29);

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        
        productService.updateProduct(id, request);

        assertEquals("27 Inches", existingProduct.getDescription());
        assertEquals(25, existingProduct.getStock());
        
        verify(productRepository).save(existingProduct);
    }

    @Test
    void updateProduct_idNotFound_throwsProductByIdException() {
        Long id = 999L;
        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Monitor Deco");

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(id));

        String messageExpected = String.format("Product with id '%s' not found. ", id);

        assertEquals(ex.getMessage(), messageExpected);
    }

    @Test
    void deleteProduct_verifyDeleting(){
        Long id = 1L;
        Product product = new Product();
        product.setName("Monitor Deco");
        product.setPrice(243.99);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        productService.deleteProduct(id);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_idNotFound_returnProductByIdException(){
        Long id = 1L;
        Product product = new Product();
        product.setName("Monitor Deco");
        product.setPrice(243.99);

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(id));

        String messageExpected = String.format("Product with id '%s' not found. ", id);

        assertEquals(ex.getMessage(), messageExpected);
    }
}

