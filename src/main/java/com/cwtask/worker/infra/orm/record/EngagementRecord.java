package com.cwtask.worker.infra.orm.record;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(schema = "engagement-mgmt-sch", name = "engagements-tbl")
@Getter
@Setter
public class EngagementRecord {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;
}
