package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyCallExpression
import security.Checks
import security.helpers.QualifiedNames

class DjangoSafeStringInspection : PyInspection() {
    val check = Checks.DjangoSafeStringCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
        val methodNames = arrayOf("SafeString", "mark_safe", "SafeBytes", "SafeUnicode", "SafeText")
        override fun visitPyCallExpression(node: PyCallExpression) {
            val calleeName = node.callee?.name ?: return
            if (!listOf(*methodNames).contains(calleeName)) return
            val qualifiedName = QualifiedNames.getQualifiedName(node) ?: return
            if (!qualifiedName.startsWith("django.utils.safestring")) return
            holder?.registerProblem(node, Checks.DjangoSafeStringCheck.getDescription(), ProblemHighlightType.WEAK_WARNING)
        }
    }
}