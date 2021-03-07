package br.com.alura.forum.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;

// 'OncePerRequestFilter' filtro do spring q é chamado uma vez a cada requisição
// Nesse tipo de filter não é possível usar 'Autowired'
// Esse filter será instanciado na classe 'SecurityConfigurations'
public class TokenFilter extends OncePerRequestFilter {
	private TokenService tokenService;
	private UsuarioRepository usuarioRepository;
	
	public TokenFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
		this.tokenService = tokenService;
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	throws ServletException, IOException {
		String token = recuperarToken(request);
		Boolean validate = tokenService.isTokenValid(token);
		
		if (validate) {
			autenticar(token);
		}
		
		// Se n autenticar, segue o fluxo normal do spring com as restrições
		filterChain.doFilter(request, response);
	}

	private void autenticar(String token) {
		Long userId = tokenService.getUserId(token);
		Usuario user = usuarioRepository.findById(userId).get();
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		
		// Indica ao spring q o usuário está autenticado
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private String recuperarToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		
		if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
			return null;
		}
		
		return token.substring(7, token.length());
	}
}
