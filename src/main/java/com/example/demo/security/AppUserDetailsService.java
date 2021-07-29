package com.example.demo.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.stereotype.Service;

import com.example.demo.model.Usuario;
import com.example.demo.repositories.UsuarioRepository;

@Service
public class AppUserDetailsService implements UserDetailsService{

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
		Usuario usuario = usuarioOpt.orElseThrow(()-> new UsernameNotFoundException("Usuário e/ou Senha inexistentes ou incorreto"));
		// Usuario usuario = usuarioOpt.orElseThrow(()-> new InvalidGrantException("Usuario e/ou Senha inexistentes"));
		// if(algum campo invalido) throw new InternalAuthenticationServiceException("Usuario Bloqueado ou E-mail nao confirmado");
		//return new User(email, usuario.getSenha(), getPermissoes(usuario));
		return new UsuarioSistema(usuario, getPermissoes(usuario));
	}

	private Collection<? extends GrantedAuthority> getPermissoes(Usuario usuario) {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		// caso perfil seja enum
		// authorities.add(new SimpleGrantedAuthority(usuario.getTipoPerfil().toString().toUpperCase()));
		usuario.getPermissoes().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getDescricao().toUpperCase())));
		return authorities;
	}
}
