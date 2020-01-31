package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.PyStringLiteralExpression
import security.Checks
import security.helpers.QualifiedNames.getQualifiedName

class DjangoRawSqlInspection : PyInspection() {
    val check = Checks.DjangoClickjackMiddlewareCheck;

    override fun getStaticDescription(): String? {
        return check.getDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression?) {
            if (node == null) return
            val calleeName = node.callee?.name ?: return
            if (calleeName != "RawSQL") return
            val qualifiedName = getQualifiedName(node) ?: return
            if (!qualifiedName.startsWith("django.db.models.expressions.RawSQL")) return
            val sqlStatement = node.arguments.first() ?: return
            if (sqlStatement !is PyStringLiteralExpression) return
            val param = Regex("%s")
            val paramMatches = param.findAll(sqlStatement.stringValue)
            for (match in paramMatches){
                try {
                    if (sqlStatement.stringValue.substring(match.range.start - 1, match.range.start) != "'") return
                    if (sqlStatement.stringValue.substring(match.range.last + 1, match.range.last + 2) != "'") return
                } catch (oobe: StringIndexOutOfBoundsException){
                    // End or beginning of string, so this SQL injection technique wouldn't be possible.
                    return
                }
                holder?.registerProblem(node, Checks.DjangoRawSqlCheck.getDescription())
            }
        }
    }
}