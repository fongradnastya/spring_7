package com.example.demo.controllers;

import com.example.demo.models.Clothes;
import com.example.demo.services.ClothesService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер REST API
 */
@RestController
@RequestMapping("/api")
public class RestClothesController {
    private static final Logger logger = LoggerFactory.getLogger(ClothesController.class);

    private final ClothesService clothesService;

    /**
     * Конструктор класса RestClothesController
     * @param clothesService сервис для работы с предметами одежды
     */
    @Autowired
    public RestClothesController(ClothesService clothesService) {
        this.clothesService = clothesService;
    }

    /**
     * Возвращает все записи о предметах одежды
     * @return список предметов одежды
     */
    @GetMapping("/clothes")
    public List<Clothes> getAll() {
        return clothesService.findAll();
    }

    /**
     * Возвращает предмет одежды с заданным id
     * @param id индекс предмета одежды
     * @return ответ на запрос
     */
    @GetMapping("/clothes/{id}")
    public ResponseEntity<Clothes> getById(@PathVariable("id") int id) {
        Clothes clothes = clothesService.findOne(id);
        if (clothes != null) {
            return new ResponseEntity<>(clothes, HttpStatus.OK);
        } else {
            logger.error("Clothes with id {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Добавляет предмет одежды в базу данных
     * @param clothes новый предмет одежды
     * @return ответ на запрос
     */
    @PostMapping
    public ResponseEntity<Clothes> create(@RequestBody @Valid Clothes clothes) {
        clothesService.save(clothes);
        return new ResponseEntity<>(clothes, HttpStatus.CREATED);
    }

    /**
     * Обновляет предмет одежды по индексу
     * @param clothes обновлённый предмет одежды
     * @param id индекс изменяемого предмета одежды
     * @return ответ на запрос
     */
    @PutMapping("/{id}")
    public ResponseEntity<Clothes> update(@RequestBody @Valid Clothes clothes, @PathVariable("id") int id) {
        if (clothesService.doesNotExist(id)) {
            logger.error("Attempted to update non-existing clothes with id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        clothesService.update(id, clothes);
        return new ResponseEntity<>(clothes, HttpStatus.OK);
    }

    /**
     * Удаляет предмет одежды по индексу
     * @param id индекс удаляемого объекта
     * @return ответ на запрос
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClothes(@PathVariable("id") int id) {
        if (clothesService.doesNotExist(id)) {
            logger.error("Attempted to delete non-existing clothes with id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        clothesService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
