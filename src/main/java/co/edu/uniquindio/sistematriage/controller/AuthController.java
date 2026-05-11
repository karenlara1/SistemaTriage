package co.edu.uniquindio.sistematriage.controller;

import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import co.edu.uniquindio.sistematriage.dto.request.LoginRequestDTO;
import co.edu.uniquindio.sistematriage.dto.response.LoginResponseDTO;
import co.edu.uniquindio.sistematriage.exception.BusinessRuleException;
import co.edu.uniquindio.sistematriage.repository.UsuarioRepository;
import co.edu.uniquindio.sistematriage.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new BusinessRuleException("Credenciales inválidas"));

        if (!usuario.isActivo()) {
            throw new BusinessRuleException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new BusinessRuleException("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(
                usuario.getCorreo(),
                usuario.getRol().name(),
                usuario.getIdUsuario()
        );

        return ResponseEntity.ok(new LoginResponseDTO(
                token,
                usuario.getRol().name(),
                usuario.getIdUsuario().toString(),
                usuario.getNombre()
        ));
    }
}