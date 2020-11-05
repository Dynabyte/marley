package com.dynabyte.marleyrest.calendar.repository;

import com.dynabyte.marleyrest.calendar.model.GoogleTokens;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoogleTokensRepository extends CrudRepository<GoogleTokens, String> {

}
