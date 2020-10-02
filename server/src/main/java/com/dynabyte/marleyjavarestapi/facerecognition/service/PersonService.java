package com.dynabyte.marleyjavarestapi.facerecognition.service;

import com.dynabyte.marleyjavarestapi.facerecognition.model.Person;
import com.dynabyte.marleyjavarestapi.facerecognition.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonService implements IPersonService{

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Optional<Person> findById(String id) {
        return personRepository.findById(id);
    }

    @Override
    public void save(Person person) {
        personRepository.save(person);
    }
}
