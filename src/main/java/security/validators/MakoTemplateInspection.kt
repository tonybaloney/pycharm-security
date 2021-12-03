package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyCallExpression
import security.Checks
import security.fixes.MakoFilterFixer
import security.helpers.SecurityVisitor
import security.helpers.calleeMatches
import security.helpers.qualifiedNameStartsWith
import security.helpers.skipDocstring

class MakoTemplateInspection : PyInspection() {
    val check = Checks.MakoTemplateFilterCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return
            if (!calleeMatches(node, "Template")) return
            if (!qualifiedNameStartsWith(node, "mako.", typeEvalContext)) return
            val defaultFiltersArgument = node.getKeywordArgument("default_filters")
            if (defaultFiltersArgument == null)
            {
                holder.registerProblem(node, Checks.MakoTemplateFilterCheck.getDescription(), MakoFilterFixer())
            }
        }
    }
}