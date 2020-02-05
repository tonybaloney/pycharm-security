package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyStringLiteralExpression
import security.Checks
import security.helpers.QualifiedNames.getQualifiedName

class BuiltinExecInspection : PyInspection() {
    val check = Checks.BuiltinExecCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            val calleeName = node.callee?.name ?: return
            if (calleeName != "exec") return
            val qualifiedName = getQualifiedName(node) ?: return
            if (qualifiedName != "exec") return

            // First argument as a string literal is ok
            if (node.arguments.isNullOrEmpty()) return
            if (node.arguments.first() is PyStringLiteralExpression) return

            holder?.registerProblem(node, Checks.BuiltinExecCheck.getDescription())
        }
    }
}