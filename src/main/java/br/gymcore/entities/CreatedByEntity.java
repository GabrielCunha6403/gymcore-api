package br.gymcore.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class CreatedByEntity extends CreatedAtEntity {

    @Column(name = "created_by")
    private UUID createdBy;
}
