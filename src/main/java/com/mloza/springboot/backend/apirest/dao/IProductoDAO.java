package com.mloza.springboot.backend.apirest.dao;

import org.springframework.data.repository.CrudRepository;

import com.mloza.springboot.backend.apirest.models.Producto;

/**
 * Interfaz Producto Dao
 * Persistencia a BD
 * Tipo JPA | Hibernate
 * @author daniel-loza
 *
 */
public interface IProductoDAO extends CrudRepository<Producto, Long> {

}
