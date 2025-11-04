// java
package com.todoapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Objects;

@Document(collection = "contacts")
public class Contact {
    @Id
    private String id;
    private String name;
    private String email;
    private String phone;
    private Instant createdAt;

    public Contact() {
        this.createdAt = Instant.now();
    }

    public Contact(String id, String name, String email, String phone, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
