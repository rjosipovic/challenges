package com.playground.user_manager.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Entity(name = "users")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, name = "alias")
    private String alias;

    public UserEntity() {
        this.alias = "";
    }

    public UserEntity(String alias) {
        this.alias = alias;
    }

    public UserEntity(UUID id, String alias) {
        this.id = id;
        this.alias = alias;
    }
}
