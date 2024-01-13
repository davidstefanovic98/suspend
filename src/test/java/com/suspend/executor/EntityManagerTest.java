package com.suspend.executor;

import com.suspend.reflection.TableMapper;
import com.suspend.reflection.TableMetadata;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerTest {

    public static final class TestModel {
        private String name;
        private int age;
    }

    @Test
    void testFindAll() {
        EntityManager entityManager = new EntityManager();
        List<TestModel> findAll = entityManager.findAll(TestModel.class);
        assertEquals(1, findAll.size());
        assertEquals("David", findAll.get(0).name);
    }

}