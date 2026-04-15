package lk.ghanim.api.service;

import lk.ghanim.api.entity.Category;
import lk.ghanim.api.exception.ResourceNotFoundException;
import lk.ghanim.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findByActiveTrue();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id
                ));
    }

    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with slug: " + slug
                ));
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category updated) {
        Category existing = getCategoryById(id);
        existing.setName(updated.getName());
        existing.setSlug(updated.getSlug());
        existing.setDescription(updated.getDescription());
        existing.setEmoji(updated.getEmoji());
        existing.setActive(updated.isActive());
        return categoryRepository.save(existing);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        category.setActive(false);
        categoryRepository.save(category);
    }
}
