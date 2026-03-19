package com.futela.api.infrastructure.persistence.entity.core;

import com.futela.api.infrastructure.persistence.entity.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "platform_settings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformSettingEntity extends BaseEntity {

    @Column(name = "setting_key", nullable = false, unique = true, length = 100)
    private String key;

    @Column(nullable = false)
    private String value;

    @Column(length = 100)
    private String category;

    @Column
    private String description;
}
