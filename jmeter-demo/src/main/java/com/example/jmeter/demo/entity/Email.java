package com.example.jmeter.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Email {
    @Id
    private String login;
    private String email;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "login", referencedColumnName = "login", nullable = false)
    private User user;
}
