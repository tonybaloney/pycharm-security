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
import security.helpers.qualifiedNameMatches
import security.helpers.skipDocstring

class BuiltinExecInspection : PyInspection() {
    val check = Checks.BuiltinExecCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return
            if (!calleeMatches(node, "exec")) return
            if (!qualifiedNameMatches(node, "exec")) return

            // First argument as a string literal is ok
            if (node.arguments.isNullOrEmpty()) return
            if (node.arguments.first() is PyStringLiteralExpression) return

            holder.registerProblem(node, Checks.BuiltinExecCheck.getDescription())
        }
    }
}