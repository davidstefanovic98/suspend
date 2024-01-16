package com.suspend.configuration.entity;

import com.suspend.annotation.Column;
import com.suspend.annotation.Id;
import com.suspend.annotation.OneToMany;
import com.suspend.annotation.Table;

import java.util.List;

@Table(name = "test_model_2")
public class TestModel2 {
    @Id
    @Column(name = "id")
    private Integer whatever;
    @Column
    private String name;
    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToMany(mappedBy = "testModel2")
    private List<TestModel> testModels;

    public TestModel2() {
    }
}
