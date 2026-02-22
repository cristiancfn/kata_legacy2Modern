package com.kata.migration_engine.domain.ast;

public record AssignmentStatement(String variableName, String value) implements CodeNode {

}
