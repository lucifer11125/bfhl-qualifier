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
        System.out.println("====== [START] Application initialized. Executing task... ======");
        RestTemplate restTemplate = new RestTemplate();
        
        // 1. Generate Webhook
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Harsh"); // You can modify this
        requestBody.put("regNo", "0416");
        requestBody.put("email", "john@example.com"); // Replaced with dummy, you can update before running if required
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        
        System.out.println("-> Sending POST to " + generateUrl);
        ResponseEntity<Map> response = restTemplate.postForEntity(generateUrl, request, Map.class);
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map body = response.getBody();
            System.out.println("<- Received response: " + body);
            
            String accessToken = (String) body.getOrDefault("accessToken", "");
            String webhookUrl = (String) body.getOrDefault("webhook", "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA");
            
            // 2. Submit SQL query solution
            String finalQuery = 
                "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, " +
                "(SELECT COUNT(*) FROM EMPLOYEE e2 WHERE e2.DEPARTMENT = e1.DEPARTMENT AND e2.DOB > e1.DOB) AS YOUNGER_EMPLOYEES_COUNT " +
                "FROM EMPLOYEE e1 JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID ORDER BY e1.EMP_ID DESC;";
            
            Map<String, String> submitBody = new HashMap<>();
            submitBody.put("finalQuery", finalQuery);
            
            HttpHeaders submitHeaders = new HttpHeaders();
            submitHeaders.setContentType(MediaType.APPLICATION_JSON);
            submitHeaders.set("Authorization", accessToken); // Instructions say use as JWT token
            
            HttpEntity<Map<String, String>> submitRequest = new HttpEntity<>(submitBody, submitHeaders);
            
            System.out.println("-> Submitting final SQL query to " + webhookUrl);
            System.out.println("   Query: " + finalQuery);
            
            try {
                if (!webhookUrl.startsWith("http")) { // Safety check
                    webhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
                }
                ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhookUrl, submitRequest, String.class);
                System.out.println("<- Submission Response Status: " + submitResponse.getStatusCode());
                System.out.println("<- Submission Response Body: " + submitResponse.getBody());
                System.out.println("====== [SUCCESS] Task complete! ======");
            } catch (Exception e) {
                System.err.println("====== [ERROR] Failed to submit solution: " + e.getMessage() + " ======");
            }
        } else {
            System.err.println("====== [ERROR] Failed to generate webhook! Status: " + response.getStatusCode() + " ======");
        }
    }
}
