package com.derso.security.usuarios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.derso.security.autenticacao.JwtService;
import com.derso.security.autenticacao.SenhaInvalidaException;

/*
 * Injetada automaticamente no Spring Boot :)
 */

@Service
public class UsuarioServiceImpl 
	implements UserDetailsService {  // Interface do Spring :)
	
	private static final PasswordEncoder encoder = 
			new BCryptPasswordEncoder(12);
	private static final String[] userRoles = {"USER"};
	private static final String[] adminRoles = {"USER", "ADMIN"};
	
	@Autowired
	private UsuarioRepository repositorio;
	
	@Autowired
	private JwtService jwtService;
	
	public void salvar(Usuario usuario) {
		String senhaPelada = usuario.getSenha();
		usuario.setSenha(encoder.encode(senhaPelada));
		repositorio.save(usuario);
	}
	
	public String autenticar(Usuario usuario) 
			throws UsernameNotFoundException, SenhaInvalidaException {
		
		// Os padrões do Spring Security são automáticos; aqui é tudo no 
		// "manuático"
		UserDetails userDetails = loadUserByUsername(usuario.getLogin());
		String senhaDigitada = usuario.getSenha();
		String criptografada = userDetails.getPassword().replace("{bcrypt}", "");
		
		if (encoder.matches(senhaDigitada, criptografada)) {
			return jwtService.gerarToken(usuario);
		}
		
		throw new SenhaInvalidaException();
	}
			
	@Override
	public UserDetails loadUserByUsername(String buscaLogin) throws UsernameNotFoundException {
		Usuario usuario = repositorio
			.findByLoginOrEmail(buscaLogin, buscaLogin)
			.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + buscaLogin));

		return User.builder()
			.username(buscaLogin)
			.password("{bcrypt}" + usuario.getSenha())
			.roles(usuario.isAdmin() ? adminRoles : userRoles)
			.build();
	}

}
