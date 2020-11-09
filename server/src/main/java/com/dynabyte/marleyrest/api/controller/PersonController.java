package com.dynabyte.marleyrest.api.controller;

import com.dynabyte.marleyrest.personrecognition.model.Person;
import com.dynabyte.marleyrest.personrecognition.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<Person> savePerson(@RequestBody Person person){
        personService.save(person);
        return ResponseEntity.ok(person);
    }

    @GetMapping
    public ResponseEntity<List<Person>> getPeople(){
        List<Person> people = personService.getAllPeople();
        return ResponseEntity.ok(people);
    }

    @DeleteMapping("/{id}")
    public HttpStatus deletePerson(@PathVariable String id){
        personService.deleteById(id);
        return HttpStatus.OK;
    }

//    @GetMapping("/{id}")
}
