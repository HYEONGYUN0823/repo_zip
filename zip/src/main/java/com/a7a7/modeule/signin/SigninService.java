package com.a7a7.modeule.signin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SigninService {
	@Autowired
	SigninDao signinDao;
	
}
