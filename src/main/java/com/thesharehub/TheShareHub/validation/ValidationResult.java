package com.thesharehub.TheShareHub.validation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ValidationResult {
    public boolean valid;
    public List<String> errors;
}
