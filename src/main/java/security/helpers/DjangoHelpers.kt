package security.helpers

import com.intellij.codeInspection.ProblemsHolder
import com.jetbrains.python.psi.PyExpression
import com.jetbrains.python.psi.PyStringLiteralExpression
import security.Checks

fun inspectStatement(sqlStatement: PyExpression, holder: ProblemsHolder, check: Checks.CheckType){
    if (sqlStatement !is PyStringLiteralExpression) return
    val param = Regex("%s")
    val paramMatches = param.findAll(sqlStatement.stringValue)
    for (match in paramMatches){
        try {
            if (sqlStatement.stringValue.substring(match.range.first - 1, match.range.first) != "'") return
            if (sqlStatement.stringValue.substring(match.range.last + 1, match.range.last + 2) != "'") return
        } catch (oobe: StringIndexOutOfBoundsException){
            // End or beginning of string, so this SQL injection technique wouldn't be possible.
            return
        }
        holder.registerProblem(sqlStatement, check.getDescription())
    }
}