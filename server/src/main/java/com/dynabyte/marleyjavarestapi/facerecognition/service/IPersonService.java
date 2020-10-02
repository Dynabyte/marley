package com.dynabyte.marleyjavarestapi.facerecognition.service;

import com.dynabyte.marleyjavarestapi.facerecognition.model.Person;

import java.util.Optional;

public interface IPersonService {

    Optional<Person> findById(String id);

    void save(Person person);
}
