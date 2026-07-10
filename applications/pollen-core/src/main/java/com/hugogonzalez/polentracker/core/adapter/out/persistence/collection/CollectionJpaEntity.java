package com.hugogonzalez.polentracker.core.adapter.out.persistence.collection;
import com.hugogonzalez.polentracker.core.domain.model.*; import com.hugogonzalez.polentracker.domain.PollenSourceType; import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="collections")
class CollectionJpaEntity {
 @Id UUID id; @Enumerated(EnumType.STRING) @Column(nullable=false) PollenSourceType sourceType; @Enumerated(EnumType.STRING) @Column(nullable=false) CollectionStatus status;
 @Column(nullable=false) Instant requestedAt; Instant completedAt; int producedItems;
 protected CollectionJpaEntity() {}
 CollectionJpaEntity(PollenCollection c){ id=c.id(); sourceType=c.sourceType(); status=c.status(); requestedAt=c.requestedAt(); completedAt=c.completedAt(); producedItems=c.producedItems(); }
 PollenCollection toDomain(){ return new PollenCollection(id,sourceType,status,requestedAt,completedAt,producedItems); }
}
