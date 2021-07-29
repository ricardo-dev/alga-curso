package com.example.demo.resources;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.evento.RecursoCriadoEvent;
import com.example.demo.model.Categoria;
import com.example.demo.repositories.CategoriaRepository;

// allowedHeaders (headers permitido
// maxAge (tempo de intervalo de requisição option)
// origin = "path-permitida"
// @CrossOrigin(maxAge=10, origins= {"http://localhost:8000", "localhost:4200"})

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

    private static final Logger log =  LoggerFactory.getLogger(CategoriaResource.class);

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
    public ResponseEntity<?> getAllCategorias(){
        List<Categoria> lista = this.categoriaRepository.findAll();

        return ResponseEntity.ok(lista);
    }

    //@RequestMapping(method = RequestMethod.POST, consumes="Content-Type:application/json", produces="...")
    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA') and #oauth2.hasScope('write')")
    public ResponseEntity<?> saveCategory(@Valid @RequestBody Categoria categoria, HttpServletResponse response){
        Categoria categoriaSalva = this.categoriaRepository.save(categoria);
        
        publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
    public ResponseEntity<?> getCategoryId(@PathVariable("id") Long id){

        Optional<Categoria> categoria = this.categoriaRepository.findById(id); // findById().orElse(null);
        return categoria.isPresent() ? ResponseEntity.ok(categoria.get()) : ResponseEntity.notFound().build();
    }
}
