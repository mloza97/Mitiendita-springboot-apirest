package com.mloza.springboot.backend.apirest.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mloza.springboot.backend.apirest.models.Producto;
import com.mloza.springboot.backend.apirest.service.IProductoService;
import com.mloza.springboot.backend.apirest.service.IUploadFileService;

@CrossOrigin(origins = {"http://localhost:4200","*"})
@RestController
@RequestMapping("/api")
public class ProductoRestController {

	@Autowired
	private IProductoService productoService;
	
	@Autowired
	private IUploadFileService uploadService;
	
	private final Logger log = org.slf4j.LoggerFactory.getLogger(ProductoRestController.class);
	
	@GetMapping("/productos")
	public List<Producto> index(){
		return productoService.findAll();
	}
	
	@GetMapping("/productos/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> show(@PathVariable Long id) {
		Producto producto = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			producto = productoService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al hacer la peticion con el servidor, intentelo de nuevo más tarde.");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (producto == null) {
			response.put("mensaje", "El producto ID: ".concat(id.toString()).concat(" no se encuentra registrado."));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Producto>(producto, HttpStatus.OK);
	}
	
	@PostMapping("/productos")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> create(@Valid @RequestBody Producto producto, BindingResult result) {
		
		Producto productoNew = null;
		Map<String, Object> response = new HashMap<>();
		
		////////////////////Validation///////////////////////////////////////////////
		if (result.hasErrors()) {
		
		List<String> errors = result.getFieldErrors().stream()
				.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
				.collect(Collectors.toList());
		
		response.put("errors", errors);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		////////////////////////////////////////////////////////////////////////////////////

		try {
			productoNew = productoService.save(producto);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al hacer el insert en la base de datos, intentelo de nuevo más tarde.");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El producto ha sido creado con exito.");
		response.put("producto", productoNew);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping("/productos/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Producto producto, BindingResult result, @PathVariable Long id) {
		Producto productoUpdate = null;
		Producto productoActual = null;

		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			List<String> lErrors = result.getFieldErrors().stream()
					.map(error -> "El campo '" + error.getField() + "' " + error.getDefaultMessage())
					.collect(Collectors.toList());

			response.put("errors", lErrors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}


		try {
			productoActual = productoService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al hacer la peticion con el servidor, intentelo de nuevo más tarde.");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (productoActual == null) {
			response.put("mensaje", "No es posible editar el producto ID: ".concat(id.toString())
					.concat(" porque no se encuentra registrado."));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			productoActual.setNombre(producto.getNombre());
			productoActual.setDescripcion(producto.getDescripcion());
			productoActual.setPrecio(producto.getPrecio());

			productoUpdate = productoService.save(productoActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al hacer el update en la base de datos, intentelo de nuevo más tarde.");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El producto ha sido actualizado con exito.");
		response.put("cliente", productoUpdate);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		
	}
	
	@DeleteMapping("/productos/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		Map<String, Object> response = new HashMap<>();

		try {
			productoService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje",
					"Error al hacer la peticion de eliminar con el servidor, intentelo de nuevo más tarde.");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El producto se ha eliminado con exito.");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		
	}
	
	@PostMapping("/productos/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Producto producto = productoService.findById(id);

		if (!archivo.isEmpty()) {
			String nombreArchivo = null;
			try {
				nombreArchivo = uploadService.copiar(archivo);
			} catch (IOException e) {
				response.put("mensaje", "Error al subir el archivo, intentelo de nuevo más tarde.");
				response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			String nombreFotoAnterior = producto.getFoto();
			uploadService.eliminar(nombreFotoAnterior);
			
			producto.setFoto(nombreArchivo);
			productoService.save(producto);

			response.put("producto", producto);
			response.put("mensaje", "Archivo cargado con exito: " + nombreArchivo);

		}
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

	}
	
	@GetMapping("/uploads/archivo/{nombreArchivo:.+}")
	public ResponseEntity<Resource> verArchivo(@PathVariable String nombreArchivo){
		Resource recurso = null;
		
		try {
			recurso = uploadService.cargar(nombreArchivo);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		org.springframework.http.HttpHeaders cabecera = new org.springframework.http.HttpHeaders();
		cabecera.add(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename =\"" + 
		recurso.getFilename() + "\"");
		
		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}
}
