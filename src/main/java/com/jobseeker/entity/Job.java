package com.jobseeker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name="job")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String company;

    private String location;

    private String description;

    private String skills;

    private Integer salary;
}