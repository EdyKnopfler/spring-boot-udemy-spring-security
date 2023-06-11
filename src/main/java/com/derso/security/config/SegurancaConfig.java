package com.derso.security.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.derso.security.autenticacao.JwtAuthFilter;
import com.derso.security.autenticacao.JwtService;
import com.derso.security.usuarios.UsuarioServiceImpl;

/*
 * CONFIGURAÇÕES DO SPRING SECURITY
 * 
 * O curso menciona WebSecurityConfigurerAdapter, o qual foi deprecado :(
 * https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
 * 
 * Foi o que usei quando fiz o curso da Alura:
 * https://github.com/EdyKnopfler/alura-spring-aplicacao/blob/main/src/main/java/br/com/pip/mvc/mudi/WebSecurityConfig.java
 * 
 * Para não se perder no meio de tanta mudança, bora ler as docs!
 * https://docs.spring.io/spring-security/reference/current/index.html
 * 
 * Com JDBC: fornecemos o UserDetailsManager
 * https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/jdbc.html
 * 
 * Encontrei um jeito de preencher o cara antigo (AuthenticationManagerBuilder)!
 * https://www.baeldung.com/spring-security-authentication-provider
 * O artigo usa para configurar de forma totalmente customizada um provedor externo.
 * 
 * Com o serviço customizado (ver UsuarioServiceImpl.java) nada disso é necessário, pois sendo um @Service
 * ele entra automaticamente na injeção de dependências.
 */

@Configuration
public class SegurancaConfig {
	
	@Autowired
	private UsuarioServiceImpl usuarioService;
	
	@Autowired
	private JwtService jwtService;
	
	// ESTE é o cara que configura o HttpSecurity agora :)
	// Segundo as docs, o SecurityFilterChain se integra na cadeia de filtros de servlets
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		AntPathRequestMatcher[] adminMatchers = Arrays.asList(
			antMatcher("/usuarios/**"),
			antMatcher("/h2-console/**")
		).toArray(AntPathRequestMatcher[]::new);
		// Fluent interfaces são a pior merda já inventada #ChangeMyMind
		return http
			.authorizeHttpRequests(
				autorizacao -> autorizacao
					.requestMatchers("/", "/usuarios/autenticar").permitAll()  // Permite a home
					.requestMatchers(adminMatchers).hasAnyRole("ADMIN")  // Verifica o perfil de admin nestes
					.anyRequest().authenticated()  // Bloqueia o resto
			)
			
			// Nosso filtro customizado (validação de JWT)
			// Executa antes do filtro padrão do Spring indicado
			.addFilterBefore(
					new JwtAuthFilter(jwtService, usuarioService),
					UsernamePasswordAuthenticationFilter.class)
			
			// Em uma API REST real desativaríamos as sessões
			// Aqui, para estudo, estou habilitando navegação no browser com form de login
			// (conveniência para acessar o console do H2)
			/*
			.sessionManagement((session) -> session
	            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			*/
			
			// O console do H2 requer habilitar os iframes e ignorar CSRF
			// PQP: estava esquecendo de desabilitar CSRF nos endpoints REST e não funcionava
			// via cURL de jeito nenhum :P
			.headers(headers -> headers.frameOptions(Customizer.withDefaults()).disable())
			.csrf(csrf -> 
				csrf.ignoringRequestMatchers(adminMatchers))
			.httpBasic(Customizer.withDefaults())
			.formLogin(Customizer.withDefaults())
			
			// That's all, folks!
			.build();
	}
	
}
