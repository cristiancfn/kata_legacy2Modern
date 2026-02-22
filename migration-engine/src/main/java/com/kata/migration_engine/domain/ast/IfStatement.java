package com.kata.migration_engine.domain.ast;

import java.util.List;

public record IfStatement(
    String condition,
    List<CodeNode> trueBlock,
    List<CodeNode> falseBlock
) implements CodeNode {

}
