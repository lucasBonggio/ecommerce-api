package com.lmelectronica.ecommerce.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lmelectronica.ecommerce.product.Product;
import com.lmelectronica.ecommerce.product.ProductRepository;
import com.lmelectronica.ecommerce.productdetail.ProductDetail;
import com.lmelectronica.ecommerce.productdetail.ProductDetailRepository;
import com.lmelectronica.ecommerce.productdetail.ProductDetailService;
import com.lmelectronica.ecommerce.shared.dtos.ProductDetailDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateDetailRequest;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ProductDetailServiceTests {
    
    @InjectMocks
    private ProductDetailService productDetailService;
    
    @Mock
    private ProductDetailRepository pdRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    void createDetail_validData_returnDTO(){
        Long productId = 1L;

        
        ProductDetail detail =  new ProductDetail();
        detail.setKeyName("Memory RAM");
        detail.setDetails("16GB");


        ProductDetailDTO detailDTO =  new ProductDetailDTO();
        detailDTO.setKeyName("Memory RAM");
        detailDTO.setDetails("16GB");

        Product product = new Product();
        product.setName("Notebook Thinkpad");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(pdRepository.save(any(ProductDetail.class))).thenReturn(detail);

        ProductDetailDTO result = productDetailService.createDetail(detailDTO, productId);

        assertNotNull(result);
        assertEquals(detail.getKeyName(), result.getKeyName());
        assertEquals(detail.getDetails(), result.getDetails());

        verify(productRepository).findById(productId);
        verify(pdRepository).save(any(ProductDetail.class));
    }

    @Test
    void createDetail_productNotFound_returnException(){
        Long productId = 1L;

        ProductDetailDTO detailDTO = new ProductDetailDTO();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productDetailService.createDetail(detailDTO, productId));

        String messageExpected = String.format("Product with id '%s' not found. ", productId);

        assertEquals(ex.getMessage(), messageExpected);
    }

    @Test
    void getDetailByProductId_validData_returnListOfDTOs() {
        Long productId = 1L;

        ProductDetail detail1 = new ProductDetail();
        detail1.setId(10L);
        detail1.setDetails("Detail 1");
        detail1.setKeyName("Key 1");

        ProductDetail detail2 = new ProductDetail();
        detail2.setId(11L);
        detail2.setDetails("Detail 2");
        detail2.setKeyName("Key 2");

        List<ProductDetail> details = List.of(detail1, detail2);

        when(pdRepository.findByProductId(productId)).thenReturn(details);

        List<ProductDetailDTO> result = productDetailService.getDetailByProductId(productId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Detail 1", result.get(0).getDetails());
        assertEquals("Key 1", result.get(0).getKeyName());

        assertEquals("Detail 2", result.get(1).getDetails());
        assertEquals("Key 2", result.get(1).getKeyName());

        verify(pdRepository).findByProductId(productId);
    }
    
    @Test
    void getDetailByProductId_noDetailsForProductId_returnEmptyList() {
        Long productId = 1L; 

        when(pdRepository.findByProductId(productId)).thenReturn(Collections.emptyList());

        List<ProductDetailDTO> result = productDetailService.getDetailByProductId(productId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(pdRepository).findByProductId(productId);
    }
    
    @Test
    void updateDetail_validChanges(){
        Long detailId = 1L;

        ProductDetail detail =  new ProductDetail();
        detail.setKeyName("Memory RAM");
        detail.setDetails("16GB");


        UpdateDetailRequest request = new UpdateDetailRequest();
        request.setDetails("32GB");

        when(pdRepository.findById(detailId)).thenReturn(Optional.of(detail));

        productDetailService.updateDetail(detailId, request);

        assertEquals("32GB", detail.getDetails());
        assertEquals("Memory RAM", detail.getKeyName());
    }

    @Test
    void updateDetail_detailNotFound_returnException(){
        Long detailId = 1L;

        UpdateDetailRequest request = new UpdateDetailRequest();
        request.setDetails("32GB");

        when(pdRepository.findById(detailId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productDetailService.updateDetail(detailId, request));

        String messageExpected = String.format("ProductDetail with id '%s' not found. ", detailId);

        assertEquals(ex.getMessage(), messageExpected);
    }
    
    @Test
    void deleteDetail_validDelete(){
        Long detailId = 1L;

        ProductDetail detail =  new ProductDetail();
        detail.setKeyName("Memory RAM");
        detail.setDetails("16GB");

        when(pdRepository.findById(detailId)).thenReturn(Optional.of(detail));

        productDetailService.deleteDetail(detailId);

        verify(pdRepository).delete(detail);
    }
    
}
