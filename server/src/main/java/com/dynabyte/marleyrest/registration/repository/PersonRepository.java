package com.dynabyte.marleyrest.registration.repository;

import com.dynabyte.marleyrest.registration.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * Repository for entity Person
 */
@Repository
public interface PersonRepository extends CrudRepository<Person, String> {

}
