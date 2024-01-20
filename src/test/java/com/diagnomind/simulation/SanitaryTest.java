package com.diagnomind.simulation;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SanitaryTest {
    
    Sanitary sanitaryTest;
    Hospital hospitalMock;

        @Before
    public void setup() {
        hospitalMock = mock(Hospital.class);
        sanitaryTest = new Sanitary(hospitalMock, 1);
    }

    @After
    public void clear() {
        hospitalMock = null;
    }

    @Test
    public void interrupt() throws InterruptedException {
        doThrow(new InterruptedException()).when(hospitalMock).attendPacient();
        sanitaryTest.run();
        assertTrue(sanitaryTest.isInterrupted());
    }
}
