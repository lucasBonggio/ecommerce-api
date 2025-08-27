package com.lmelectronica.ecommerce.productdetail;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.lmelectronica.ecommerce.product.Product;
import com.lmelectronica.ecommerce.product.ProductRepository;
import com.lmelectronica.ecommerce.shared.dtos.ProductDetailDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateDetailRequest;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductDetailService {

    private final ProductDetailRepository pdRepository;

    private final ProductRepository productRepository;

    public ProductDetailDTO createDetail(ProductDetailDTO productDetailDTO, Long productId){
        Product productExisting = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        
        ProductDetail detail = new ProductDetail();
        detail.setDetails(productDetailDTO.getDetails());
        detail.setKeyName(productDetailDTO.getKeyName());
        detail.setProduct(productExisting);

        ProductDetail detailSaved = pdRepository.save(detail);

        return mapProductDetailDTO(detailSaved);
    }

    public List<ProductDetailDTO> getDetailByProductId(Long productId){
        List<ProductDetail> details = pdRepository.findByProductId(productId);
        
        return details.stream()
                        .map(this::mapProductDetailDTO)
                        .collect(Collectors.toList());
    }

    public void updateDetail(Long detailId, UpdateDetailRequest request){
        ProductDetail detail = pdRepository.findById(detailId)
            .orElseThrow(() -> new ResourceNotFoundException("ProductDetail", detailId));

        if(request.getDetails() != null){
            detail.setDetails(request.getDetails());
        }
        if(request.getKeyName() != null){
            detail.setKeyName(request.getKeyName());
        }

        pdRepository.save(detail);
    }

    public void deleteDetail(Long detailId){
        ProductDetail detail = pdRepository.findById(detailId)
            .orElseThrow(() -> new ResourceNotFoundException("ProductDetail", detailId));
        
        pdRepository.delete(detail);
    }

    public ProductDetailDTO mapProductDetailDTO(ProductDetail detail){
        ProductDetailDTO pd = new ProductDetailDTO();
        pd.setDetails(detail.getDetails());
        pd.setKeyName(detail.getKeyName());

        return pd;
    }

}
