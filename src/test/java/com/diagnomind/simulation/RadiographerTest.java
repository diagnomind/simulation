package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;

public class RadiographerTest {

    Radiographer radioTest;
    Hospital hospitalMock;

    @Before
    public void setup() {
        hospitalMock = mock(Hospital.class);
        radioTest = new Radiographer(hospitalMock, 1);
    }

    @After
    public void clear() {
        hospitalMock = null;
    }

    @Test
    public void getSetHospitalTest() {
        radioTest.setHospital(hospitalMock);
        assertEquals(hospitalMock, radioTest.getHospital());
    }

}
