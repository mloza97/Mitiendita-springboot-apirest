package com.mloza.springboot.backend.apirest.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mloza.springboot.backend.apirest.dao.IProductoDAO;
import com.mloza.springboot.backend.apirest.models.Producto;

@Service
public class ProductoServiceImpl implements IProductoService{

	@Autowired
	private IProductoDAO productoDAO;
	
	@Override
	public List<Producto> findAll() {
		return (List<Producto>) productoDAO.findAll();
	}

	@Override
	public Producto findById(Long id) {
		return productoDAO.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		productoDAO.deleteById(id);
	}

	@Override
	@Transactional
	public Producto save(Producto producto) {
		return productoDAO.save(producto);
	}
	
}
