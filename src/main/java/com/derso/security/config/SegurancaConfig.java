package com.derso.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
	
	// ESTE é o cara que configura o HttpSecurity agora :)
	// Segundo as docs, o SecurityFilterChain se integra na cadeia de filtros de servlets
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// Fluent interfaces são a pior merda já inventada #ChangeMyMind
		return http
			.authorizeHttpRequests(
				autorizacao -> autorizacao
					.requestMatchers("/").permitAll()  // Permite a home
					.requestMatchers(
						AntPathRequestMatcher.antMatcher("/h2-console/**"),
						AntPathRequestMatcher.antMatcher("/usuarios/**")
					).hasAnyRole("ADMIN")
					.anyRequest().authenticated()      // Bloqueia o resto 
			)
			// O console do H2 requer habilitar os iframes e ignorar CSRF
			.headers(headers -> headers.frameOptions(Customizer.withDefaults()).disable())
			.csrf(csrf -> 
				csrf.ignoringRequestMatchers(
						AntPathRequestMatcher.antMatcher("/h2-console/**")))
			.formLogin(Customizer.withDefaults())
			.build();
	}
	
}
