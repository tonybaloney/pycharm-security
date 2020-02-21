package security.helpers

import com.jetbrains.python.psi.PyExpression
import com.jetbrains.python.psi.PyStringLiteralExpression

fun inspectDjangoSqlTemplate(sqlStatement: PyExpression) : Boolean{
    if (sqlStatement !is PyStringLiteralExpression) return false
    val paramMatches = Regex("%s").findAll(sqlStatement.stringValue) + Regex("%\\([a-zA-Z0-9_]*\\)s").findAll(sqlStatement.stringValue)
    for (match in paramMatches){
        try {
            if (sqlStatement.stringValue.substring(match.range.first - 1, match.range.first) == "'" && sqlStatement.stringValue.substring(match.range.last + 1, match.range.last + 2) == "'")
                return true
            if (sqlStatement.stringValue.substring(match.range.first - 1, match.range.first) == "\"" && sqlStatement.stringValue.substring(match.range.last + 1, match.range.last + 2) == "\"")
                return true
        } catch (oobe: StringIndexOutOfBoundsException){
            // End or beginning of string, so this SQL injection technique wouldn't be possible.
            return false
        }
    }
    return false
}