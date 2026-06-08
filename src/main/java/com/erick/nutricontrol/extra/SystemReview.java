package com.erick.nutricontrol.extra;

import jakarta.validation.constraints.Size;

public record SystemReview(
    @Size(max = 300) String head,
    @Size(max = 300) String eyes,
    @Size(max = 300) String ears,
    @Size(max = 300) String nose,
    @Size(max = 300) String mouthAndThroat,
    @Size(max = 300) String neck,
    @Size(max = 300) String thorax,
    @Size(max = 300) String lungs,
    @Size(max = 300) String heart,
    @Size(max = 300) String abdomen,
    @Size(max = 300) String genitourinary,
    @Size(max = 300) String extremities,
    @Size(max = 300) String musculoskeletal,
    @Size(max = 300) String neurological,
    @Size(max = 300) String skin,
    @Size(max = 300) String generalStatus) {}
