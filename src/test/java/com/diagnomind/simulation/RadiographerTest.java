package com.diagnomind.simulation;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class RadiographerTest {

    Radiographer radioTest;
    Hospital hospital = new Hospital();

    @Before
    public void setup() {
        radioTest = new Radiographer(hospital);
    }

    @Test
    public void getSetHospitalTest() {
        radioTest.setHospital(hospital);
        assertEquals(hospital,radioTest.getHospital() );
    }

}