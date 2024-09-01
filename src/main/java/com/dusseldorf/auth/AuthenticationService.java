package com.dusseldorf.auth;

import com.dusseldorf.email.EmailService;
import com.dusseldorf.email.EmailTemplateName;
import com.dusseldorf.email.model.Token;
import com.dusseldorf.email.repository.TokenRepository;
import com.dusseldorf.model.User;
import com.dusseldorf.repository.RoleRepository;
import com.dusseldorf.repository.UserRepository;
import com.dusseldorf.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {

        var userRole = roleRepository.findByName("User")
                .orElseThrow(() -> new IllegalStateException("Role User was not initialized"));


        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false) // cuenta bloqueado false -> la cuenta por defecto esta bloqueada
                .enabled(false) // cuenta habilitada es false -> ya que la cuenta debe habilitarse cuando el usuario registro una nueva cuenta
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);


        //enviar validacion de correo electronico de acuerdo

        //Primero -> es generar y guardar el token de activacion y luego enviar el correo electronico

        //enviar el correo electronico
        sendValidationEmail(user);

    }

    /**
     * Generated the email sending (generar el envio del correo electronico)
     * @param user
     */
    private void sendValidationEmail(User user) throws MessagingException {

        //paso 1:
        //generar y enviar el token de activacion
        var newToken = generateAndSavedActivationToken(user);

        //Paso 2:
        //sendEmail -> enviar correo electronico
        emailService.sendEmail(user.getEmail(), user.fullName(), EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation");


    }

    /**
     * Generate and save the activation token
     * @param user
     * @return activation token
     */
    private String generateAndSavedActivationToken(User user) {

        //generar token
        String generatedToken = generateAndSaveActivationCode(6);

        //guardar token
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepository.save(token);

        return generatedToken;

    }

    private String generateAndSaveActivationCode(int length) {

        String characters = "0123456789";
        //StringBuilder -> ES UN CONSTRUCTOR DE CADENA
        StringBuilder codeBuilder = new StringBuilder();
        // generar un valor aleatorio criptograficament seguro
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length()); //0 ... 9
            //chartAt regresa la posicion de un caracter de un cadena
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.fullName());

        var jwtToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    //token creado con los numeros del correo
    public void activateAccount(String token) throws MessagingException {

        Token savedToken = tokenRepository.findByToken(token)
                //todo exception has to be defined
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        // primero debemos validar si la hora local es despues  del token guardado
        if (LocalDateTime.now().isAfter(savedToken.getExpiredAt())) {
            //enviara un nuevo correo de validacion
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("""
                    Activation token has expired. A new token been sent to the same email address
                    """);
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidateAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
