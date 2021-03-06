package com.mloza.springboot.backend.apirest.dao;

import org.springframework.data.repository.CrudRepository;

import com.mloza.springboot.backend.apirest.models.Producto;

public interface IProductoDAO extends CrudRepository<Producto, Long> {

}
