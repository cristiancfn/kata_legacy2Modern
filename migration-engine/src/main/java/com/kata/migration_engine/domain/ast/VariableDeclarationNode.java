package com.kata.migration_engine.domain.ast;

public record VariableDeclarationNode(String variableName, String dataType) implements CodeNode {

}
