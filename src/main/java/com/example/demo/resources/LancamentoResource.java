package com.example.demo.resources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.LancamentoEstatisticaCategoria;
import com.example.demo.dto.LancamentoEstatisticaDia;
import com.example.demo.evento.RecursoCriadoEvent;
import com.example.demo.exceptionhandler.AlgamoneyApiExceptionHandler.Erro;
import com.example.demo.model.Lancamento;
import com.example.demo.repositories.LancamentoRepository;
import com.example.demo.repositories.filter.LancamentoFilter;
import com.example.demo.repositories.projection.ResumoLancamento;
import com.example.demo.service.LancamentoService;
import com.example.demo.service.exception.PessoaInexistenteOuInativaException;
//import com.example.demo.storage.S3;


@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
    private ApplicationEventPublisher publisher;
	
	@Autowired
	private MessageSource messageSource;
	
	/*@Autowired
	private S3 s3;
	
	@RequestMapping(value="/anexo-aws", method=RequestMethod.POST)
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public Anexo uploadAnexoAwes(@RequestParam MultipartFile anexo) throws IOException {
		String nome = s3.salvarTemporariamente(anexo);
		return new Anexo(nome, s3.configurarUrl(nome));
	}*/
	
	@RequestMapping(value="/anexo", method=RequestMethod.POST, consumes="multipart/form-data", produces="application/json")
	//@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<?> uploadAnexo(@RequestParam MultipartFile anexo, @RequestParam String teste) throws IOException {
		System.out.println("Teste "+teste);
		/*OutputStream out = new FileOutputStream(
				"/home/ricardo/anexo--"+anexo.getOriginalFilename());
		out.write(anexo.getBytes());
		out.close();
		return "OK";*/
		
		 StringBuilder sb = new StringBuilder();
		 sb.append("data:image/png;base64,");
		 sb.append(StringUtils.newStringUtf8(Base64.encodeBase64(anexo.getBytes(), false)));
		 return ResponseEntity.ok(sb);
		 
		 /*
		  * private String getImageFromLocalStorage(MultipartFile file) {
		
				StringBuilder filesName = new StringBuilder();
        		Path fileNameAndPath = Paths.get(diretorioUpload, this.obterHashAleatorio()+file.getOriginalFilename());
        		filesName.append(file.getOriginalFilename());
        		try {
            		Files.write(fileNameAndPath, file.getBytes());
            		return fileNameAndPath.toString();
        		} catch (IOException e) {
            		e.printStackTrace();
            		return "";
        		}
			}
		  */
		
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/estatisticas/por-dia")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<?> lancamentoPorDia(){
		List<LancamentoEstatisticaDia> lancamentos = lancamentoRepository.porDia(LocalDate.now().withMonth(1));
		return ResponseEntity.ok(lancamentos);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/estatisticas/por-categoria")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<?> lancamentoPorCategoria(){
		List<LancamentoEstatisticaCategoria> lancamentos = lancamentoRepository.porCategoria(LocalDate.now());
		return ResponseEntity.ok(lancamentos);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<?> searchLancamentos(LancamentoFilter lancamentoFilter, Pageable pageable){
		// List<Lancamento> lancamentos = this.lancamentoRepository.findAll();
		Page<Lancamento> lancamentos = this.lancamentoRepository.filtrar(lancamentoFilter, pageable);
		return ResponseEntity.ok(lancamentos);
	}
	
	/*
	Alterando o retorno paginado
	Page<Contact> contactPage = this.contactRepository.findAll(pageable);
	final Page<ContactDto> contactDtoPage = contactPage.map(this::convertToContactDto);
	
	private ContactDto convertToContactDto(final Contact contact) {
	    final ContactDto contactDto = new ContactDto();
	    //get values from contact entity and set them in contactDto
	    //e.g. contactDto.setContactId(contact.getContactId());
	    return contactDto;
	}
	*/
	
	/*
	 * @RequestMapping(method=RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PARCEIRO') and #oauth2.hasScope('read')")
	public ResponseEntity<Page<Contact>> getAllPaginator(@RequestParam(required=false, defaultValue="") String name, @RequestParam("page") int page, @RequestParam("size") int size){
		Page<Contact> contactsPage = this.contactRepository.findByNameContaining(name, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
		return ResponseEntity.ok(contactsPage);
	}*/
	
	@RequestMapping(method = RequestMethod.GET, params = "resumo")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<?> resumirLancamentos(LancamentoFilter lancamentoFilter, Pageable pageable){
		Page<ResumoLancamento> lancamentos = this.lancamentoRepository.resumir(lancamentoFilter, pageable);
		return ResponseEntity.ok(lancamentos);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<?> getLancamentoById(@PathVariable("id") Long id){
		Optional<Lancamento> lancamentoSalvo = this.lancamentoRepository.findById(id);
		return lancamentoSalvo.isPresent() ? ResponseEntity.ok(lancamentoSalvo.get()) : ResponseEntity.notFound().build();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	public ResponseEntity<?> inserirLancamento(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response ){
		Lancamento lancamentoSalvo = this.lancamentoService.salvar(lancamento);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamento.getCodigo() ) );
		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
	}
	
	// Exception que só vai cair aqui, bem especifica, entao pode manter no resource
	@ExceptionHandler({PessoaInexistenteOuInativaException.class})
	public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex){
		String body = messageSource.getMessage("pessoa.inexistente-ou-inativa", null, LocaleContextHolder.getLocale());
        String messageDevelop = ex.toString();
        List<Erro> erros = Arrays.asList(new Erro(body, messageDevelop));
    	return ResponseEntity.badRequest().body(erros);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE )
	@PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and #oauth2.hasScope('write')")
    @ResponseStatus(HttpStatus.NO_CONTENT) // pelo fato de não ter retorno - 204
    public void deleteLancamento(@PathVariable("id") Long id) {
		Optional<Lancamento> lancamentoSalvo = lancamentoRepository.findById(id);
		/*if(lancamentoSalvo.isPresent() && StringUtils.hasText(lancamentoSalvo.get().getAnexo())) {
			s3.remover(lancamentoSalvo.get().getAnexo());
		}*/
    	lancamentoRepository.deleteById(id);
    }
	
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	public ResponseEntity<?> atualizarLancamento(@PathVariable("id") Long id, @Valid @RequestBody Lancamento lancamento){
		try {
			Lancamento lancamentoSalvo = this.lancamentoService.atualizar(id, lancamento);
			return ResponseEntity.ok(lancamentoSalvo);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
