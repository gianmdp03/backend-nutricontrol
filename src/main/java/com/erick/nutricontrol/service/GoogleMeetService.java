package com.erick.nutricontrol.service;

import com.erick.nutricontrol.model.Appointment;

public interface GoogleMeetService {
    String createMeetLink(Appointment appointment);
}
