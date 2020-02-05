package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.*
import security.Checks

class BindAllInterfacesInspection : PyInspection() {
    val check = Checks.BindAllInterfacesCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
        val allInterfacesStrings = arrayOf("0.0.0.0", "::", "0:0:0:0:0:0:0:0") // IPv4 and IPv6

        private fun isMatch(el: PsiElement): Boolean {
            if (el !is PyStringLiteralExpression) return false
            return (listOf(*allInterfacesStrings).contains((el).stringValue))
        }

        override fun visitPyCallExpression(node: PyCallExpression) {
            val calleeName = node.callee?.name ?: return
            if (calleeName != "bind") return
            if (node.arguments.isNullOrEmpty()) return
            val firstArg = node.arguments.first() ?: return

            // Takes single argument (IP)
            if (isMatch(firstArg))
                holder?.registerProblem(node, Checks.BindAllInterfacesCheck.getDescription(), ProblemHighlightType.WEAK_WARNING)

            if (firstArg is PyParenthesizedExpression){
                val exp = firstArg.containedExpression ?: return
                // Takes two arguments as tuple (IP, port), e.g. TCP, UDP
                if (exp is PyTupleExpression && !exp.isEmpty) {
                    if (isMatch(exp.firstChild))
                        holder?.registerProblem(node, Checks.BindAllInterfacesCheck.getDescription(), ProblemHighlightType.WEAK_WARNING)
                }
            }
        }
    }
}