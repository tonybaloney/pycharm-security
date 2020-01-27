package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.*
import security.Checks
import security.fixes.UseCompareDigestFixer

class HardcodedPasswordInspection : PyInspection() {
    val check = Checks.HardcodedPasswordCheck;

    override fun getStaticDescription(): String? {
        return check.getDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
        override fun visitPyAssignmentStatement(node: PyAssignmentStatement?) {
            if (node == null) return
            val left = node.leftHandSideExpression ?: return
            if (left !is PyTargetExpression) return
            if (!listOf(*PasswordVariableNames).contains(left.name)) return
            val right = node.assignedValue ?: return
            if (right !is PyStringLiteralExpression) return
            holder?.registerProblem(node, Checks.HardcodedPasswordCheck.getDescription())
        }
    }
}