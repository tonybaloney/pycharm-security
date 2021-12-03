package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyBoolLiteralExpression
import com.jetbrains.python.psi.PyCallExpression
import security.Checks
import security.fixes.JinjaAutoinspectUnconditionalFixer
import security.helpers.SecurityVisitor
import security.helpers.calleeMatches
import security.helpers.qualifiedNameStartsWith
import security.helpers.skipDocstring

class JinjaAutoinspectInspection : PyInspection() {
    val check = Checks.JinjaAutoinspectCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return
            if (!calleeMatches(node, arrayOf("Environment", "Template"))) return
            if (!qualifiedNameStartsWith(node, "jinja2.", typeEvalContext)) return
            val autoescapeArgument = node.getKeywordArgument("autoescape")
            if (autoescapeArgument == null)
            {
                holder.registerProblem(node, Checks.JinjaAutoinspectCheck.getDescription(), JinjaAutoinspectUnconditionalFixer())
            } else {
                if (autoescapeArgument !is PyBoolLiteralExpression) return
                if (autoescapeArgument.value) return
                holder.registerProblem(node, Checks.JinjaAutoinspectCheck.getDescription(), JinjaAutoinspectUnconditionalFixer())
            }
        }
    }
}