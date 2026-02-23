package com.kata.migration_engine.infraestructure.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kata.migration_engine.application.service.MigrationEngine;
import com.kata.migration_engine.domain.model.Language;
import com.kata.migration_engine.domain.model.MigrationResult;
import com.kata.migration_engine.infraestructure.rest.dto.MigrationRequestDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/migrations")
@CrossOrigin(origins = "*")
public class MigrationController {

    private final MigrationEngine migrationEngine;

    public MigrationController(MigrationEngine migrationEngine){
        this.migrationEngine = migrationEngine;
    }

    @PostMapping
    public ResponseEntity<MigrationResult> migrateCode(@Valid @RequestBody MigrationRequestDto request) {
        Language sourceLang = Language.valueOf(request.sourceLanguage().toUpperCase());
        Language targetLang = Language.valueOf(request.targetLanguage().toUpperCase());

        MigrationResult result = migrationEngine.migrate(request.sourceCode(), sourceLang, targetLang);

        return ResponseEntity.ok(result);
    }
}
