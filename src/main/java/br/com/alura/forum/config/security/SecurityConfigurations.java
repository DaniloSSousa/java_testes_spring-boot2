package br.com.alura.forum.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.alura.forum.repository.UsuarioRepository;

@EnableWebSecurity
@Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {
	@Autowired
	private AutenticacaoService autenticacaoService;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UsuarioRepository UsuarioRepository;
	
	@Override
	// Annotation para indicar ao spring que o retorno do método é um valor q pode
	// ser injetado em outro local
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}
	
	// Configurações de autenticação, ex: usuário
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
	}
	
	// Configurações de autorização, ex: acesso a api
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//	http.authorizeRequests()
		//		.antMatchers(HttpMethod.GET, "/topicos").permitAll()
		//		.antMatchers(HttpMethod.GET, "/topicos/*").permitAll()
		//		.anyRequest().authenticated()
		//		.and().formLogin();
		
		// 'UsernamePasswordAuthenticationFilter.class' filter q já tem/inicia no spring
		// por padrão
		http.authorizeRequests()
			.antMatchers(HttpMethod.GET, "/topicos").permitAll()
			.antMatchers(HttpMethod.GET, "/topicos/*").permitAll()
			.antMatchers(HttpMethod.POST, "/login").permitAll()
			.anyRequest().authenticated()
			.and().csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and().addFilterBefore(new TokenFilter(tokenService, UsuarioRepository),
			UsernamePasswordAuthenticationFilter.class);
	}
	
	// Configurações de recursos estáticos, ex: js, css, imagens etc.
	@Override
	public void configure(WebSecurity web) throws Exception {}
}
