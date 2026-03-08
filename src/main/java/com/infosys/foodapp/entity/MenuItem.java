package com.infosys.foodapp.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Positive
    @Column(nullable = false)
    private Double price;

    private String imageUrl;

    private String category;          // e.g. "Starters", "Main Course", "Desserts"

    @Column(nullable = false)
    private boolean isVeg = false;

    @Column(nullable = false)
    private boolean isAvailable = true;

    // Add this field to your MenuItem entity
    @Column(nullable = false)
    private boolean deleted = false;

    // ─── Relationships ───────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
}