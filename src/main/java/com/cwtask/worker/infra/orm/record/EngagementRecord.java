package com.cwtask.worker.infra.orm.record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "engagement-mgmt-sch", name = "engagements-tbl")
@Getter
@Setter
public class EngagementRecord {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;
}
