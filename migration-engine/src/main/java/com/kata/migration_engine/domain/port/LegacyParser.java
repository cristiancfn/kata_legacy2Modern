package com.kata.migration_engine.domain.port;

import java.util.List;

import com.kata.migration_engine.domain.ast.CodeNode;
import com.kata.migration_engine.domain.model.Language;

public interface LegacyParser {

    boolean supports(Language language);

    List<CodeNode> parse(String sourceCode);

}
