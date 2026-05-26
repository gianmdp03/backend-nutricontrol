package com.erick.nutricontrol.dto.service;

import jakarta.validation.constraints.Size;

public record ServiceUpdateDTO(@Size(max = 50) String name, @Size(max = 200) String description) {}
