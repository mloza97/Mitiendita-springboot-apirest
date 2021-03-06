package com.mloza.springboot.backend.apirest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mloza.springboot.backend.apirest.models.Producto;
import com.mloza.springboot.backend.apirest.service.IProductoService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class ProductoRestController {

	@Autowired
	private IProductoService productoService;
	
	@GetMapping("/productos")
	public List<Producto> index(){
		return productoService.findAll();
	}
	
	@GetMapping("/productos/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Producto show(@PathVariable Long id) {
		return productoService.findById(id);
	}
	
	@PostMapping("/productos")
	@ResponseStatus(HttpStatus.CREATED)
	public Producto create(@RequestBody Producto producto) {
		return productoService.save(producto);
	}
	
	@PutMapping("/productos/{id}")
	public Producto update(@RequestBody Producto producto, @PathVariable Long id) {
		Producto productoActual = productoService.findById(id);
		
		productoActual.setNombre(producto.getNombre());
		productoActual.setDescripcion(producto.getDescripcion());
		productoActual.setPrecio(producto.getPrecio());
		
		return productoService.save(productoActual);
	}
	
	@DeleteMapping("/productos/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		productoService.delete(id);
	}
}
