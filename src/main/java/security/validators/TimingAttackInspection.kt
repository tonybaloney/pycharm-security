package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyBinaryExpression
import com.jetbrains.python.psi.PyReferenceExpression
import security.Checks
import security.fixes.UseCompareDigestFixer

class TimingAttackInspection : PyInspection() {
    val check = Checks.TimingAttackCheck;

    override fun getStaticDescription(): String? {
        return check.getDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
        override fun visitPyBinaryExpression(node: PyBinaryExpression?) {
            if (node == null) return
            val rightExpression = node.rightExpression ?: return
            val leftExpression = node.leftExpression ?: return
            if (rightExpression is PyReferenceExpression) {
                if (looksLikeAPassword(rightExpression))
                    holder?.registerProblem(node, Checks.TimingAttackCheck.getDescription(), UseCompareDigestFixer())
            }
            if (leftExpression is PyReferenceExpression) {
                if (looksLikeAPassword(leftExpression))
                    holder?.registerProblem(node, Checks.TimingAttackCheck.getDescription(), UseCompareDigestFixer() )
            }
        }

        private fun looksLikeAPassword(expression: PyReferenceExpression): Boolean {
            return listOf(*PasswordVariableNames).contains(expression.name)
        }
    }
}