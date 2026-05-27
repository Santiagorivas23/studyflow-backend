package co.studyflow.service;

import co.studyflow.dto.AuthResponseDTO;
import co.studyflow.dto.LoginRequestDTO;
import co.studyflow.dto.RegistroRequestDTO;
import co.studyflow.exception.ConflictException;
import co.studyflow.exception.UnauthorizedException;
import co.studyflow.model.TipoAlgoritmo;
import co.studyflow.model.Usuario;
import co.studyflow.repository.UsuarioRepository;
import co.studyflow.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lógica de negocio para autenticación: login y registro
 */
@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales incorrectas"));

        if (usuario.getPasswordHash() == null ||
                !passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales incorrectas");
        }

        usuario.setUltimaConexion(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = jwtUtil.generarToken(usuario.getId());
        return new AuthResponseDTO(token, usuario.getId(), usuario.getNombre(), usuario.getEmail());
    }

    public AuthResponseDTO registro(RegistroRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Ya existe una cuenta con ese email");
        }

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID().toString());
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setAlgoritmoPorDefecto(TipoAlgoritmo.SM2);
        usuario.setTarjetasPorSesion(20);
        usuario.setIdioma("es");
        usuario.setTema("light");
        usuario.setNotificacionesHabilitadas(true);

        usuarioRepository.save(usuario);

        String token = jwtUtil.generarToken(usuario.getId());
        return new AuthResponseDTO(token, usuario.getId(), usuario.getNombre(), usuario.getEmail());
    }
}
