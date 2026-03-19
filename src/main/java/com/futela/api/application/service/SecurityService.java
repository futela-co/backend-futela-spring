package com.futela.api.application.service;

import java.util.UUID;

public interface SecurityService {
    UUID getCurrentUserId();
    UUID getCurrentCompanyId();
    UUID getCurrentSessionId();
}
