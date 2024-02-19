package com.example.JwtOauth2.repository;

import com.example.JwtOauth2.Entities.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProducts extends JpaRepository<Products, Integer> {
}
