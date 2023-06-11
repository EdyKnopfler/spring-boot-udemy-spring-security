package com.derso.security.autenticacao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.derso.security.usuarios.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/*
 * TOKENS JWT
 * 
 * Dependência: jwts (io.jsonwebtoken), dentre outras opções
 */

@Service
public class JwtService {
	
	@Value("${security.jwt.expiracao-em-minutos}")
	private String expiracaoEmMinutos;
	
	@Value("${security.jwt.chave-assinatura}")
	private String chaveAssinatura;

	private SecretKey criarObjetoChave() {
		// Estava tentando decodificar os bytes a partir de um Base64, porém o
		// application.properties está tendo problemas com strings longas
		// (64 bytes porém em Base64 aumenta). 
		// Portanto, vamos pegar diretamente os bytes de uma sequência ASCII
		// qualquer.
		byte[] chaveEmBytes = chaveAssinatura.getBytes();
		return Keys.hmacShaKeyFor(chaveEmBytes);
	}
	
	public String gerarToken(Usuario usuario) {
		LocalDateTime dataHoraExpiracao = LocalDateTime.now().plusMinutes(
				Long.valueOf(expiracaoEmMinutos));
		
		// java.util.Date: exigência da lib
		Date data = Date.from(
				dataHoraExpiracao.atZone(ZoneId.systemDefault()).toInstant());
		
		Map<String, Object> claims = new HashMap<>();
		claims.put("admin", usuario.isAdmin());
		
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(usuario.getLogin())
				.setExpiration(data)
				.signWith(criarObjetoChave(), SignatureAlgorithm.HS512)
				.compact();
	}
	
	public Claims decodificar(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(criarObjetoChave())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	public boolean tokenValido(String token) {
		try {
			LocalDateTime dataHoraExpiracao = decodificar(token)
					.getExpiration()
					.toInstant()
					.atZone(ZoneId.systemDefault())
					.toLocalDateTime();
			
			// No creo que precisei fazer isto na mão!
			return dataHoraExpiracao.isAfter(LocalDateTime.now());
		} catch (Exception e) {
			return false;
		}
	}
	
	public String loginUsuario(String token) {
		return decodificar(token).getSubject();
	}
	
}
