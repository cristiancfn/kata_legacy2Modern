package com.kata.migration_engine.application.expert;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kata.migration_engine.domain.ast.AssignmentStatement;
import com.kata.migration_engine.domain.ast.CodeNode;
import com.kata.migration_engine.domain.ast.IfStatement;
import com.kata.migration_engine.domain.ast.PrintStatement;
import com.kata.migration_engine.domain.ast.VariableDeclarationNode;
import com.kata.migration_engine.domain.model.Language;
import com.kata.migration_engine.domain.model.MigrationRuleReport;
import com.kata.migration_engine.domain.model.ParseResult;
import com.kata.migration_engine.domain.model.MigrationRuleReport.RuleType;
import com.kata.migration_engine.domain.port.LegacyParser;

public class CobolParser implements LegacyParser{

    private static final Pattern IF_PATTERN = Pattern.compile("IF\\s+(.+)");
    private static final Pattern DISPLAY_PATTERN = Pattern.compile("DISPLAY\\s+\"([^\"]+)\"");
    private static final Pattern MOVE_PATTERN = Pattern.compile("MOVE\\s+(.+)\\s+TO\\s+(.+)");
    private static final Pattern VAR_DECLARATION_PATTERN = Pattern.compile("(?i)\\d{2}\\s+([A-Za-z0-9\\-]+)\\s+PIC\\s+([A-Za-z0-9\\(\\)]+)");

    @Override
    public boolean supports(Language language) {
        return language == Language.COBOL;
    }

    @Override
    public ParseResult parse(String sourceCode) {
        List<CodeNode> ast = new ArrayList<>();
        List<MigrationRuleReport> reports = new ArrayList<>();

        String[] lines = sourceCode.split("\\r?\\n");

        IfStatement currentIf = null;
        boolean inElseBlock = false;

        for (int i=0; i<lines.length; i++) {
            String line = lines[i].replaceAll("[\\u00A0\\s]+", " ").trim();

            if (line.isEmpty())
                continue;

            if (line.endsWith(".")){
                line = line.substring(0, line.length() - 1).trim();
            }

            //REGLA 1: Condicional IF
            Matcher ifMatcher = IF_PATTERN.matcher(line);
            if (ifMatcher.matches()) {
                currentIf = new IfStatement(ifMatcher.group(1), new ArrayList<>(), new ArrayList<>());
                reports.add(new MigrationRuleReport("Cobol-Rule-01", "Conversión de condicional IF", RuleType.APPLIED));
                continue;
            }

            // REGLA 2: ELSE
            if (line.equals("ELSE")) {
                inElseBlock = true;
                reports.add(new MigrationRuleReport("Cobol-Rule-02", "Detección de bloque ELSE", RuleType.APPLIED));
                continue;
            }

            // REGLA 3: END-IF
            if (line.equals("END-IF")){
                if (currentIf != null) {
                    ast.add(currentIf);
                    currentIf = null;
                    inElseBlock = false;
                    reports.add(new MigrationRuleReport("Cobol-Rule-03", "Cierre de condicional END-IF", RuleType.APPLIED));
                }
                continue;
            }

            CodeNode node = null;

            //REGLA 4: DISPLAY
            Matcher displayMatcher = DISPLAY_PATTERN.matcher(line);
            if (displayMatcher.matches()) {
                node = new PrintStatement(displayMatcher.group(1));
                reports.add(new MigrationRuleReport("Cobol-Rule-04", "Conversión de DISPLAY a log/print", RuleType.APPLIED));
            }

            //REGLA 5: Asignaciones (MOVE x TO y)
            Matcher moveMatcher = MOVE_PATTERN.matcher(line);
            if (moveMatcher.matches() && node == null) {
                node = new AssignmentStatement(moveMatcher.group(2), moveMatcher.group(1));
                reports.add(new MigrationRuleReport("Cobol-Rule-05", "Conversión de asignación MOVE", RuleType.APPLIED));
            }

            //REGLA 6: Declaración de variables
            Matcher varMatcher = VAR_DECLARATION_PATTERN.matcher(line);
            if (varMatcher.matches() && node == null) {
                String varName = varMatcher.group(1);
                String picClause = varMatcher.group(2).toUpperCase();

                String abstractType = (picClause.startsWith("X") || picClause.startsWith("A")) ? "STRING" : "NUMBER";

                node = new VariableDeclarationNode(varName, abstractType);
                reports.add(new MigrationRuleReport("Cobol-Rule-06", "Conversión de declaración de variable (" + abstractType + ")", RuleType.APPLIED));
            }

            //REGLA 7: Detección de código no soportado (Warning)
            if (node == null && currentIf == null && !line.matches("\\d+")){
                reports.add(new MigrationRuleReport("Warning-01", "Instrucción no reconocida o no soportada en la línea " + (i+1) + ": " + line, RuleType.WARNING));
            }

            if (node != null) {
                if (currentIf != null) {
                    if (inElseBlock)
                        currentIf.falseBlock().add(node);
                    else
                        currentIf.trueBlock().add(node);
                } else {
                    ast.add(node);
                }
            }
        }

        return new ParseResult(ast, reports);
    }

}
