package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.demo.mail.Mailer;
import com.example.demo.model.Lancamento;
import com.example.demo.model.Pessoa;
import com.example.demo.model.Usuario;
import com.example.demo.repositories.LancamentoRepository;
import com.example.demo.repositories.PessoaRepository;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.service.exception.PessoaInexistenteOuInativaException;
//import com.example.demo.storage.S3;

@Service
public class LancamentoService {
	
	/*	Pegar usuario pelo token
	 * 
	 *  String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	 *  Optional<Usuario> logg = this.usuarioRepository.findByEmail(principal);
	*/
	
	/* List To Page
	 * 
	 * public Page<AllUserDto> toPage(List<AllUserDto> list, Pageable pageable) {
		if (pageable.getOffset() >= list.size()) {
			return Page.empty();
		}
		int startIndex = (int)pageable.getOffset();
		int endIndex = (int) ((pageable.getOffset() + pageable.getPageSize()) > list.size() ?
		list.size() :
		pageable.getOffset() + pageable.getPageSize());
		List<AllUserDto> subList = list.subList(startIndex, endIndex);
		return new PageImpl<AllUserDto>(subList, pageable, list.size());
	}
	 */
	
	private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";
	
	private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private Mailer mailer;
	
	//@Autowired
	//private S3 s3;
	
	//config/WebConfig
	//fixedDelay -> tempo fixo sempre quando o procedimento anterior e finalizado
	//cron -> segundo minuto hora dia mes semana
	@Scheduled(cron = "0 30 14 * * *")
	public void avisarSobreLancamentoVencido() {
		if(logger.isDebugEnabled()) {
			logger.debug("Preparando envio de e-mails sobre lancamentos");
		}
		List<Lancamento> vencidos = lancamentoRepository
				.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
		
		if(vencidos.isEmpty()) {
			logger.info("Sem lancamentos vencidos para aviso");
			return ;
		}
		
		logger.info("Existem {} lançamentos vencidos.", vencidos.size());
		
		List<Usuario> destinatarios = usuarioRepository
				.findByPermissoesDescricao(DESTINATARIOS);
		
		if(destinatarios.isEmpty()) {
			logger.warn("Existem lançamentos vencidos, mas não tem destinatários");
			return ;
		}
		
		mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);
		
		logger.info("Envio de e-mail de aviso concluido");
	}

	public Lancamento salvar(@Valid Lancamento lancamento) {
		/*Optional<Pessoa> pessoa = this.pessoaRepository.findById(lancamento.getPessoa().getId());
		if(!pessoa.isPresent() || pessoa.get().isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}*/
		validarPessoa(lancamento);
		
		if(StringUtils.hasText(lancamento.getAnexo())) {
			//s3.salvar(lancamento.getAnexo());
		}
		
		return lancamentoRepository.save(lancamento);
	}
	
	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento lancamentoSalvo = buscarLancamentoExistente(codigo);
		if(!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			validarPessoa(lancamento);
		}
		
		if(StringUtils.isEmpty(lancamento.getAnexo()) && 
				StringUtils.hasText(lancamentoSalvo.getAnexo())) {
			//s3.remover(lancamentoSalvo.getAnexo());
		} else if (StringUtils.hasText(lancamento.getAnexo()) && !lancamento.getAnexo().equals(lancamentoSalvo.getAnexo())) {
			//s3.substituir(lancamentoSalvo.getAnexo(), lancamento.getAnexo());
		} else if(StringUtils.hasText(lancamento.getAnexo()) && StringUtils.isEmpty(lancamentoSalvo.getAnexo())) {
			//s3.salvar(lancamento.getAnexo());
		}
		
		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");
		
		return lancamentoRepository.save(lancamentoSalvo);
	}
	
	private void validarPessoa(Lancamento lancamento) {
		Optional<Pessoa> pessoa = this.pessoaRepository.findById(lancamento.getPessoa().getId());
		if(!pessoa.isPresent() || pessoa.get().isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
	}
	
	private Lancamento buscarLancamentoExistente(Long codigo) {
		Optional<Lancamento> lancamentoOpt = this.lancamentoRepository.findById(codigo);
		if(!lancamentoOpt.isPresent()) {
			throw new IllegalArgumentException();
		}
		return lancamentoOpt.get();
	}
}
