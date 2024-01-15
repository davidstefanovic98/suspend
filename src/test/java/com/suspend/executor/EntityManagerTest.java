package com.suspend.executor;

import com.suspend.annotation.*;
import com.suspend.exception.InvalidMappingException;
import com.suspend.repository.Repository;
import com.suspend.repository.RepositoryFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityManagerTest {

    @Table(name = "test_model")
    public static class TestModelIncorrect {
        @Id
        private Integer whatever;
        @Column
        private String name;
        @Column
        private int age;
    }

    @Table(name = "test_model")
    public static class TestModelCorrect {
        @Id
        @Column(name = "id")
        private Integer whatever;
        @Column
        private String name;
        @Column
        private int age;
    }

    @Test
    void testFindAllIncorrect() {
        Repository<TestModelIncorrect, Integer> repository = RepositoryFactory.createRepository(TestModelIncorrect.class);
        assertThrows(InvalidMappingException.class, repository::findAll);
    }

    @Test
    void testFindAllCorrect() {
        Repository<TestModelCorrect, Integer> repository = RepositoryFactory.createRepository(TestModelCorrect.class);
        List<TestModelCorrect> findAll = repository.findAll();
        assertEquals(2 , findAll.get(0).whatever);
        assertEquals("John", findAll.get(0).name);
    }

    @Test
    void testSave() {
        Repository<TestModelCorrect, Integer> repository = RepositoryFactory.createRepository(TestModelCorrect.class);
        TestModelCorrect model = new TestModelCorrect();
        model.name = "John";
        model.age = 30;
        TestModelCorrect savedModel = repository.save(model);
        assertEquals("John", savedModel.name);
        assertEquals(30, savedModel.age);
    }

    @Test
    void testFindById() {
        Repository<TestModelCorrect, Integer> repository = RepositoryFactory.createRepository(TestModelCorrect.class);
        TestModelCorrect model = repository
                .findById(2)
                .orElseThrow(()  -> new RuntimeException("Model not found."));
        assertThrows(RuntimeException.class, () -> repository.findById(1).orElseThrow(() -> new RuntimeException("Model not found.")));
        assertEquals("John", model.name);
    }

    @Test
    void testUpdate() {
        Repository<TestModelCorrect, Integer> repository = RepositoryFactory.createRepository(TestModelCorrect.class);
        TestModelCorrect model = repository
                .findById(2)
                .orElseThrow(() -> new RuntimeException("Model not found."));

        model.name = "John";
        TestModelCorrect updatedModel = repository.update(model);
        assertThrows(RuntimeException.class, () -> repository.findById(1).orElseThrow(() -> new RuntimeException("Model not found.")));
        assertEquals("John", updatedModel.name);
    }
}