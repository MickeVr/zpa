/*
 * Z PL/SQL Analyzer
 * Copyright (C) 2015-2019 Felipe Zorzo
 * mailto:felipebzorzo AT gmail DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plsqlopen.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.plsqlopen.api.PlSqlGrammar;
import org.sonar.plsqlopen.annnotations.ActivatedByDefault;
import org.sonar.plsqlopen.annnotations.ConstantRemediation;
import com.sonar.sslr.api.AstNode;

@Rule(
    key = UnnecessaryElseCheck.CHECK_KEY,
    priority = Priority.MINOR
)
@ConstantRemediation("2min")
@ActivatedByDefault
public class UnnecessaryElseCheck extends AbstractBaseCheck {
    public static final String CHECK_KEY = "UnnecessaryElse";
    
    @Override
    public void init() {
        subscribeTo(PlSqlGrammar.ELSE_CLAUSE);
    }

    @Override
    public void visitNode(AstNode node) {
        AstNode ifStatement = node.getParent();
        
        if (!hasElsifClause(ifStatement) && hasTerminationStatement(ifStatement)) {
            addLineIssue(getLocalizedMessage(CHECK_KEY), node.getTokenLine());
        }
    }
    
    public boolean hasElsifClause(AstNode node) {
        return node.hasDirectChildren(PlSqlGrammar.ELSIF_CLAUSE);
    }
    
    private static boolean hasTerminationStatement(AstNode ifStatement) {
        for (AstNode statement : ifStatement.getFirstChild(PlSqlGrammar.STATEMENTS).getChildren()) {
            AstNode internal = statement.getFirstChild();
            if (CheckUtils.isTerminationStatement(internal)) {
                return true;
            }
        }
        return false;
    }
}
