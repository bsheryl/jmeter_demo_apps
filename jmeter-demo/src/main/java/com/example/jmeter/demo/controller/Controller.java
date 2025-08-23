package com.example.jmeter.demo.controller;

import com.example.jmeter.demo.entity.Email;
import com.example.jmeter.demo.entity.User;
import com.example.jmeter.demo.repository.EmailRepository;
import com.example.jmeter.demo.repository.UserRepository;
import com.example.jmeter.demo.utils.Factorial;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class Controller {
    private final UserRepository userRepository;
    private final EmailRepository emailRepository;

    @GetMapping
    @Timed(value = "simple_get_method_execution", description = "Time taken to execute simple GET method")
    public ResponseEntity<User> get() {
        User user = new User();
        user.setLogin("myUser");
        user.setPassword("psw");
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @Transactional
    @Timed(value = "simple_post_method_execution", description = "Time taken to execute simple POST method")
    public ResponseEntity<User> post(@RequestBody User user) {
        if (user.getLogin() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Login or password is empty");
        }
        user.setCurrentDateTime(LocalDateTime.now());
        Email email = new Email();
        email.setLogin(user.getLogin());
        if (user.getEmail() == null) {
            email.setEmail(user.getLogin() + "@example.ru");
        } else {
            email.setEmail(user.getEmail().getEmail());
        }
        user.setEmail(email);
        userRepository.save(user);
//        emailRepository.save(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/withParam")
    @Transactional
    @Timed(value = "get_method_with_param_execution", description = "Time taken to execute GET method with param")
    public ResponseEntity<User> getWithParam(@RequestParam String login, @RequestParam String password,
                                             @RequestParam(required = false) String email) {
        if (login == null || password == null) {
            throw new IllegalArgumentException("Login or password is empty");
        }
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setCurrentDateTime(LocalDateTime.now());
        return ResponseEntity.ok(userRepository.save(addEmail(user, login, email)));
    }

    @GetMapping("/withHeader")
    @Transactional
    @Timed(value = "get_method_with_header_execution", description = "Time taken to execute GET method with header")
    public ResponseEntity<User> getWithHeader(@RequestHeader String login, @RequestHeader String password,
                                              @RequestHeader(required = false) String email) {
        if (login == null || password == null) {
            throw new IllegalArgumentException("Login or password is empty");
        }
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setCurrentDateTime(LocalDateTime.now());
        return ResponseEntity.ok(userRepository.save(addEmail(user, login, email)));
    }

    @GetMapping("/findByLogin/{login}")
    @Transactional
    @Timed(value = "find_by_login_method", description = "Time taken to execute GET method find user by login")
    public ResponseEntity<User> findByLogin(@PathVariable String login) {
        var user = userRepository.findById(login);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("No such user");
        }
        return ResponseEntity.ok(user.get());
    }

    private User addEmail(User user, String login, String email) {
        Email emailTmp = new Email();
        emailTmp.setLogin(login);
        if (email == null) {
            emailTmp.setEmail(login + "@example.ru");
        } else {
            emailTmp.setEmail(email);
        }
        user.setEmail(emailTmp);
        return user;
    }

    @KafkaListener(topics = "jmeter-topic", groupId = "myGroupId")
    @Timed(value = "kafka_consumer", description = "Time consume kafka topic")
    public void listenGroupFoo(User user) {
        Factorial.factorial(15);
        System.out.println("Received Message in group myGroupId: " + user);
    }
}
