package br.com.alura.forum.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.forum.config.security.TokenService;
import br.com.alura.forum.controller.dto.LoginRes;
import br.com.alura.forum.controller.form.LoginReq;

@RequestMapping("/login")
@RestController
public class AutenticacaoController {
	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private TokenService tokenService;
	
	@PostMapping
	public ResponseEntity<?> autenticar(@RequestBody @Valid LoginReq lR) {
		// Chama o método 'converter' q utiliza os dados de email e senha contidos na
		// própria classe 'LoginReq'
		UsernamePasswordAuthenticationToken dadosLogin = lR.converter();
		
		try {
			// Quando o spring executar essa linha, executará tb o service/classe
			// 'AutenticacaoService'contendo as consultas e verificações de existencia ou n
			// do usuário, retorna uma 'exception' caso n encontre
			
			// A classe AuthenticationManager deve ser utilizada apenas na lógica de autenticação
			// via username/password, para a geração do token
			Authentication authentication = authManager.authenticate(dadosLogin);
			
			// Se chegou até aqui é pq o usuário existe
			String token = tokenService.gerarToken(authentication);
						
			return ResponseEntity.ok(new LoginRes(token, "Bearer"));
		} catch (AuthenticationException e) {
			return ResponseEntity.badRequest().build();
		}		
	}
}
