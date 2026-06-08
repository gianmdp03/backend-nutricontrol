package com.erick.nutricontrol.extra;

import jakarta.validation.constraints.Size;

public record FamilyHistory(
    @Size(max = 200) String father,
    @Size(max = 200) String mother,
    @Size(max = 200) String grandparents,
    @Size(max = 200) String others) {}
