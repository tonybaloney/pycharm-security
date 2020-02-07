package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyAssertStatement
import security.Checks
import security.helpers.SecurityVisitor

class AssertInspection : PyInspection() {
    val check = Checks.AssertCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        override fun visitPyAssertStatement(node: PyAssertStatement) {
            if (node.containingFile.name.contains("test"))
                return
            holder.registerProblem(node, Checks.AssertCheck.getDescription(), ProblemHighlightType.WEAK_WARNING)
        }
    }
}