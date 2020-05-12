package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyStringLiteralExpression
import security.Checks
import security.helpers.SecurityVisitor
import security.helpers.calleeMatches
import security.helpers.hasImportedNamespace
import security.helpers.skipDocstring
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

        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return
            val targetFunctions = arrayOf("text", "prefix_with", "suffix_with")
            if (!calleeMatches(node, targetFunctions)) return
            if (!hasImportedNamespace(node.containingFile, "sqlalchemy")) return
            if (node.arguments.isNullOrEmpty()) return
            val sqlStatement = node.arguments.first()
            if (sqlStatement is PyStringLiteralExpression) return
            holder.registerProblem(node, Checks.SqlAlchemyUnsafeQueryCheck)
        }
    }
}
