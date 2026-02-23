package com.kata.migration_engine.infraestructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kata.migration_engine.application.expert.CobolParser;
import com.kata.migration_engine.application.expert.JavaGenerator;
import com.kata.migration_engine.application.expert.NodeJsGenerator;
import com.kata.migration_engine.application.service.MigrationEngine;
import com.kata.migration_engine.domain.port.LegacyParser;
import com.kata.migration_engine.domain.port.ModernGenerator;

@Configuration
public class MigrationConfig {

    @Bean
    LegacyParser cobolParser() {
        return new CobolParser();
    }

    @Bean
    ModernGenerator javaGenerator() {
        return new JavaGenerator();
    }

    @Bean
    ModernGenerator nodeJsGenerator() {
        return new NodeJsGenerator();
    }

    @Bean
    MigrationEngine migrationEngine(List<LegacyParser> parsers, List<ModernGenerator> generators) {
        return new MigrationEngine(parsers, generators);
    }

}
