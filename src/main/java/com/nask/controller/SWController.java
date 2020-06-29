package com.nask.controller;

import com.nask.model.Page;
import com.nask.model.Person;
import com.nask.model.RequestFailed;
import com.nask.swapireader.CacheBuilder;
import com.nask.swapireader.PageInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class SWController {

    @Autowired
    private CacheBuilder cacheBuilder;

    @Autowired
    private PageInitializer pageInitializer;

    @GetMapping(value = "/characters/{id}")
    public ResponseEntity getCharacterById(@PathVariable(value = "id") int id) {
        Map<Integer, Person> characters = cacheBuilder.getPeople();
        if (characters.get(id)==null) {
            RequestFailed requestFailed = new RequestFailed(HttpStatus.NOT_FOUND, String.format("Character with id %s not found.", id));
            return new ResponseEntity(requestFailed, requestFailed.getStatus());
        }
        return new ResponseEntity(characters.get(id), HttpStatus.OK);
    }

    @GetMapping(value = "/characters")
    public ResponseEntity getCharacterPages(@RequestParam int page) {
        Page pageResult = pageInitializer.getPage(page);
        if(pageResult.getElements().equals(Collections.EMPTY_LIST)) {
            RequestFailed requestFailed = new RequestFailed(HttpStatus.NOT_FOUND, String.format("Page %s not found. Last page number = %s", page, pageResult.getPages()));
            return new ResponseEntity(requestFailed, requestFailed.getStatus());
        }
        return new ResponseEntity(pageInitializer.getPage(page), HttpStatus.OK);
    }

}
