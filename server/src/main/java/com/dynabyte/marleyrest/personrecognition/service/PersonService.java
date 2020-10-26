package com.dynabyte.marleyrest.personrecognition.service;

import com.dynabyte.marleyrest.personrecognition.model.Person;
import com.dynabyte.marleyrest.personrecognition.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for making database CRUD operations for entity Person
 */
@Service
public class PersonService {

    private final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Searches the SQL database for a person with a specific faceId
     *
     * @param faceId the faceId that the person's face encodings are stored with in the face recognition database
     * @return An Optional Person
     */
    public Optional<Person> findById(String faceId) {
        LOGGER.debug("Searching database by faceId " + faceId);
        return personRepository.findById(faceId);
    }

    public void deleteById(String faceId){
        LOGGER.debug("Deleting person with faceId " + faceId);
        personRepository.deleteById(faceId);
    }

    /**
     * Saves a person object to the SQL database
     *
     * @param person An object matching the Person entity
     */
    public void save(Person person) {
        personRepository.save(person);
        LOGGER.info("Person saved to database: " + person);
    }
}
