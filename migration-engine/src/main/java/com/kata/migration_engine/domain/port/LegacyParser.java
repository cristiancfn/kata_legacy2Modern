package com.kata.migration_engine.domain.port;

import com.kata.migration_engine.domain.model.Language;
import com.kata.migration_engine.domain.model.ParseResult;

public interface LegacyParser {

    boolean supports(Language language);

    ParseResult parse(String sourceCode);

}
