package com.example.myapplication.samples.crud;

import com.example.myapplication.MyUI;
import com.example.myapplication.samples.backend.DataService;
import com.example.myapplication.samples.backend.data.Category;
import com.example.myapplication.samples.backend.data.Product;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vaadin.server.Page;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class SampleCrudPresenter implements Serializable {

    private SampleCrudView view;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private Collection<Product> products;

    public SampleCrudPresenter(SampleCrudView simpleCrudView) {
        view = simpleCrudView;
    }

    public CompletableFuture<Collection<Product>> loadProductsAsync() {
        return CompletableFuture.supplyAsync(
                () -> DataService.get().getAllProducts(), executor);
    }

    public CompletableFuture<Collection<Category>> loadCategoriesAsync() {
        return CompletableFuture.supplyAsync(
                () -> DataService.get().getAllCategories(), executor);
    }

    public void updateProduct(Product product) {
        DataService.get().updateProduct(product);
    }

    public void init() {
        editProduct(null);
        // Hide and disable if not admin
        if (!MyUI.get().getAccessControl().isUserInRole("admin")) {
            view.setNewProductEnabled(false);
        }

        loadProductsAsync().thenAccept(prod -> {
            view.showProductsAsync(prod);
        });
    }

    public void cancelProduct() {
        setFragmentParameter("");
        view.clearSelection();
        view.editProduct(null);
    }

    /**
     * Update the fragment without causing navigator to change view
     */
    private void setFragmentParameter(String productId) {
        String fragmentParameter;
        if (productId == null || productId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = productId;
        }

        Page page = MyUI.get().getPage();
        page.setUriFragment(
                "!" + SampleCrudView.VIEW_NAME + "/" + fragmentParameter,
                false);
    }

    public void enter(String productId) {
        if (productId != null && !productId.isEmpty()) {
            if (productId.equals("new")) {
                newProduct();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    int pid = Integer.parseInt(productId);
                    Product product = findProduct(pid);
                    view.selectRow(product);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private Product findProduct(int productId) {
        return DataService.get().getProductById(productId);
    }

    public void saveProduct(Product product) {
        view.showSaveNotification(product.getProductName() + " ("
                + product.getId() + ") updated");
        view.clearSelection();
        view.editProduct(null);
        view.refreshProduct(product);
        setFragmentParameter("");
    }

    public void deleteProduct(Product product) {
        DataService.get().deleteProduct(product.getId());
        view.showSaveNotification(product.getProductName() + " ("
                + product.getId() + ") removed");

        view.clearSelection();
        view.editProduct(null);
        view.removeProduct(product);
        setFragmentParameter("");
    }

    public void editProduct(Product product) {
        if (product == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(product.getId() + "");
        }
        view.editProduct(product);
    }

    public void newProduct() {
        view.clearSelection();
        setFragmentParameter("new");
        view.editProduct(new Product());
    }

    public void rowSelected(Product product) {
        if (MyUI.get().getAccessControl().isUserInRole("admin")) {
            view.editProduct(product);
        }
    }
}
