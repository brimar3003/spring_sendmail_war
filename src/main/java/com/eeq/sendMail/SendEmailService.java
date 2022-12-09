package com.eeq.sendMail;

import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@RestController
public class SendEmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    
    @PostMapping("/send")
    public Map<String, String> sendEmail(@NonNull @RequestBody String payload) throws MessagingException{
        HashMap<String, String> response = new HashMap<>();
        JSONObject data = new JSONObject(payload.toString());
        
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("Brian Marquez");
        mimeMessageHelper.setTo(data.get("to").toString());
        mimeMessageHelper.setText(data.get("text").toString(), data.get("html").toString());
        mimeMessageHelper.setSubject(data.get("subject").toString());

        // byte[] xmlDecodedBytes = Base64.getDecoder().decode(data.get("xml").toString());
        // byte[] rideDecodedBytes = Base64.getDecoder().decode(data.get("ride").toString());

        // // FileSystemResource fileSystemResourceXml = new FileSystemResource(new File(data.get("xml").toString()));
        // // FileSystemResource fileSystemResourceRide = new FileSystemResource(new File(data.get("ride").toString()));

        // mimeMessageHelper.addAttachment(data.get("code").toString()+".xml", new ByteArrayResource(xmlDecodedBytes));
        // mimeMessageHelper.addAttachment(data.get("code").toString()+".pdf", new ByteArrayResource(rideDecodedBytes));
        // javaMailSender.send(mimeMessage);

        JSONArray attchments = new JSONArray(data.get("attchments").toString());

        for(int i=0; i<attchments.length(); i++){
            JSONObject attchment = attchments.getJSONObject(i);
            byte[] attchmentByte = Base64.getDecoder().decode(attchment.get("base64").toString());
            mimeMessageHelper.addAttachment(attchment.get("name").toString()+"."+attchment.get("ext"), new ByteArrayResource(attchmentByte));
        }

        javaMailSender.send(mimeMessage);

        response.put("response", "Email sent");
        
        return response;
    }

}
