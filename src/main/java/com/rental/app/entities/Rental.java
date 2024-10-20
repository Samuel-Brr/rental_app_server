package com.rental.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RENTALS")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal surface;
    private BigDecimal price;
    private String picture;
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private User owner;

    @JsonProperty("created_at")
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Rental(String name, BigDecimal bigDecimal, BigDecimal bigDecimal1, String pictureUrl, String description) {
        this.name = name;
        this.surface = bigDecimal;
        this.price = bigDecimal1;
        this.picture = pictureUrl;
        this.description = description;
    }

    @JsonProperty("owner_id")
    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }
}