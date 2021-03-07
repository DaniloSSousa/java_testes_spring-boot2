package br.com.alura.forum.controller.dto;

public class LoginRes {
	private String token;
	private String tipo;
	
	public LoginRes(String token, String tipo) {
		this.token = token;
		this.tipo = tipo;
	}

	public String getToken() {
		return token;
	}

	public String getTipo() {
		return tipo;
	}
}
