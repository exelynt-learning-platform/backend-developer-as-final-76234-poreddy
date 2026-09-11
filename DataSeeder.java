package com.example.booking.config;

import com.example.booking.entity.AppUser;
import com.example.booking.entity.Role;
import com.example.booking.entity.ResourceEntity;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner init(UserRepository userRepository, ResourceRepository resourceRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("adminpass"));
                admin.setRoles(Set.of(Role.ROLE_ADMIN));
                userRepository.save(admin);
            }
            if (userRepository.findByUsername("user").isEmpty()) {
                AppUser user = new AppUser();
                user.setUsername("user");
                user.setPassword(encoder.encode("userpass"));
                user.setRoles(Set.of(Role.ROLE_USER));
                userRepository.save(user);
            }
            if (resourceRepository.count() == 0) {
                ResourceEntity r1 = new ResourceEntity(); r1.setName("Conference Room A"); r1.setDescription("Room with projector");
                ResourceEntity r2 = new ResourceEntity(); r2.setName("Projector"); r2.setDescription("Portable projector");
                resourceRepository.save(r1); resourceRepository.save(r2);
            }
        };
    }
}
