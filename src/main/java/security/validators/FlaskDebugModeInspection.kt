package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyBoolLiteralExpression
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyReferenceExpression
import security.Checks

class FlaskDebugModeInspection : PyInspection() {

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            val calleeName = node.callee?.name ?: return
            if (calleeName != "run") return
            if ((node.firstChild as PyReferenceExpression).asQualifiedName().toString() != "app.run") return
            if (node.getKeywordArgument("debug") == null) return
            if (!(node.getKeywordArgument("debug") as PyBoolLiteralExpression?)!!.value) return
            holder?.registerProblem(node, Checks.FlaskDebugModeCheck.Message)
        }
    }
}