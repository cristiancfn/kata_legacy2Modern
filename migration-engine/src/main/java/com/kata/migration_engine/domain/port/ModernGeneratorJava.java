package com.kata.migration_engine.domain.port;

import java.util.List;

import com.kata.migration_engine.domain.ast.CodeNode;
import com.kata.migration_engine.domain.model.Language;

public interface ModernGeneratorJava {

    boolean supports(Language language);

    String generate(List<CodeNode> ast);

}
