package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyFormattedStringElement
import com.jetbrains.python.psi.PyReferenceExpression
import com.jetbrains.python.psi.PyStringLiteralExpression
import security.Checks
import security.helpers.SecurityVisitor
import security.helpers.skipDocstring

class StrFormatInspection : PyInspection() {
    val check = Checks.StrFormatInspectionCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            if (node.arguments.isEmpty()) return
            if (skipDocstring(node)) return
            val callee = node.callee
            if (callee?.name != "format") return
            if(callee is PyReferenceExpression) {
                val qualifier = callee.qualifier
                if (qualifier is PyStringLiteralExpression && qualifier.stringElements.isNotEmpty()) {
                    val strFormat = qualifier.stringElements[0]
                    if(strFormat !is PyFormattedStringElement){
                        return
                    }
                }
                if (qualifier?.name == "self"){
                    // TODO improve check if self is class ref or is overwrite var
                    // TODO improve check if class extends from str
                    return
                }
            }
            holder.registerProblem(node, Checks.StrFormatInspectionCheck.getDescription(), ProblemHighlightType.WEAK_WARNING)
        }
    }
}
