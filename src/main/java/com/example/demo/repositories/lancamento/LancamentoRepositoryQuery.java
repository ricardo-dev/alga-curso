package com.example.demo.repositories.lancamento;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.dto.LancamentoEstatisticaCategoria;
import com.example.demo.dto.LancamentoEstatisticaDia;
import com.example.demo.model.Lancamento;
import com.example.demo.repositories.filter.LancamentoFilter;
import com.example.demo.repositories.projection.ResumoLancamento;

public interface LancamentoRepositoryQuery {
	
	/*public List<Lancamento> filtrar(LancamentoFilter lancamentoFilter);*/
	
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);

	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);
	
	public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia);
	
	public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia);

}
