package com.saveaddicts.app.repositories;

import com.saveaddicts.app.models.Appointment;
import com.saveaddicts.app.models.AppointmentStatus;

import java.util.List;

/**
 * Repository interface for appointment booking and management (FR4 & FR9).
 */
public interface AppointmentRepository {
    void getAppointmentsForPatient(String patientId, Callback<List<Appointment>> callback);

    void getAppointmentsForPhysician(String physicianId, Callback<List<Appointment>> callback);

    void getAppointmentById(String appointmentId, Callback<Appointment> callback);

    void createAppointment(Appointment appointment, Callback<Appointment> callback);

    void updateAppointmentStatus(String appointmentId, AppointmentStatus status, Callback<Void> callback);

    void cancelAppointment(String appointmentId, String reason, Callback<Void> callback);

    void getAvailableTimeSlots(String physicianId, String date, Callback<List<String>> callback);
}
