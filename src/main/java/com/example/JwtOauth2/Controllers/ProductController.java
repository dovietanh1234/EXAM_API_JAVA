package com.example.JwtOauth2.Controllers;

import com.example.JwtOauth2.Entities.Products;
import com.example.JwtOauth2.Model.ProductModel;
import com.example.JwtOauth2.repository.IProducts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v3")
public class ProductController {

    @Autowired
    private IProducts _iproducts;

    @GetMapping("/products")
    public List<Products> getAllP(){
      //  List<Products> listP = new ArrayList<>();
        try {
            return _iproducts.findAll();
        }catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    @PostMapping("/post/product")
    public String postP(ProductModel p){
        try {
            Products pro = new Products();
            pro.setName(p.getName());
            pro.setPrice(p.getPrice());
            pro.setDescription(p.getDescription());
            pro.setThumbnail(p.getThumbnail());
            _iproducts.save(pro);
            return "add data success";
        }catch (Exception ex){
            return "error! occured error";
        }
    }

    @DeleteMapping("/delete/product")
    public String deleteP(int id){
        try{
            _iproducts.deleteById(id);
            return "delete successfully";
        }catch (Exception ex){
            return "error! has error occured";
        }
    }

    @PutMapping("/update/product")
    public String updateP(Products p){
        try{

            Products p1 =  _iproducts.findById(p.getId()).get();
            p1.setName(p.getName());
            p1.setPrice(p.getPrice());
            p1.setDescription(p.getDescription());
            p1.setThumbnail(p.getThumbnail());

            _iproducts.save(p1);


            return "update success";
        }catch (Exception ex){
            return "error! has error occured";
        }
    }

}
