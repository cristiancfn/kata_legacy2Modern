package com.kata.migration_engine.domain.model;

import java.util.List;

import com.kata.migration_engine.domain.ast.CodeNode;

public record ParseResult(
    List<CodeNode> ast,
    List<MigrationRuleReport> reports
) {

}
