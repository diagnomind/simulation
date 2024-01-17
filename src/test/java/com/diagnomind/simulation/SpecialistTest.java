package com.diagnomind.simulation;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Test;

public class SpecialistTest {

    Hospital hospitalMock;
    Specialist specialistTest;

    @Test
    public void constructor() {
        hospitalMock = mock(Hospital.class);
        specialistTest = new Specialist(hospitalMock, 1);
        assertNotNull(specialistTest);
    }

}
