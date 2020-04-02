package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyAssignmentStatement
import com.jetbrains.python.psi.PyStringLiteralExpression
import com.jetbrains.python.psi.PyTargetExpression
import security.Checks
import security.helpers.SecurityVisitor
import security.helpers.skipDocstring

class HardcodedPasswordInspection : PyInspection() {
    val check = Checks.HardcodedPasswordCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        override fun visitPyAssignmentStatement(node: PyAssignmentStatement) {
            if (skipDocstring(node)) return
            val left = node.leftHandSideExpression ?: return
            if (left !is PyTargetExpression) return
            if (!listOf(*PasswordVariableNames).contains(left.name)) return
            val right = node.assignedValue ?: return
            if (right !is PyStringLiteralExpression) return
            holder.registerProblem(node, Checks.HardcodedPasswordCheck.getDescription(false))
        }
    }
}