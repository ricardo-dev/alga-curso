package com.example.demo.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.demo.model.Pessoa;
import com.example.demo.repositories.PessoaRepository;

@Service
public class PessoaService {
	
	@Autowired
	PessoaRepository pessoaRepository;
	
	public Pessoa save(Pessoa pessoa) {
		// porque em contato via pessoa, a propriedade pessoa é ignorada
		pessoa.getContatos().forEach( c -> c.setPessoa(pessoa));
		return pessoaRepository.save(pessoa);
	}
	
	public Pessoa atualizarPessoa(Long id, Pessoa pessoa) {
		Pessoa pessoaSalva = findUserForId(id);
		
		// devido o uso do orphan removal
		pessoaSalva.getContatos().clear();
		pessoaSalva.getContatos().addAll(pessoa.getContatos());
		pessoaSalva.getContatos().forEach( c -> c.setPessoa(pessoaSalva));
				
		// BeanUtils.copyProperties(pessoa, pessoaSalva, "id", "senha"); copia os dados -id e senha
    	BeanUtils.copyProperties(pessoa, pessoaSalva, "id", "contatos"); // copia as properties
    	return pessoaRepository.save(pessoaSalva);
	}

	public void atualizarPropriedadeAtivo(Long id, Boolean ativo) {
		Pessoa pessoaSalva = this.findUserForId(id);
		pessoaSalva.setAtivo(ativo);
		this.pessoaRepository.save(pessoaSalva);
	}
	
	private Pessoa findUserForId(Long id) {
		Pessoa pessoaSalva = this.pessoaRepository.findById(id)
				.orElseThrow(() -> new EmptyResultDataAccessException(1));
		return pessoaSalva;
	}
}
