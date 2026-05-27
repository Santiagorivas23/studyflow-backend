package co.studyflow.controller;

import co.studyflow.dto.AuthResponseDTO;
import co.studyflow.dto.LoginRequestDTO;
import co.studyflow.dto.RegistroRequestDTO;
import co.studyflow.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints públicos de autenticación: login y registro
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://*.vercel.app"})
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * POST /api/auth/login
     * Body: { email, password }
     * Response: { token, usuarioId, nombre, email }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/registro
     * Body: { nombre, apellido?, email, password }
     * Response: { token, usuarioId, nombre, email }
     */
    @PostMapping("/registro")
    public ResponseEntity<AuthResponseDTO> registro(@RequestBody RegistroRequestDTO request) {
        AuthResponseDTO response = authService.registro(request);
        return ResponseEntity.status(201).body(response);
    }
}
