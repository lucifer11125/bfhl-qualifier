package com.bfhl.qualifier;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import java.util.Map;
import java.util.HashMap;

@Component
public class WebhookRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        
        // 1. On app startup, send this POST request:
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "John Doe");
        requestBody.put("regNo", "0416");
        requestBody.put("email", "john@example.com");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(generateUrl, request, Map.class);
        
        // 2. You will receive a 'webhook' URL and an 'accessToken'
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map body = response.getBody();
            String webhookUrl = (String) body.get("webhook");
            String accessToken = (String) body.get("accessToken");
            
            // 3 & 4. Submit your solution (the final SQL query)
            String finalQuery = 
                "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, " +
                "(SELECT COUNT(*) FROM EMPLOYEE e2 WHERE e2.DEPARTMENT = e1.DEPARTMENT AND e2.DOB > e1.DOB) AS YOUNGER_EMPLOYEES_COUNT " +
                "FROM EMPLOYEE e1 JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID ORDER BY e1.EMP_ID DESC;";
            
            Map<String, String> submitBody = new HashMap<>();
            submitBody.put("finalQuery", finalQuery);
            
            HttpHeaders submitHeaders = new HttpHeaders();
            submitHeaders.setContentType(MediaType.APPLICATION_JSON);
            submitHeaders.set("Authorization", accessToken);
            
            HttpEntity<Map<String, String>> submitRequest = new HttpEntity<>(submitBody, submitHeaders);
            restTemplate.postForEntity(webhookUrl, submitRequest, String.class);
        }
    }
}
