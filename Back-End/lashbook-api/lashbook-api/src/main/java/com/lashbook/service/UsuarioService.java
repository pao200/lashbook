package com.lashbook.service;

import com.lashbook.dto.RegistroUsuarioRequest;
import com.lashbook.dto.UsuarioResponse;
import com.lashbook.entity.RolUsuario;
import com.lashbook.entity.Usuario;
import com.lashbook.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import com.lashbook.dto.LoginRequest;
import com.lashbook.dto.LoginResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
    this.usuarioRepository = usuarioRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
}    
    @Transactional
    public UsuarioResponse registrarCliente(
            RegistroUsuarioRequest request
    ) {
        String correoNormalizado = request
            .getCorreo()
            .trim()
            .toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByCorreo(correoNormalizado)) {
            throw new IllegalArgumentException(
                "Ya existe un usuario registrado con ese correo"
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(request.getNombre().trim());
        usuario.setCorreo(correoNormalizado);

        usuario.setPasswordHash(
            passwordEncoder.encode(request.getPassword())
        );

        usuario.setRol(RolUsuario.CLIENTA);
        usuario.setActivo(true);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return convertirRespuesta(usuarioGuardado);
    }

    private UsuarioResponse convertirRespuesta(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getCorreo(),
            usuario.getRol(),
            usuario.getActivo(),
            usuario.getCreadoEn()
        );
    }
    @Transactional(readOnly = true)
public LoginResponse iniciarSesion(LoginRequest request) {

    String correoNormalizado = request
        .getCorreo()
        .trim()
        .toLowerCase(Locale.ROOT);

    Usuario usuario = usuarioRepository
        .findByCorreo(correoNormalizado)
        .orElseThrow(() ->
            new BadCredentialsException(
                "Correo o contraseña incorrectos"
            )
        );

    if (!Boolean.TRUE.equals(usuario.getActivo())) {
        throw new DisabledException(
            "El usuario se encuentra desactivado"
        );
    }

    boolean passwordCorrecto = passwordEncoder.matches(
        request.getPassword(),
        usuario.getPasswordHash()
    );

    if (!passwordCorrecto) {
        throw new BadCredentialsException(
            "Correo o contraseña incorrectos"
        );
    }

    String token = jwtService.generarToken(usuario);

    return new LoginResponse(
        token,
        "Bearer",
        jwtService.getExpirationSeconds(),
        convertirRespuesta(usuario)
    );
}






}
