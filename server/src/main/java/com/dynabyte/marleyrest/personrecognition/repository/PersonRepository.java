package com.dynabyte.marleyrest.personrecognition.repository;

import com.dynabyte.marleyrest.personrecognition.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * Repository for entity Person
 */
@Repository
public interface PersonRepository extends CrudRepository<Person, String> {

}
