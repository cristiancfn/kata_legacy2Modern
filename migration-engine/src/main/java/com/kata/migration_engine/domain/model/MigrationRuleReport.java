package com.kata.migration_engine.domain.model;

public record MigrationRuleReport(
    String ruleName,
    String description,
    RuleType type
) {
    public enum RuleType {
        APPLIED, WARNING
    }
}
