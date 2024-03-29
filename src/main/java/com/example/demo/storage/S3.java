package com.example.demo.storage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
/*
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;
import com.example.demo.config.property.AlgamoneyApiProperty;*/
/*
@Component
public class S3 {
	
	private static final Logger log = LoggerFactory.getLogger(S3.class);

	@Autowired
	private AmazonS3 amazonS3;
	
	//22.34 - anexando arquivo ao lancamento
	
	@Autowired
	private AlgamoneyApiProperty property;
	
	public String salvarTemporariamente(MultipartFile arquivo){
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(arquivo.getContentType());
		objectMetadata.setContentLength(arquivo.getSize());
		
		String nomeUnico = gerarNomeUnico(arquivo.getOriginalFilename());
		
		try {
			PutObjectRequest put = new PutObjectRequest(
					property.getS3().getBucket(),
					nomeUnico, arquivo.getInputStream(), objectMetadata
					).withAccessControlList(acl);
			
			put.setTagging(new ObjectTagging(Arrays.asList(new Tag("expirar", "true"))));
			
			amazonS3.putObject(put);
			
			if(log.isDebugEnabled()) {
				log.debug("Arquivo {} enviado com sucesso para o S3.", arquivo.getOriginalFilename());
			}
			
			return nomeUnico;
		} catch (IOException e) {
			throw new RuntimeException("Problemas ao tentar enviar o arquivo ao S3.", e);
		}
		
	}
	
	public String configurarUrl(String objeto) {
		return "\\\\"+property.getS3().getBucket()+".s3.amazonaws.com/"+objeto;
	}
	
	public void salvar(String objeto) {
		SetObjectTaggingRequest setObjectTaggingRequest = 
				new SetObjectTaggingRequest(property.getS3().getBucket(), 
						objeto, 
						new ObjectTagging(Collections.emptyList()));
		amazonS3.setObjectTagging(setObjectTaggingRequest);
	}
	
	public void remover(String objeto) {
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
				property.getS3().getBucket(), objeto
				);
		amazonS3.deleteObject(deleteObjectRequest);
	}
	
	public void substituir(String objetoAntigo, String objetoNovo) {
		if(StringUtils.hasText(objetoAntigo)) {
			this.remover(objetoAntigo);
		}
		this.salvar(objetoNovo);
	}	

	private String gerarNomeUnico(String originalFilename) {
		return UUID.randomUUID().toString()+"_"+originalFilename;
	}
}
*/
