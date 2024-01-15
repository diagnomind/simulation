package com.diagnomind.simulation;

import static org.mockito.Mockito.mock;

import org.junit.Before;

public class SpecialistTest {
    
    Hospital hospitalMock;
    Specialist specialistTest;

    @Before
    public void setup() {
        hospitalMock = mock(Hospital.class);
        specialistTest = new Specialist(hospitalMock, 1);
    }

}
