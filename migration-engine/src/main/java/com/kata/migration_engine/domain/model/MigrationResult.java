package com.kata.migration_engine.domain.model;

import java.util.List;

public record MigrationResult(
    String generatedCode,
    List<MigrationRuleReport> report
) {

}
