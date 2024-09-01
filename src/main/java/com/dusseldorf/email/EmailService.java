package com.dusseldorf.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    //Remitente de correo
    private final JavaMailSender javaMailSender;

    //para enviar plantillas html
    private final SpringTemplateEngine templateEngine;

    //crear un correo electronico publico
    //enviar correo

    /**
     *
     * @param to -> aquien se va enviar el correo electrónico
     * @param username -> el nombre del usuario
     * @param emailTemplateName -> plantilla de correo electronico
     * @param confirmationUrl -> para que el usuario pueda confirmar su cuenta
     * @param activationCode -> codigo de activación
     * @param subject -> el asunto del correo electrónico
     */
    //enviar el correo puede llevar mucho tiempo, por eso el envia el correo de forma asincrona
    //el la clase que tiene el metodo main tenemos que habilitar -> @EnableAsync
    @Async
    public void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplateName,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException {

        //nombre de la plantilla
        String templateName;

        if (emailTemplateName == null){
            templateName = "Confirm_email";
        }else {
            templateName = emailTemplateName.name();
        }

        //confirmar nuestro correo remitente
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        // mi mensaje ayudante
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        // COMO PASAMOS ALGUNOS PARAMETROS A NUESTRA PLANTILLA
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activationCode", activationCode);

        //creamos un objeto de tipo contexto -> del paquete de Thymeleaf
        Context context = new Context();
        //hacer que el contexto establesca las variables correctamente -> necesitamos pasar el mapa
        context.setVariables(properties);

        /**
         * agregar la propiedad de nuestro correo
         */
        //quien envia el correo
        helper.setFrom("javajunior10@gmail.com");

        //a quien quiero enviar el correo
        helper.setTo(to);

        //el tema
        helper.setSubject(subject);

        //procesar la plantilla
        String template = templateEngine.process(templateName, context);
        helper.setText(template, true);

        //enviar el mensaje
        javaMailSender.send(mimeMessage);

        //vido 1:44

    }

}
