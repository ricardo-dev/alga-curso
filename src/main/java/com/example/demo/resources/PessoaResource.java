package com.example.demo.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.evento.RecursoCriadoEvent;
import com.example.demo.model.Pessoa;
import com.example.demo.repositories.PessoaRepository;
import com.example.demo.service.PessoaService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {

    @Autowired
    private PessoaRepository pessoaRepository;
    
    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private ApplicationEventPublisher publisher;

    /*@RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
    public ResponseEntity<?> getUsers(){
        List<Pessoa> pessoas = this.pessoaRepository.findAll();
        return !pessoas.isEmpty() ? ResponseEntity.ok(pessoas) : ResponseEntity.noContent().build();
    }*/
    
    @ApiOperation(value="Pesquisar pessoa pelo nome")
	@ApiResponses(value= {
			@ApiResponse(code=200, message="Retorna uma paginação de pessoa"),
			@ApiResponse(code=204, message="Sem conteúdo"),
			@ApiResponse(code=500, message="Erro inesperado")
	})
    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
    public ResponseEntity<?> getUsers(@RequestParam(required=false, defaultValue="") String nome, Pageable pageable){
        Page<Pessoa> pessoas = this.pessoaRepository.findByNomeContaining(nome, pageable);
        return ResponseEntity.ok(pessoas);
    }

    @RequestMapping(method = RequestMethod.POST)
    // @PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
    // @PreAuthorize("hasAnyAuthority('DELETE_AUTHORITY', 'UPDATE_AUTHORITY') and #oauth2.hasScope('write')")
    public ResponseEntity<?> savePessoa(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response){

        Pessoa pessoaSalva = this.pessoaService.save(pessoa);
        
        publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoaSalva.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
    }

    @RequestMapping(value="/{id}",method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
    public ResponseEntity<?> getAllUsers(@PathVariable("id") Long id){
        Optional<Pessoa> pessoa = this.pessoaRepository.findById(id);

        return pessoa.isPresent() ? ResponseEntity.ok(pessoa.get()) : ResponseEntity.notFound().build();
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE )
    @ResponseStatus(HttpStatus.NO_CONTENT) // pelo fato de não ter retorno - 204
    @PreAuthorize("hasAuthority('ROLE_REMOVER_PESSOA') and #oauth2.hasScope('write')")
    public void deleteUser(@PathVariable("id") Long id) {
    	pessoaRepository.deleteById(id);
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
    public ResponseEntity<Pessoa> update(@PathVariable("id") Long id, @Valid @RequestBody Pessoa pessoa){
    	
    	Pessoa pessoaSalva = this.pessoaService.atualizarPessoa(id, pessoa);
    	return ResponseEntity.ok(pessoaSalva);
    }
    
    @RequestMapping(value="/{id}/ativo", method=RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
    public void atualizarPropriedadeAtivo(@PathVariable Long id, @RequestBody Boolean ativo) {
    	this.pessoaService.atualizarPropriedadeAtivo(id, ativo);
    }
}
