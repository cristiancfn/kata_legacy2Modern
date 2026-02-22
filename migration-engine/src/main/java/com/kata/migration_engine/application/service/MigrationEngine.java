package com.kata.migration_engine.application.service;

import java.util.List;

import com.kata.migration_engine.domain.model.Language;
import com.kata.migration_engine.domain.model.MigrationResult;
import com.kata.migration_engine.domain.model.ParseResult;
import com.kata.migration_engine.domain.port.LegacyParser;
import com.kata.migration_engine.domain.port.ModernGenerator;

public class MigrationEngine {

    private final List<LegacyParser> parsers;
    private final List<ModernGenerator> generators;

    public MigrationEngine(List<LegacyParser> parsers, List<ModernGenerator> generators) {
        this.parsers = parsers;
        this.generators = generators;
    }

    public MigrationResult migrate(String sourceCode, Language sourceLang, Language targetLang) {

        LegacyParser parser = parsers.stream()
            .filter(p -> p.supports(sourceLang))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No hay parser para el lenguaje: " + sourceLang));

        ModernGenerator generator = generators.stream()
            .filter(g -> g.supports(targetLang))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No hay generador para el lenguaje: " + targetLang));

        ParseResult parseResult = parser.parse(sourceCode);

        String generatedCode = generator.generate(parseResult.ast());

        return new MigrationResult(generatedCode, parseResult.reports());
    }

}
