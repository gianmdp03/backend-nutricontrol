package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.model.Appointment;
import com.erick.nutricontrol.service.GoogleMeetService;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.apps.meet.v2.CreateSpaceRequest;
import com.google.apps.meet.v2.Space;
import com.google.apps.meet.v2.SpacesServiceClient;
import com.google.apps.meet.v2.SpacesServiceSettings;
import com.google.auth.oauth2.UserCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GoogleMeetServiceImpl implements GoogleMeetService {

    @Value("${google.meet.mock.enabled:true}")
    private boolean isMockEnabled;

    @Value("${google.oauth.client-id}")
    private String clientId;

    @Value("${google.oauth.client-secret}")
    private String clientSecret;

    @Value("${google.oauth.refresh-token}")
    private String refreshToken;

    @Override
    public String createMeetLink(Appointment appointment) {
        // Si por algún motivo volvés a prender el mock, te genera el link falso
        if (isMockEnabled) {
            log.info("MOCK MODE habilitado. Generando link falso para el turno {}.", appointment.getId());
            return "https://meet.google.com/mock-link-123";
        }

        try {
            UserCredentials credentials = UserCredentials.newBuilder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRefreshToken(refreshToken)
                    .build();

            SpacesServiceSettings settings = SpacesServiceSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            // 3. Conectarse a Google y crear la sala vacía
            try (SpacesServiceClient spacesServiceClient = SpacesServiceClient.create(settings)) {

                // Un "Space" vacío le dice a Google que genere una sala estándar
                CreateSpaceRequest request = CreateSpaceRequest.newBuilder()
                        .setSpace(Space.newBuilder().build())
                        .build();

                Space response = spacesServiceClient.createSpace(request);

                log.info("Sala de Meet creada exitosamente en la cuenta real para el turno id {}: {}",
                        appointment.getId(), response.getMeetingUri());

                return response.getMeetingUri();
            }

        } catch (Exception e) {
            log.error("Error crítico al comunicarse con la API de Google Meet", e);
            return null;
        }
    }
}