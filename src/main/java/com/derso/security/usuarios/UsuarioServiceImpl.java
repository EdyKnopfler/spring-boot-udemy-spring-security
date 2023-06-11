package com.derso.security.usuarios;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
 * MANUTENÇÃO DE USUÁRIOS
 */

@Service
public class UsuarioServiceImpl 
	implements UserDetailsService {  // Interface do Spring :)
	
	@Autowired
	private UsuarioRepository repositorio;

	private final List<String> roles = Arrays.asList("USER", "ADMIN");
			
	@Override
	public UserDetails loadUserByUsername(String buscaLogin) throws UsernameNotFoundException {
		Usuario usuario = repositorio
			.findByLoginOrEmail(buscaLogin, buscaLogin)
			.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + buscaLogin));
		
		String[] permissoes = roles
			.stream()
			.filter(role -> usuario.isAdmin() || !role.equals("ADMIN"))
			.toArray(String[]::new);
		
		// Password igual ao username
		// Encriptado em https://bcrypt-generator.com/
		return User.builder()
			.username(buscaLogin)
			.password("{bcrypt}" + usuario.getSenha())
			.roles(permissoes)
			.build();
	}

}
