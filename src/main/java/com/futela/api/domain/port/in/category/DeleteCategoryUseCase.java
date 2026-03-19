package com.futela.api.domain.port.in.category;

import java.util.UUID;

public interface DeleteCategoryUseCase {
    void execute(UUID id);
}
