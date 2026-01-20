package com.agro.dto;

public class AuthResponse {
private String token;
private String role;
public AuthResponse(String token, String role) {
	super();
	this.token = token;
	this.role = role;
}
public AuthResponse() {
	super();
}
public String getToken() {
	return token;
}
public void setToken(String token) {
	this.token = token;
}
public String getRole() {
	return role;
}
public void setRole(String role) {
	this.role = role;
}
@Override
public String toString() {
	return "AuthResponse [token=" + token + ", role=" + role + "]";
}


}
