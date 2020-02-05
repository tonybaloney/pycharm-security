package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.PyStringLiteralExpression
import security.Checks
import security.helpers.ImportValidators.hasImportedNamespace

class DjangoSafeStringInspection : PyInspection() {
    val check = Checks.DjangoSafeStringCheck;

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
        val methodNames = arrayOf("SafeString", "mark_safe", "SafeBytes", "SafeUnicode", "SafeText")
        override fun visitPyCallExpression(node: PyCallExpression?) {
            if (node == null) return
            val calleeName = node.callee?.name ?: return
            if (!listOf(*methodNames).contains(calleeName)) return

            if (node.containingFile !is PyFile) return
            if (!hasImportedNamespace(node.containingFile as PyFile, "django.utils.safestring")) return

            holder?.registerProblem(node, Checks.DjangoSafeStringCheck.getDescription(), ProblemHighlightType.WEAK_WARNING)

        }
    }
}