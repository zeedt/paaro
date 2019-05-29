package com.plethub.paaro.core.models;

import javax.persistence.*;
import java.io.Serializable;

//@Entity (name = "authority_managed_user")
public class AuthorityManagedUser implements Serializable {

    @Transient
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "authority_id")
    private Authority authority;

    @Column(name = "managed_userList_id")
    private ManagedUser managedUser;
}
