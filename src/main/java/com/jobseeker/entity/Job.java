package com.jobseeker.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Job {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String company;

    private String location;

    private String description;

    private String skills;

    private Integer salary;

    private LocalDate postedDate;
}