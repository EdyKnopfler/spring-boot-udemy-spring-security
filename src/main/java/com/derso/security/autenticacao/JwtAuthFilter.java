package com.derso.security.autenticacao;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.derso.security.usuarios.UsuarioServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * FILTRO DE AUTENTICAÇÃO
 */

public class JwtAuthFilter extends OncePerRequestFilter {
	
	private JwtService jwtService;
	private UsuarioServiceImpl usuarioService;
	
	// Usada na configuração do Spring Boot
	public JwtAuthFilter(JwtService jwtService, UsuarioServiceImpl usuarioService) {
		this.jwtService = jwtService;
		this.usuarioService = usuarioService;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, 
			HttpServletResponse response, 
			FilterChain filterChain)
			throws ServletException, IOException {
		
		String authorization = request.getHeader("Authorization");
		
		if (authorization != null && authorization.startsWith("Bearer")) {
			// Isto é feio mas para fins didáticos está bom
			String token = authorization.split(" ")[1];
			
			if (jwtService.tokenValido(token)) {
				String login = jwtService.loginUsuario(token);
				UserDetails usuario = usuarioService.loadUserByUsername(login);
				
				// Colocando o usuário no contexto
				// O usuário é autenticado durante o processamento da requisição
				UsernamePasswordAuthenticationToken userAuthentication =
						new UsernamePasswordAuthenticationToken(
								usuario, null, usuario.getAuthorities());
				userAuthentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(userAuthentication);
			}
		}
		
		// Caso não, vai embora sem esse usuário e dispara qualquer outro mecanismo!
		filterChain.doFilter(request, response);
	}

}
