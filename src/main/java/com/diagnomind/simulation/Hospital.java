package com.diagnomind.simulation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

/**
 * The Hospital class represents the central entity in the simulation, managing patients, doctors,
 * specialists, radiographers, and various queues for patient flow and diagnosis processes.
 */
public class Hospital {

    /** Flag indicating whether the hospital uses a diagnostic model. */
    private Boolean useModel;

    /** RestTemplate for making HTTP requests to external services. */
    private RestTemplate restTemplate;
    private static final String URL = "https://simulation.diagnomind.duckdns.org/simulation";

    /** Constants defining the capacity and number of staff in the hospital. */
    private static final int CAPACITY = 4;
    private static final int NUM_DOCTORS = 3;
    private static final int NUM_PATIENTS = 10;
    private static final int NUM_RADIOGRAPHERS = 2;
    private static final int NUM_SPECIALISTS = 2;

    /** Strings representing different levels of indentation for formatting. */
    private static final String SPACE_1 = "\t";
    private static final String SPACE_2 = "\t\t";
    private static final String SPACE_3 = "\t\t\t";
    private static final String SPACE_4 = "\t\t\t\t";
    private static final String SPACE_5 = "\t\t\t\t\t";

    /**
     * Arrays to store instances of patients, doctors, specialists, and
     * radiographers.
     */
    private Patient[] patients;
    private Sanitary[] doctors;
    private Specialist[] specialists;
    private Radiographer[] radiographers;

    /** Total time elapsed during the hospital simulation. */
    private long totalTime;

    /**
     * Blocking queues for managing patients, doctors, radiographers, and diagnosis
     * workflow.
     */
    private BlockingQueue<Patient> firstWaitingRoom;
    private BlockingQueue<Patient> secondWaitingRoom;
    private BlockingQueue<Sanitary> availableDocs;
    private BlockingQueue<Radiography> radiographysToEvaluate;
    private BlockingQueue<Diagnosis> finishedDiagnosis;

    /**
     * Constructs a Hospital object with the specified configuration.
     *
     * @param model        Indicates whether the hospital uses a diagnostic model.
     * @param restTemplate The RestTemplate for making HTTP requests to external
     *                     services.
     */
    public Hospital(boolean model, RestTemplate restTemplate) {
        this.useModel = model;
        this.restTemplate = restTemplate;
        this.totalTime = 0;

        this.patients = new Patient[NUM_PATIENTS];
        this.doctors = new Sanitary[NUM_DOCTORS];
        this.specialists = new Specialist[NUM_SPECIALISTS];
        this.radiographers = new Radiographer[NUM_RADIOGRAPHERS];

        this.firstWaitingRoom = new LinkedBlockingQueue<>(CAPACITY);
        this.secondWaitingRoom = new LinkedBlockingQueue<>(CAPACITY);
        this.availableDocs = new LinkedBlockingQueue<>(NUM_DOCTORS);
        this.radiographysToEvaluate = new LinkedBlockingQueue<>();
        this.finishedDiagnosis = new LinkedBlockingQueue<>();
    }

    /**
     * Adds a patient to the first waiting room.
     * This method prints a message indicating the patient's entry into the hospital
     * and the waiting room.
     *
     * @param patient The patient to be added to the first waiting room.
     * @throws InterruptedException If the thread is interrupted while waiting to
     *                              add the patient to the queue.
     */
    @SuppressWarnings("java:S106")
    public void firstWaitingRoom(Patient patient) throws InterruptedException {
        System.out.println("[" + patient.getName() + "] enters the hospital");
        firstWaitingRoom.put(patient);
        patient.setTiempoInit(System.currentTimeMillis());
        System.out.println("[Waitingroom 1]: " + patient.getName() + " enters");
    }

    /**
     * Attends to a patient by assigning a doctor for evaluation.
     * This method dequeues a patient from the first waiting room, a doctor from the
     * available doctors queue,
     * performs the evaluation, and then enqueues the doctor back to the available
     * doctors queue.
     * It prints messages indicating the patient's exit from the waiting room, the
     * doctor's evaluation process,
     * and the completion of the evaluation.
     *
     * @throws InterruptedException If the thread is interrupted while waiting to
     *                              dequeue or enqueue patients or doctors.
     */
    @SuppressWarnings("java:S106")
    public void attendPacient() throws InterruptedException {
        Patient toEvaluate = firstWaitingRoom.take();
        Sanitary doc = availableDocs.take();
        System.out.println(SPACE_1 + "[Waitingroom 1]: " + toEvaluate.getName() + " gets out");
        System.out.println(
                SPACE_2 + "[" + doc.getName() + "]: Evaluating " + toEvaluate.getName());
        System.out.println(SPACE_2 + "[" + doc.getName() + "]: Evaluation done");
        availableDocs.put(doc);
        toEvaluate.patientKeepRunning();
    }

    /**
     * Moves a patient to the second waiting room, indicating that the patient is
     * waiting for further examination or treatment.
     * This method puts the patient into the second waiting room queue and prints a
     * message indicating the patient's entry.
     *
     * @param patient The patient to be moved to the second waiting room.
     * @throws InterruptedException If the thread is interrupted while waiting to
     *                              put the patient into the queue.
     */
    @SuppressWarnings("java:S106")
    public void secondWaitingRoom(Patient patient) throws InterruptedException {
        patient.patientWait();
        secondWaitingRoom.put(patient);
        System.out.println(SPACE_1 + "[Waitingroom 2]: " + patient.getName() + " enters");
    }

    /**
     * Performs a radiography for a patient from the second waiting room.
     * This method dequeues a patient from the second waiting room, simulates the
     * radiography process,
     * and sends the radiography image either to a diagnostic model or a specialist
     * based on the hospital configuration.
     * It prints messages indicating the patient's exit from the waiting room, the
     * radiography process, and the completion.
     *
     * @throws InterruptedException If the thread is interrupted while waiting to
     *                              dequeue patients or perform radiography.
     */
    @SuppressWarnings("java:S106")
    public void doRadiographyToPacient() throws InterruptedException {
        Patient toEvaluate = secondWaitingRoom.take();
        System.out.println(SPACE_1 + "[Waitingroom 2]: " + toEvaluate.getName() + " gets out");
        System.out
                .println(SPACE_3 + "[" + Thread.currentThread().getName() + "]: Scanning " + toEvaluate.getName());
        System.out.println(SPACE_3 + "[" + Thread.currentThread().getName() + "]: Radiography done");
        if (Boolean.TRUE.equals(useModel)) {
            this.sendImageToModel(toEvaluate);
        } else {
            this.sendImageToSpecialist(toEvaluate);
        }
    }

    /**
     * Sends the radiography image of a patient to a diagnostic model via an
     * external service.
     * This method performs an HTTP GET request to a predefined URL, retrieves the
     * radiography image,
     * and puts the radiography into the queue for evaluation.
     * It prints messages indicating the process of sending the image to the model.
     *
     * @param diagnosisPatient The patient for whom the radiography image is sent to
     *                         the model.
     * @throws InterruptedException If the thread is interrupted while waiting to
     *                              put the radiography into the queue.
     */
    @SuppressWarnings("java:S106")
    public void sendImageToModel(Patient diagnosisPatient) throws InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<byte[]> request = new HttpEntity<>(headers);

        int status = restTemplate.exchange(URL, HttpMethod.GET, request,
                new ParameterizedTypeReference<byte[]>() {
                }).getStatusCode().value();

        if (status == 200) {
            Radiography newRadiography = new Radiography(diagnosisPatient, true);
            radiographysToEvaluate.put(newRadiography);
            System.out.println(
                    SPACE_3 + "[" + Thread.currentThread().getName() + "]: " + newRadiography.getPatient().getName()
                            + "'s image sent to model");
        } else {
            System.out.println("[[[[[[[[[[[[[[  CONNECTION STATUS ERROR " + status + " ]]]]]]]]]]]]]]");
        }
    }

    /**
     * Sends the radiography image of a patient to a specialist for evaluation.
     * This method creates a new Radiography object, indicating that it is not
     * processed by a diagnostic model,
     * and puts the radiography into the queue for specialist evaluation.
     * It prints messages indicating the process of sending the image to the
     * specialist.
     *
     * @param diagnosisPatient The patient for whom the radiography image is sent to
     *                         the specialist.
     * @throws InterruptedException If the thread is interrupted while waiting to
     *                              put the radiography into the queue.
     */
    @SuppressWarnings("java:S106")
    public void sendImageToSpecialist(Patient diagnosisPatient) throws InterruptedException {
        Radiography newRadiography = new Radiography(diagnosisPatient, false);
        radiographysToEvaluate.put(newRadiography);
        System.out.println(
                SPACE_3 + "[" + Thread.currentThread().getName() + "]: " + newRadiography.getPatient().getName()
                        + "'s image sent");
    }

    /**
     * Performs the diagnosis for a patient based on the evaluated radiography.
     * This method dequeues a radiography from the queue, simulates the diagnosis
     * process,
     * and creates a new Diagnosis object with the diagnosis message.
     * It prints messages indicating the diagnosis process and completes the
     * diagnosis.
     *
     * @throws InterruptedException If the thread is interrupted while waiting to
     *                              dequeue a radiography or during the diagnosis
     *                              simulation.
     */
    @SuppressWarnings({ "java:S106", "java:S5411" })
    public void doDiagnosis() throws InterruptedException {
        Radiography radiographyToEvaluate = radiographysToEvaluate.take();
        Patient diagnosedPatient = radiographyToEvaluate.getPatient();
        Diagnosis newDiagnosis = new Diagnosis(radiographyToEvaluate, diagnosedPatient.getName() + " has cancer");
        int millis = (radiographyToEvaluate.getUsesModel()) ? 1000 : 3000;
        Thread.sleep(millis);
        System.out.println(SPACE_4 + "[" + Thread.currentThread().getName() + "]: Diagnosis complete for "
                + diagnosedPatient.getName());
        finishedDiagnosis.put(newDiagnosis);
        diagnosedPatient.patientKeepRunning();
    }

    /**
     * Retrieves the final diagnosis result for a patient from the finished
     * diagnosis queue.
     * This method dequeues a doctor from the available doctors queue, a diagnosis
     * from the finished diagnosis queue,
     * and prints messages indicating the final result for the patient.
     *
     * @param patient The patient for whom the final diagnosis result is retrieved.
     * @throws InterruptedException If the thread is interrupted while waiting to
     *                              dequeue doctors, diagnoses, or during the result
     *                              presentation.
     */
    @SuppressWarnings("java:S106")
    public void getFinalResult(Patient patient) throws InterruptedException {
        patient.patientWait();
        Sanitary doc = availableDocs.take();
        Diagnosis diagnosis = finishedDiagnosis.take();
        Thread.sleep(1000);
        System.out.println(SPACE_2 + "[" + doc.getName() + "]: " + patient.getName()
                + "'s result -> " + "\"" + diagnosis.getMsg() + "\"");
        patient.setTiempoFin(System.currentTimeMillis());
        totalTime += patient.calcularTiempoEjecucion();
        System.out.println(SPACE_5 + "[" + patient.getName() + "] Total time: " + patient.calcularTiempoEjecucion());
        System.out.println(SPACE_5 + "[" + diagnosis.getPatient().getName() + "]: " + "leaves the hospital");
        availableDocs.put(doc);
    }

    /**
     * Creates and initializes the threads for @param patients @param
     * specialists @param radiographers and @param doctors
     * This method initializes arrays of {@link Patient}, {@link Specialist},
     * {@link Radiographer}, and {@link Sanitary} objects, associating them with the
     * hospital.
     * It also adds doctors to the available doctors queue.
     *
     * @return The initialized Hospital object with created threads.
     */
    @SuppressWarnings("java:S2142")
    public Hospital createThreads() {
        for (int i = 0; i < NUM_PATIENTS; i++) {
            patients[i] = new Patient(i + 1, this, new Semaphore(0));
        }
        for (int i = 0; i < NUM_SPECIALISTS; i++) {
            specialists[i] = new Specialist(this, i + 1);
        }
        for (int i = 0; i < NUM_RADIOGRAPHERS; i++) {
            radiographers[i] = new Radiographer(this, i + 1);
        }
        for (int i = 0; i < NUM_DOCTORS; i++) {
            doctors[i] = new Sanitary(this, i + 1);
            try {
                availableDocs.put(doctors[i]);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return this;
    }

    /**
     * Starts the threads for patients, specialists, radiographers, and doctors.
     * This method iterates through the arrays of Patient, Sanitary (doctors),
     * Specialist, and Radiographer objects,
     * and starts each thread.
     *
     * @return The Hospital object with started threads.
     */
    public Hospital startThreads() {
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
        return this;
    }

    /**
     * Waits for the completion of all threads for patients, specialists,
     * radiographers, and doctors.
     * This method iterates through the arrays of Patient, Sanitary (doctors),
     * Specialist, and Radiographer objects,
     * and waits for each thread to complete using the join() method.
     * It also interrupts the threads for doctors, specialists, and radiographers
     * before waiting for their completion.
     *
     * @return The Hospital object after all threads have completed.
     * @throws InterruptedException If the thread is interrupted while waiting for
     *                              the completion of the threads.
     */
    public Hospital waitEndOfThreads() throws InterruptedException {
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
        return this;
    }

    /**
     * Gets the total time elapsed during the hospital simulation.
     *
     * @return The total time elapsed in milliseconds.
     */
    public long getTotalTime() {
        return this.totalTime;
    }

    /**
     * Gets the blocking queue for the first waiting room.
     *
     * @return The {@link BlockingQueue} representing the first waiting room.
     */
    public BlockingQueue<Patient> getFirstWaitingRoom() {
        return this.firstWaitingRoom;
    }

    /**
     * Gets the blocking queue for the second waiting room.
     *
     * @return The {@link BlockingQueue} representing the second waiting room.
     */
    public BlockingQueue<Patient> getSecondWaitingRoom() {
        return this.secondWaitingRoom;
    }

    /**
     * Gets the blocking queue for available doctors.
     *
     * @return The {@link BlockingQueue} representing available doctors.
     */
    public BlockingQueue<Sanitary> getAvailableDocs() {
        return this.availableDocs;
    }

    /**
     * Gets the blocking queue for radiographies awaiting evaluation.
     *
     * @return The {@link BlockingQueue} representing radiographies awaiting
     *         evaluation.
     */
    public BlockingQueue<Radiography> getRadiographysToEvaluate() {
        return this.radiographysToEvaluate;
    }

    /**
     * Gets the blocking queue for finished diagnoses.
     *
     * @return The {@link BlockingQueue} representing finished diagnoses.
     */
    public BlockingQueue<Diagnosis> getFinishedDiagnosis() {
        return this.finishedDiagnosis;
    }

}
