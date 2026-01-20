package com.agro.service;

import com.agro.dto.LoginRequest;
import com.agro.dto.RegisterRequest;
import com.agro.entity.Farmer;

public interface AuthService {

	Farmer register(RegisterRequest request);

	Farmer login(LoginRequest request);
}
