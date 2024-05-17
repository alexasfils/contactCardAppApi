package io.getarrays.contanctapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.getarrays.contanctapi.domain.Contact;

@Repository
public interface ContactsRepository extends JpaRepository<Contact, String> {
	public Optional<Contact> findContactById(String id);

}
