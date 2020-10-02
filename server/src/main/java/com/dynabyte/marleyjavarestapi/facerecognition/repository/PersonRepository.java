package com.dynabyte.marleyjavarestapi.facerecognition.repository;

import com.dynabyte.marleyjavarestapi.facerecognition.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PersonRepository extends CrudRepository<Person, String> {

//    @Override
//    Optional<Person> findById(String s);
}
