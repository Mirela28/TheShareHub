package com.thesharehub.TheShareHub.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResult {
    public boolean valid;
    public List<String> errors;
}
