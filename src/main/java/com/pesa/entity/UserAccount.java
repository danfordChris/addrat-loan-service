package com.pesa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserAccount {

    @Id
    private Long id;

    @Column(name = "pin")
    private String pin;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public enum UserStatus {
        ACTIVE, SUSPENDED, DELETED
    }

    public Long getId() {
        return id;
    }

    public String getPin() {
        return pin;
    }

    public UserStatus getStatus() {
        return status;
    }
}
