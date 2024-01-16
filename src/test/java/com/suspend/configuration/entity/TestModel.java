package com.suspend.configuration.entity;

import com.suspend.annotation.*;

@Table(name = "test_model")
public class TestModel {
    @Id
    @Column
    private Integer id;
    @Column
    private String name;
    @Column
    private int age;

    @ManyToOne
    @JoinColumn(name = "test_model_2_id")
    private TestModel2 testModel2;

    public TestModel() {
    }
}

