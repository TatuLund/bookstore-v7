package com.example.myapplication.samples.backend.mock;

import java.util.List;

import com.example.myapplication.samples.backend.DataService;
import com.example.myapplication.samples.backend.data.Category;
import com.example.myapplication.samples.backend.data.Product;

/**
 * Mock data model. This implementation has very simplistic locking and does not
 * notify users of modifications. There are mocked delays to simulate real
 * database response times.
 */
public class MockDataService extends DataService {

    private static MockDataService INSTANCE;

    private List<Product> products;
    private List<Category> categories;
    private int nextProductId = 0;

    private MockDataService() {
        categories = MockDataGenerator.createCategories();
        products = MockDataGenerator.createProducts(categories);
        nextProductId = products.size() + 1;
    }

    public synchronized static DataService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MockDataService();
        }
        return INSTANCE;
    }

    @Override
    public synchronized List<Product> getAllProducts() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        return products;
    }

    @Override
    public synchronized List<Category> getAllCategories() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        return categories;
    }

    @Override
    public synchronized void updateProduct(Product p) {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
        }
        if (p.getId() < 0) {
            // New product
            p.setId(nextProductId++);
            products.add(p);
            return;
        }
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == p.getId()) {
                products.set(i, p);
                return;
            }
        }

        throw new IllegalArgumentException(
                "No product with id " + p.getId() + " found");
    }

    @Override
    public synchronized Product getProductById(int productId) {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
        }
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == productId) {
                return products.get(i);
            }
        }
        return null;
    }

    @Override
    public synchronized void deleteProduct(int productId) {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
        }
        Product p = getProductById(productId);
        if (p == null) {
            throw new IllegalArgumentException(
                    "Product with id " + productId + " not found");
        }
        products.remove(p);
    }
}
