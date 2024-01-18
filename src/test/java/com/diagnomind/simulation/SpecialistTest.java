package com.diagnomind.simulation;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SpecialistTest {

    Hospital hospitalMock;
    Specialist specialistTest;

    @Before
    public void setup() {
        hospitalMock = mock(Hospital.class);
        specialistTest = new Specialist(hospitalMock, 1);
    }

    @After
    public void clear() {
        hospitalMock = null;
    }

}
