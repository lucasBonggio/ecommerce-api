package com.lmelectronica.ecommerce.services;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.data.domain.Pageable;

import com.lmelectronica.ecommerce.category.Category;
import com.lmelectronica.ecommerce.category.CategoryRepository;
import com.lmelectronica.ecommerce.category.CategoryService;
import com.lmelectronica.ecommerce.shared.dtos.CategoryDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateCategoryRequest;
import com.lmelectronica.ecommerce.shared.exceptions.BusinessRuleException;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void createCategory_validData_returnCategoryDTO(){
        Category category = new Category();
        category.setName("Technology");
        category.setDescription("All the technology, All the technologies. ");

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Technology");
        categoryDTO.setDescription("All the technology, All the technologies. ");

        when(categoryRepository.existsByName("Technology")).thenReturn(false);
        when(modelMapper.map(any(CategoryDTO.class), eq(Category.class))).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.createCategory(categoryDTO);

        assertEquals("Technology", result.getName());
        assertEquals("All the technology, All the technologies. ", result.getDescription());
    }

    @Test
    void createCategory_existsName_throwException(){
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Technology");

        when(categoryRepository.existsByName("Technology")).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> categoryService.createCategory(categoryDTO));

        String messageExpected = String.format("Category with name '%s' already exists. ", categoryDTO.getName());

        assertEquals(ex.getMessage(), messageExpected);
    }

    @Test
    void getCategoryById_validData_returnCategoryDTO(){
        Long id = 1L;

        Category category = new Category();
        category.setId(id);
        category.setName("Technologies");
        category.setDescription("All technologies of the world. ");

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Technologies");
        categoryDTO.setDescription("All technologies of the world. ");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        CategoryDTO categoryFound = categoryService.getCategoryById(id);

        assertEquals("Technologies", categoryFound.getName());
        assertEquals("All technologies of the world. ", categoryFound.getDescription());
    }

    @Test
    void getCategoryById_categoryNotExists_throwException(){
        Long id = 1L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());


        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(id));

        String messageExpected = String.format("Category with id '%s' not found. ", id);

        assertEquals(ex.getMessage(), messageExpected);
    }
    
    @Test
    void getProductByName_validData_returnDTO(){
        String name = "Technology";

        Category category = new Category();
        category.setName("Technology");
        
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Technology");
        
        when(categoryRepository.findByName(name)).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.getCategoryByName(name);

        assertEquals("Technology", result.getName());
    }

    @Test
    void getCategoryByName_nameNotExists_throwException(){
        String name = "Technology";

        when(categoryRepository.findByName(name)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryByName(name));

        String messageExpected = String.format("Category with id '%s' not found. ", name);

        assertEquals(ex.getMessage(), messageExpected);
    }

    @Test
    void getAllCategories_validData_returnPageCategoryDTO(){
        Category category = new Category();
        category.setName("Technology");

        Category category2 = new Category();
        category2.setName("Cables");

        List<Category> categories = List.of(category, category2);
        
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Technology");

        CategoryDTO categoryDTO2 = new CategoryDTO();
        categoryDTO2.setName("Cables");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> productPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(productPage);
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn((categoryDTO));
        when(modelMapper.map(category2, CategoryDTO.class)).thenReturn((categoryDTO2));

        Page<CategoryDTO> result = categoryService.getAllCategories(0, 10);

        assertThat(result.getContent())
                    .hasSize(2)
                    .extracting("name")
                    .contains("Technology", "Cables");
    };

    @Test
    void getAllCategories_emptyList_returnEmptyPage(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> emptyPage = Page.empty(pageable);


        when(categoryRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<CategoryDTO> result = categoryService.getAllCategories(0, 10);

        assertTrue(result.isEmpty());
        
        assertThat(result.getContent())
                    .hasSize(0);
    }

    @Test
    void updateCategory_validChanges(){
        Long id = 1L;

        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("Technologies");
        request.setDescription("All technology of the world. ");

        Category existingCategory = new Category();
        existingCategory.setName("Technology");
        
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));

        categoryService.updateCategory(id, request);

        assertEquals("Technologies", existingCategory.getName());
        assertEquals("All technology of the world. ", existingCategory.getDescription());

        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void updateCategory_idNotFound_throwException(){
        Long id = 1L;

        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("Cables");

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());
        
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(id, request));

        String messageExpected = String.format("Category with id '%s' not found. ", id);

        assertEquals(ex.getMessage(), messageExpected);
    }

    @Test
    void deleteCategory_verifyDeleting(){
        Long id = 1L;

        Category category = new Category();
        category.setName("Cables");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(id);

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_idNotFound_throwException(){
        Long id = 1L;

        Category category = new Category();
        category.setName("Cables");

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(id));

        String messageExpected = String.format("Category with id '%s' not found. ", id);

        assertEquals(ex.getMessage(), messageExpected);
    }
}
