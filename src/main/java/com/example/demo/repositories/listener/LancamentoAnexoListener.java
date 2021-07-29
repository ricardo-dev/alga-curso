package com.example.demo.repositories.listener;

import javax.persistence.PostLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.example.demo.AlgamoneyApi2Application;
import com.example.demo.model.Lancamento;
//import com.example.demo.storage.S3;
/*
 
 
 
 // para ativar, precisa alterar o ApiApplication
public class LancamentoAnexoListener {
		
	@PostLoad
	public void postLoad(Lancamento lancamento) {
		if(StringUtils.hasText(lancamento.getAnexo())) {
			// algamoneyapiapplication
			// entity lancamento
			S3 s3 = AlgamoneyApi2Application.getBean(S3.class);
			lancamento.setUrlAnexo(s3.configurarUrl(lancamento.getAnexo()));
		}
	}

}
*/