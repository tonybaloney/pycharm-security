package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyAssignmentStatement
import security.Checks

class DjangoDebugModeSettingsInspection : PyInspection() {

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
        override fun visitPyAssignmentStatement(node: PyAssignmentStatement?) {
            if (node?.containingFile?.name != "settings.py") return;
            val leftExpression = node.leftHandSideExpression?.text ?: return
            if (leftExpression != "DEBUG") return;
            val assignedValue = node.assignedValue ?: return
            if (assignedValue.textMatches("True").not()) return;
            holder?.registerProblem(node, Checks.DjangoDebugModeCheck.Message)
        }
    }
}