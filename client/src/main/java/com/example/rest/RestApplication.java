package com.example.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Запускает клиентскую часть приложения
 */
@SpringBootApplication
public class RestApplication {

    private RestTemplate restTemplate;

    /**
     * Точка входа в приложение
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }

    /**
     * Шаблон для работы с REST API
     * @return REST шаблон
     */
    @Bean
    public RestTemplate restTemplate() {
        this.restTemplate = new RestTemplate();
        return this.restTemplate;
    }

    /**
     * Получает все предметы одежды от сервера
     */
    public void createGetRequest(){
        String url = "http://localhost:8080/api/clothes";
        ResponseEntity<Clothes[]> response = restTemplate.getForEntity(url, Clothes[].class);
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response headers:");
        System.out.println(response.getHeaders());
        System.out.println("Response Code: " + response.getStatusCode());
        // Get the array of Clothes objects from the response
        Clothes[] clothesArray = response.getBody();
        System.out.println("Response body:");
        // Print each Clothes object
        if (clothesArray != null) {
            for (Clothes clothes : clothesArray) {
                System.out.println(clothes);
            }
        }
    }

    /**
     * Получает предмет одежды от сервера по индексу
     * @param id индекс запрашиваемого предмета одежды
     */
    public void createGetByIDRequest(Integer id){
        String url = "http://localhost:8080/api/clothes/" + id;
        // Send the request
        try {
            ResponseEntity<Clothes> response = restTemplate.getForEntity(url, Clothes.class);
            System.out.println("Response Code: " + response.getStatusCode());
            System.out.println("Response headers:");
            System.out.println(response.getHeaders());
            System.out.println("Response body:");
            System.out.println(response.getBody());
        }
        catch (HttpClientErrorException e) {
            System.out.println("Response Code: " + e.getStatusCode());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                System.out.println("Clothes with id " + id + " not found");
            } else {
                throw e;  // rethrow the exception if it's not a 404 error
            }
        }
    }

    /**
     * Отправляет предмет одежды на сервер
     * @param clothes новый предмет одежды
     */
    public void createPostRequest(Clothes clothes){
        String url = "http://localhost:8080/api";
        ObjectMapper mapper = new ObjectMapper();
        try{
            String clothesJson = mapper.writeValueAsString(clothes);
            // Set the headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Create a new HttpEntity
            HttpEntity<String> entity = new HttpEntity<>(clothesJson, headers);
            // Send the request
            ResponseEntity<Clothes> response = restTemplate.postForEntity(url, entity, Clothes.class);
            System.out.println("Response Status : " + response.getStatusCode());
            System.out.println("Response headers:");
            System.out.println(response.getHeaders());
            System.out.println("Response Body\n" + response.getBody());
        }
        catch (JsonProcessingException e){
            System.out.println("Impossible to process json file");
        }
    }

    /**
     * Отправляет модифиццированный предмет одеждыы на сервер
     * @param id индекс заменяемого прндмета одежды
     * @param clothes изменённый предмет одежды
     */
    public void createPutRequest(Integer id, Clothes clothes){
        String url = "http://localhost:8080/api/" + clothes.getId();
        ObjectMapper mapper = new ObjectMapper();
        try{
            String clothesJson = mapper.writeValueAsString(clothes);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Create a new HttpEntity
            HttpEntity<String> entity = new HttpEntity<>(clothesJson, headers);
            try{
                restTemplate.put(url, entity);
                System.out.println("Successfully edited an object\n" + clothes);
            }
            catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    System.out.println("Clothes with id " + id + " not found");
                } else {
                    throw e;  // rethrow the exception if it's not a 404 error
                }
            }
        }
        catch(JsonProcessingException e){
            System.out.println("Impossible to process json file");
        }
    }

    /**
     * Запрашивает удаление предмета одежды на сервере
     * @param id индекс удаляемого предмета одежды
     */
    public void createDeleteRequest(Integer id){
        try {
            // Send the request
            restTemplate.delete("http://localhost:8080/api/delete/" + id);

            System.out.println("Clothes with id " + id + " deleted successfully");
        } catch (HttpClientErrorException e) {
            // Handle the exception
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                System.out.println("Clothes with id " + id + " not found");
            } else {
                throw e;  // rethrow the exception if it's not a 404 error
            }
        }
    }

    /**
     * Запускает работу приложения в коммандной строке
     * @return CommandLineRunner
     */
    @Bean
    public CommandLineRunner run() {
        return args -> {
            System.out.println("\n------------------------------ Getting all clothes ------------------------------");
            createGetRequest();
            System.out.println("\n--------------------------- Getting clothes by its ID ---------------------------");
            createGetByIDRequest(1);
            System.out.println("\n--------------------------- Posting new clothes item ---------------------------");
            Clothes newClothes = new Clothes("T-shirt", "white", "H&M",48, 8000, 6);
            createPostRequest(newClothes);
            System.out.println("\n--------------------------- Editing clothes by its ID ---------------------------");
            Clothes clothes = new Clothes("skirt", "blue", "Zara",46, 12000, 1);
            int id = 1;
            clothes.setId(id);
            createPutRequest(id, clothes);
            System.out.println("\n--------------------------- Deleting clothes by its ID ---------------------------");
            createDeleteRequest(2);
        };
    }
}
