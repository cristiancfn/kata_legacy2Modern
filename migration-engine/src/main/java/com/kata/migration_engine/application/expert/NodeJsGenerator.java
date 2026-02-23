package com.kata.migration_engine.application.expert;

import java.util.List;

import com.kata.migration_engine.domain.ast.AssignmentStatement;
import com.kata.migration_engine.domain.ast.CodeNode;
import com.kata.migration_engine.domain.ast.IfStatement;
import com.kata.migration_engine.domain.ast.PrintStatement;
import com.kata.migration_engine.domain.ast.VariableDeclarationNode;
import com.kata.migration_engine.domain.model.Language;
import com.kata.migration_engine.domain.port.ModernGenerator;

public class NodeJsGenerator implements ModernGenerator {

    @Override
    public boolean supports(Language language) {
        return language == Language.NODEJS;
    }

    @Override
    public String generate(List<CodeNode> ast) {
        StringBuilder builder = new StringBuilder();
        generateNodes(ast, builder, 0);
        return builder.toString();
    }

    private void generateNodes(List<CodeNode> nodes, StringBuilder builder, int indentLevel) {
        String indent = "    ".repeat(indentLevel);

        for (CodeNode node : nodes) {
            if (node instanceof PrintStatement printStmt) {
                builder.append(indent).append("console.log(\"").append(printStmt.content()).append("\");\n");
            } 
            else if (node instanceof VariableDeclarationNode varNode) {
                String formattedName = varNode.variableName().replace("-", "_");
                builder.append(indent).append("let ").append(formattedName).append(";\n");
            } 
            else if (node instanceof AssignmentStatement assignStmt) {
                String formattedVarName = assignStmt.variableName().replace("-", "_");
                
                builder.append(indent).append(formattedVarName).append(" = ").append(assignStmt.value()).append(";\n");
            } 
            else if (node instanceof IfStatement ifStmt) {
                builder.append(indent).append("if (").append(ifStmt.condition()).append(") {\n");
                
                generateNodes(ifStmt.trueBlock(), builder, indentLevel + 1);
                
                if (!ifStmt.falseBlock().isEmpty()) {
                    builder.append(indent).append("} else {\n");
                    generateNodes(ifStmt.falseBlock(), builder, indentLevel + 1);
                }
                
                builder.append(indent).append("}\n");
            }
        }
    }


    
}
