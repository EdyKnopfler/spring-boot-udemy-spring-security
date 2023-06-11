package com.derso.security.usuarios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.derso.security.autenticacao.SenhaInvalidaException;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {
	
	@Autowired
	private UsuarioServiceImpl usuarioService;
	
	@PostMapping
	@Transactional
	public void novo(@RequestBody @Valid Usuario usuario) {
		usuarioService.salvar(usuario);
	}
	
	@PostMapping("/autenticar")
	public String autenticar(@RequestBody Usuario usuario) {
		try {
			return usuarioService.autenticar(usuario);
		} catch (UsernameNotFoundException | SenhaInvalidaException e) {
			// Até parece que vou diferenciar aqui para um atacante saber que
			// achou um usuário :P
			throw new ResponseStatusException(
					HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos");
		}
	}

}
