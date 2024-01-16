package com.suspend.configuration;

import com.suspend.annotation.Table;
import com.suspend.configuration.entity.TestModel;
import com.suspend.configuration.repository.TestRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {

    @Test
    void testConfiguration() {
        Configuration configuration = Configuration.getInstance();
        configuration.setRepositoryPackageName("com.suspend.configuration.repository");
        configuration.addAnnotatedClass(Table.class);

        assertEquals(1, configuration.getRepositoryMap().size());
        assertTrue(configuration.getRepositoryMap().containsValue(TestRepository.class));
        assertTrue(configuration.getRepositoryMap().containsKey(TestModel.class));
    }
}