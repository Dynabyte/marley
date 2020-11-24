package com.dynabyte.marleyrest.personrecognition.service;

import com.dynabyte.marleyrest.personrecognition.model.Person;
import com.dynabyte.marleyrest.personrecognition.repository.PersonRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    PersonRepository personRepository;

    @InjectMocks
    PersonService personService;

    private static String faceId;
    private static Person person;

    @BeforeAll
    static void setup(){
        faceId = "5fad4b9e1bbac38873e8cdbd";
        person = new Person(faceId, "First-name Last-name");
    }

    @Test
    void findById() {
        when(personRepository.findById(faceId)).thenReturn(Optional.of(person));
        Optional<Person> personOptional = personService.findById(faceId);
        assertThat(personOptional.isPresent());
        verify(personRepository).findById(faceId);
    }

    @Test
    void deleteById() {
        personService.deleteById(faceId);
        verify(personRepository).deleteById(faceId);
    }

    @Test
    void save() {
        personService.save(person);
        verify(personRepository).save(person);
    }
}