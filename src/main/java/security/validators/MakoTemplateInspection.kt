package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyCallExpression
import security.Checks
import security.fixes.MakoFilterFixer
import security.helpers.QualifiedNames.getQualifiedName

class MakoTemplateInspection : PyInspection() {
    val check = Checks.MakoTemplateFilterCheck;

    override fun getStaticDescription(): String? {
        return check.getDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            val calleeName = node.callee?.name ?: return
            if (calleeName != "Template") return
            val qualifiedName = getQualifiedName(node) ?: return
            if (!qualifiedName.startsWith("mako.")) return
            val defaultFiltersArgument = node.getKeywordArgument("default_filters")
            if (defaultFiltersArgument == null)
            {
                holder?.registerProblem(node, Checks.MakoTemplateFilterCheck.getDescription(), MakoFilterFixer())
            }
        }
    }
}