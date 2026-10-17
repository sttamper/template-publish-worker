package com.cwtask.worker.domain.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateUpdateAvailable {

    private String engagementId;
    private String templateId;
    private String version;

}
