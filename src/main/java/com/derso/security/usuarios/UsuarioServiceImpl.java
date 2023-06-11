package com.derso.security.usuarios;

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

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (!username.equals("kânia")) {
			throw new UsernameNotFoundException("Cê tá pensando que cê é quem??");
		}
		
		// Password igual ao username
		// Encriptado em https://bcrypt-generator.com/
		return User.builder()
			.username("kânia")
			.password("{bcrypt}$2a$12$.bLRI7BwK/osDYYSCPVtueLLhqq2d3aRMd4y.cefMjUrzceHRwEmW")
			.roles("USER", "ADMIN")
			.build();
	}

}
