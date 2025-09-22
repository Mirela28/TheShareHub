package com.thesharehub.TheShareHub.validation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ValidationResult {
    public boolean Valid;
    public List<String> Errors;
}
