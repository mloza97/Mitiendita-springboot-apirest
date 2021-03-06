package com.mloza.springboot.backend.apirest.service;

import java.util.List;

import com.mloza.springboot.backend.apirest.models.Producto;

public interface IProductoService {
	public List<Producto> findAll();
	public Producto findById(Long id);
	public void delete(Long id);
	public Producto save(Producto producto);
}
