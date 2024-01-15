package com.suspend.configuration.entity;

import com.suspend.annotation.Column;
import com.suspend.annotation.Id;
import com.suspend.annotation.Table;

@Table(name = "test_model")
public class TestModel {
    @Id
    @Column
    private Integer id;
    @Column
    private String name;
    @Column
    private int age;
}

