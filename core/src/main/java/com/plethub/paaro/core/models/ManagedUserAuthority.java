package com.plethub.paaro.core.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity (name = "managed_user_authority")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"managed_user_id", "authority_id"}))
public class ManagedUserAuthority implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "managed_user_id")
    private Long managedUserId;

    @NotNull
    @Column(name = "authority_id")
    private Long authorityId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getManagedUserId() {
        return managedUserId;
    }

    public void setManagedUserId(Long managedUserId) {
        this.managedUserId = managedUserId;
    }

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }
}
