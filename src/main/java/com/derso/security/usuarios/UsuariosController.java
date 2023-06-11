package com.derso.security.usuarios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController("/usuarios")
public class UsuariosController {
	
	@Autowired
	private UsuarioServiceImpl repositorio;
	
	@PostMapping
	@Transactional
	public void novo(@RequestBody @Valid Usuario usuario) {
		repositorio.salvar(usuario);
	}

}
