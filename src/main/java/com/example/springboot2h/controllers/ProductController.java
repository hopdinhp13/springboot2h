package com.example.springboot2h.controllers;

import com.example.springboot2h.models.Product;
import com.example.springboot2h.models.ResponseObject;
import com.example.springboot2h.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/Products")
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @GetMapping("")
    List<Product> getAllProducts() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        Optional<Product> foundProduct = repository.findById(id);
        return foundProduct.isPresent() ? ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Query product successfull", foundProduct)
        ) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObject("false", "Cannot find product with id = " + id, ""
                ));

    }

    @PostMapping("/insert")
    ResponseEntity<ResponseObject> insertProduct(@RequestBody Product newProduct){
        List<Product> foundProducts = repository.findByProductName(newProduct.getProductName().trim());
        if(foundProducts.size() > 0){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed", "product name already taker", "")
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Insert Product successfully", repository.save(newProduct))
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ResponseObject> updateProduct(@RequestBody Product newProduct, @PathVariable Long id){
        Product updatedProduct = repository.findById(id)
                .map(product -> {
                    product.setProductName(newProduct.getProductName());
                    product.setYear(newProduct.getYear());
                    product.setPrice(newProduct.getPrice());
                    return repository.save(product);
                }).orElseGet(()-> {
                    newProduct.setId(id);
                    return  repository.save(newProduct);
                });
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Insert Product successfully", updatedProduct)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> deleteProduct(@PathVariable Long id){
        boolean exists = repository.existsById(id);
        if(exists){
            repository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Delete Product successfully", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObject("failed", "cannot find product", "")
        );
    }
}



