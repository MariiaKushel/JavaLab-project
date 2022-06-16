package com.epam.esm.dao.entity;

import com.epam.esm.enumeration.UserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

/**
 * Class represent User entity
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "orders")
@ToString(callSuper = true, exclude = "orders")
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "login")
    private String login;
    @Column(name = "password")
    private String password;
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Order> orders;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
