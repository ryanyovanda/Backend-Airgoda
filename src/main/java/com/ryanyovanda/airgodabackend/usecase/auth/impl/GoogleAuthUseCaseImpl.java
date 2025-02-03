package com.ryanyovanda.airgodabackend.usecase.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.ryanyovanda.airgodabackend.infrastructure.auth.utils.JwtUtil;
import com.ryanyovanda.airgodabackend.usecase.auth.GoogleAuthUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;

@Service
public class GoogleAuthUseCaseImpl implements GoogleAuthUseCase {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthUseCaseImpl.class);
    private static final String CLIENT_ID = "307742974723-9oqsehf5kjih7n28vtv9rqscb045oraa.apps.googleusercontent.com"; // Ensure this matches your Google OAuth client ID

    @Override
    public Map<String, Object> verifyToken(String idTokenString) throws GeneralSecurityException, IOException {
        logger.info("🔍 Verifying Google ID Token...");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(CLIENT_ID)) // ✅ Validate against your Google Client ID
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // ✅ Extract user details
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");

            logger.info("✅ Google ID Token is valid! Email: {}", email);

            // ✅ Generate JWT for the user
            String jwtToken = JwtUtil.generateToken(email, false); // ✅ Pass 'false' for access token

            return Map.of(
                    "email", email,
                    "name", name,
                    "picture", picture,
                    "jwt", jwtToken // ✅ Include JWT in response
            );
        } else {
            logger.error("❌ Invalid Google ID Token received!");
            throw new IllegalArgumentException("Invalid Google ID Token");
        }
    }
}
