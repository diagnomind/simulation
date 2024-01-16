package com.diagnomind.simulation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Hospital {

    private static final int CAPACITY = 4;
    private static final int NUM_DOCTORS = 3;
    private static final int NUM_PATIENTS = 10;
    private static final int NUM_RADIOGRAPHERS = 2;
    private static final int NUM_SPECIALISTS = 2;

    private Patient[] patients;
    private Sanitary[] doctors;
    private Specialist[] specialists;
    private Radiographer[] radiographers;

    private int numPatientsEntered;
    private int numPatientsRadiography;
    private int numRadiographys;
    private int totalTime;
    private int availableDoctors;

    private Lock mutex;
    private Condition docWait;
    private Condition patientWait;
    private Condition patientWaitRadiography;
    private Condition radWait;
    private Condition specialistWait;
    private Condition firstWaitingRoomFull;
    private Condition secondWaitingRoomFull;
    private BlockingQueue<Patient> firstWaitingRoom;
    private BlockingQueue<Patient> secondWaitingRoom;
    private BlockingQueue<Diagnosis> diagnosisToAprove;
    private BlockingQueue<Patient> patientResults;

    public Hospital() {
        this.patients = new Patient[NUM_PATIENTS];
        this.doctors = new Sanitary[NUM_DOCTORS];
        this.specialists = new Specialist[NUM_SPECIALISTS];
        this.radiographers = new Radiographer[NUM_RADIOGRAPHERS];

        this.totalTime = 0;
        this.numRadiographys = 0;
        this.numPatientsEntered = 0;
        this.numPatientsRadiography = 0;
        this.availableDoctors = 0;

        this.mutex = new ReentrantLock();
        this.docWait = mutex.newCondition();
        this.radWait = mutex.newCondition();
        this.patientWait = mutex.newCondition();
        this.specialistWait = mutex.newCondition();
        this.firstWaitingRoomFull = mutex.newCondition();
        this.secondWaitingRoomFull = mutex.newCondition();
        this.patientWaitRadiography = mutex.newCondition();

        this.firstWaitingRoom = new LinkedBlockingQueue<>();
        this.secondWaitingRoom = new LinkedBlockingQueue<>();
        this.diagnosisToAprove = new LinkedBlockingQueue<>();
        this.patientResults = new LinkedBlockingQueue<>();
    }

    /* Patient */
    @SuppressWarnings("java:S106")
    public void firstWaitingRoom(Patient patient) throws InterruptedException {
        mutex.lock();
        try {
            if (!patient.getItsAttended()) {
                System.out.println("[" + patient.getName() + "] enters the hospital");
                while (numPatientsEntered == CAPACITY) {
                    System.out.println("[Waitingroom 1]: Full");
                    firstWaitingRoomFull.await();
                }
                patient.setTiempoInit(System.currentTimeMillis());
                numPatientsEntered++;
                firstWaitingRoom.put(patient);
                System.out.println("[Waitingroom 1]: " + patient.getName() + " enters");

                while (!patient.getItsAttended()) {
                    docWait.signal();
                    patientWait.await();
                }

                System.out.println("[Waitingroom 1]: " + patient.getName() + " gets out");
                numPatientsEntered--;
                firstWaitingRoomFull.signal();
                patientWait.signal();
                patient.sendToRadiography();
            }
        } finally {
            mutex.unlock();
        }
    }

    /* Doc */
    @SuppressWarnings("java:S106")
    public void attendPacient() throws InterruptedException {
        mutex.lock();
        try {
            if (availableDoctors < NUM_DOCTORS / 2) {
                availableDoctors++;
                while (firstWaitingRoom.isEmpty()) {
                    docWait.await();
                }
                Patient toEvaluate = firstWaitingRoom.take();
                System.out.println("\t[" + Thread.currentThread().getName() + "]: Evaluating " + toEvaluate.getName());
                Thread.sleep(1000);
                System.out.println("\t\t[" + Thread.currentThread().getName() + "]: Evaluation done");
                toEvaluate.itsAttended();
                patientWait.signal();
                availableDoctors--;
            }
        } finally {
            mutex.unlock();
        }
    }

    /* Patient */
    @SuppressWarnings("java:S106")
    public void secondWaitingRoom(Patient patient) throws InterruptedException {
        mutex.lock();
        try {
            if (patient.getCanDoRadiography() && !patient.getRadiographyDone()) {
                while (numPatientsRadiography == CAPACITY) {
                    System.out.println("[Waitingroom 2]: Full");
                    secondWaitingRoomFull.await();
                }
                numPatientsRadiography++;
                secondWaitingRoom.put(patient);
                System.out.println("[Waitingroom 2]: " + patient.getName() + " enters");
                while (!patient.getRadiographyDone()) {
                    radWait.signal();
                    patientWaitRadiography.await();
                }
                System.out.println("[Waitingroom 2]: " + patient.getName() + " gets out");
                numPatientsRadiography--;
                secondWaitingRoomFull.signal();
                patientWaitRadiography.signal();
            }
        } finally {
            mutex.unlock();
        }
    }

    /* Radiographer */
    @SuppressWarnings("java:S106")
    public void doRadiographyToPacient() throws InterruptedException {
        mutex.lock();
        try {
            while (secondWaitingRoom.isEmpty()) {
                radWait.await();
            }
            Patient toEvaluate = secondWaitingRoom.take();
            System.out.println("\t[" + Thread.currentThread().getName() + "]: Scanning " + toEvaluate.getName());
            Thread.sleep(1000);
            System.out.println("\t\t[" + Thread.currentThread().getName() + "]: Radiography done");
            toEvaluate.radiographyDone();
            numRadiographys++;
            sendImageToSpecialist(toEvaluate);
            patientWaitRadiography.signal();
        } finally {
            mutex.unlock();
        }
    }

    /* Radiographer */
    @SuppressWarnings("java:S106")
    public void sendImageToModel(Patient diagnosisPatient) throws InterruptedException, IOException {
        /* Conseguir el diagnosis del modelo y depositarlo */
        mutex.lock();
        try {
            if (numRadiographys != 0) {
                URL url = new URL("");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                if (response == 200) {
                    /*
                     * Aqui cambiar si el diagnostico es true o false en base al accuracy del modelo
                     */
                    Diagnosis resultado = new Diagnosis(true, diagnosisPatient);
                    diagnosisToAprove.put(resultado);
                } else {
                    System.out.println("\tError connecting to the server");
                }
                numRadiographys--;
                specialistWait.signal();
            }
        } finally {
            mutex.unlock();
        }
    }

    /* Radiographer */
    @SuppressWarnings("java:S106")
    public void sendImageToSpecialist(Patient diagnosisPatient) throws InterruptedException {
        mutex.lock();
        try {
            if (numRadiographys != 0) {
                /* Tiempo en el que el radiografo interpreta la imagen */
                Thread.sleep(2000);
                /*
                 * Aqui podemos hacer probabilidad de fallo humano, que el 0.05 por ciento de
                 * las veces sea erroneo por ejemplo
                 */
                Diagnosis resultado = new Diagnosis(true, diagnosisPatient);
                diagnosisToAprove.put(resultado);
                System.out.println("\t\t\t[" + Thread.currentThread().getName() + "]: Image sent");
                numRadiographys--;
                specialistWait.signal();
            }
        } finally {
            mutex.unlock();
        }
    }

    /* Specialist */
    @SuppressWarnings("java:S106")
    public void doDiagnosis() throws InterruptedException {
        mutex.lock();
        try {
            while (diagnosisToAprove.isEmpty()) {
                specialistWait.await();
            }
            Patient diagnosedPatient = diagnosisToAprove.take().getPatient();
            /* Tiempo para hacer un diagnostico */
            Thread.sleep(2000);
            System.out.println("\t\t\t\t[" + Thread.currentThread().getName() + "]: Diagnosis complete for "
                    + diagnosedPatient.getName());
            diagnosedPatient.setTiempoFin(System.currentTimeMillis());
            totalTime += diagnosedPatient.calcularTiempoEjecucion();
            System.out.println("\t\t\t\t[" + diagnosedPatient.getName() + "] Total time: " + totalTime);
            patientResults.put(diagnosedPatient);
        } finally {
            mutex.unlock();
        }
    }

    /* Specialist */
    @SuppressWarnings("java:S106")
    public void doDiagnosisWithModel() throws InterruptedException {
        mutex.lock();
        try {
            while (diagnosisToAprove.isEmpty()) {
                specialistWait.await();
            }
            diagnosisToAprove.take();
            Thread.sleep(2000);
            System.out.println("Diagnosis Complete");
            patientWait.signal();
        } finally {
            mutex.unlock();
        }
    }

    /* Doc */
    @SuppressWarnings("java:S106")
    public void giveFinalResult() throws InterruptedException {
        mutex.lock();
        try {
            /* This works but has preferency, over doing a consult */
            while (!patientResults.isEmpty()) {
                Patient patient = patientResults.take();
                System.out.println("\t\t\t\t\t[" + Thread.currentThread().getName() + "]: " + patient.getName()
                        + " has received the result");
                Thread.sleep(1000);
                System.out.println("\t\t\t\t\t[" + Thread.currentThread().getName() + "]: " + patient.getName()
                        + " leaves the hospital");
            }
        } finally {
            mutex.unlock();
        }
    }

    public void createThreads() {
        for (int i = 0; i < NUM_PATIENTS; i++) {
            patients[i] = new Patient("Patient", i + 1, this);
        }
        for (int i = 0; i < NUM_DOCTORS; i++) {
            doctors[i] = new Sanitary(this, i + 1);
        }
        for (int i = 0; i < NUM_SPECIALISTS; i++) {
            specialists[i] = new Specialist(this, i + 1);
        }
        for (int i = 0; i < NUM_RADIOGRAPHERS; i++) {
            radiographers[i] = new Radiographer(this, i + 1);
        }
    }

    public void startThreads() {
        for (Patient thread : patients) {
            thread.start();
        }
        for (Sanitary thread : doctors) {
            thread.start();
        }
        for (Specialist thread : specialists) {
            thread.start();
        }
        for (Radiographer thread : radiographers) {
            thread.start();
        }
    }

    public void waitEndOfThreads() throws InterruptedException {
        for (Patient thread : patients) {
            thread.join();
        }
        for (Sanitary thread : doctors) {
            thread.interrupt();
            thread.join();
        }
        for (Specialist thread : specialists) {
            thread.interrupt();
            thread.join();
        }
        for (Radiographer thread : radiographers) {
            thread.interrupt();
            thread.join();
        }
    }

    public int getTotalTime() {
        return this.totalTime;
    }

    public BlockingQueue<Patient> getFirstWaitingRoom() {
        return this.firstWaitingRoom;
    }

    public BlockingQueue<Patient> getSecondWaitingRoom() {
        return this.secondWaitingRoom;
    }

    public BlockingQueue<Diagnosis> getDiagnosisToAprove() {
        return this.diagnosisToAprove;
    }

    public BlockingQueue<Patient> getPatientResults() {
        return this.patientResults;
    }


}
