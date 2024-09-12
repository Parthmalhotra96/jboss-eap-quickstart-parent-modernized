package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.unit.util;

import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.util.GenericIdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GenericIdGeneratorTest {

    @InjectMocks
    private GenericIdGenerator genericIdGenerator;

    @Test
    public void testGetNextId() {
        long firstId = genericIdGenerator.getNextId();
        long secondId = genericIdGenerator.getNextId();

        assertEquals(1, firstId);
        assertEquals(2, secondId);
    }
}
