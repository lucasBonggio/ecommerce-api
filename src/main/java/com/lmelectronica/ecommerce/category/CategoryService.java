package com.lmelectronica.ecommerce.category;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.lmelectronica.ecommerce.shared.dtos.CategoryDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateCategoryRequest;
import com.lmelectronica.ecommerce.shared.exceptions.BusinessRuleException;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;
    
    public CategoryDTO createCategory(CategoryDTO categoryDTO){
        if(categoryRepository.existsByName(categoryDTO.getName())){
            throw BusinessRuleException.duplicateResource("Category", "name", categoryDTO.getName());
        }
        Category category = modelMapper.map(categoryDTO, Category.class);

        Category newCategory = categoryRepository.save(category);
        return modelMapper.map(newCategory, CategoryDTO.class);
    } 

    public CategoryDTO getCategoryById(Long id){
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        
        return modelMapper.map(category, CategoryDTO.class);
    }

    public CategoryDTO getCategoryByName(String name){
        Category category = categoryRepository.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("Category", name));
        return modelMapper.map(category, CategoryDTO.class);
    }
    
    public Page<CategoryDTO> getAllCategories(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categories = categoryRepository.findAll(pageable);

        return categories.map(category -> modelMapper.map(category, CategoryDTO.class));
    }
    
    public void updateCategory(Long id, UpdateCategoryRequest request){
        Category categoryFound = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        if(request.getName() != null){
            boolean existName= categoryRepository.existsByName(request.getName());
            if(existName){
                throw BusinessRuleException.duplicateResource("Category", "name", request.getName());
            }
            categoryFound.setName(request.getName());
        }
        
        if(request.getDescription() != null){
            categoryFound.setDescription(request.getDescription());
        }

        categoryRepository.save(categoryFound);
    }

    public void deleteCategory(Long id){
        Category categoryFound = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", id));
            
        categoryRepository.delete(categoryFound);
    }
}
