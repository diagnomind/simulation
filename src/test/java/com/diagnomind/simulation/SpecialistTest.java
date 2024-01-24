package com.diagnomind.simulation;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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

    
    /** 
     * @throws InterruptedException
     */
    @Test
    public void interrupt() throws InterruptedException {
        doThrow(new InterruptedException()).when(hospitalMock).doDiagnosis();
        specialistTest.run();
        assertTrue(specialistTest.isInterrupted());
    }

}
