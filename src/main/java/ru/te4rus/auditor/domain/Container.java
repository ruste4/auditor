package ru.te4rus.auditor.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Table(name = "containers")
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Container {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "full_container_weight", nullable = false)
    private Double fullContainerWeight;

    @Column(name = "empty_container_weight", nullable = false)
    private Double emptyContainerWeight;

    @Column(name = "container_capacity", nullable = false)
    private Double containerCapacity;

}
