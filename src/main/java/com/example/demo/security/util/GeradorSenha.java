package com.example.demo.security.util;

import javax.validation.Valid;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class GeradorSenha {
	
	
	// alterar senha no post de usuario
	public static String gerarBCrypt(String senha) {
		if(senha == null)
			return senha;
		
		// log.info("Gerando Hash com BCrypt");
		BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
		String s = bCryptEncoder.encode(senha);
		// log.info(s);
		return bCryptEncoder.encode(senha);
	}
}

/*
 * public static boolean compare(String password, String hashPassword) {
		BCryptPasswordEncoder bC = new BCryptPasswordEncoder();
		return bC.matches(password, hashPassword);
	}
	
	public UpdatePasswordReturnDto updatePassword(Long id, @Valid UpdatePasswordDto dto) {
		Partner partner = this.verifyPartnerById(id);
		boolean change = false;
		if(BcryptEncoder.compare(dto.getPassword(), partner.getPassword())) {
			partner.setPassword(BcryptEncoder.createBCrypt(dto.getNewPassword()));
			this.partnerRepository.save(partner);
			change = true;
		} 
		UpdatePasswordReturnDto ret = new UpdatePasswordReturnDto();
		ret.setChange(change);
		return ret;
	}
	
	
 * 
 */
