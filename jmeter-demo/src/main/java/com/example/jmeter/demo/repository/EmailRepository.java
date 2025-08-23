package com.example.jmeter.demo.repository;

import com.example.jmeter.demo.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email, String> {
    Email findByLogin(String login);
}
