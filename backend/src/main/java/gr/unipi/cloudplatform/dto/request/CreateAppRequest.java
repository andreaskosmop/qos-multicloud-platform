package gr.unipi.cloudplatform.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateAppRequest(
        @NotBlank String title,
        String description,
        String version,
        List<String> tags
) {}
