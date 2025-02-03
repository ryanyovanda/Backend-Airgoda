package com.ryanyovanda.airgodabackend.usecase.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public interface GoogleAuthUseCase {
    Map<String, Object> verifyToken(String idTokenString) throws GeneralSecurityException, IOException;
}
