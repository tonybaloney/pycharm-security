package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyCallExpression
import security.Checks
import security.helpers.*
import security.registerProblem

class SqlAlchemyUnsafeQueryInspection : PyInspection() {
    val check = Checks.SqlAlchemyUnsafeQueryCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        val methodNames = arrayOf("RawSQL", "raw", "execute")
        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return
            if (!calleeMatches(node, methodNames)) return
            if (!hasImportedNamespace(node.containingFile, "django")) return

            if (node.arguments.isNullOrEmpty()) return
            val sqlStatement = node.arguments.first() ?: return
            if (inspectDjangoSqlTemplate(sqlStatement))
                holder.registerProblem(sqlStatement, Checks.DjangoRawSqlCheck)
        }
    }
}
