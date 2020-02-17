package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyListLiteralExpression
import com.jetbrains.python.psi.PyStringLiteralExpression
import security.Checks
import security.fixes.ShellEscapeFixer
import security.helpers.QualifiedNames.getQualifiedName
import security.helpers.SecurityVisitor

class StandardShellInjectionInspection : PyInspection() {
    val check = Checks.ShellInjectionCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            val qualifiedName = getQualifiedName(node) ?: return

            // Check this is one of the possible shell APIs
            if (listOf(*ShellApis).contains(qualifiedName).not()) return

            if (node.arguments.isNullOrEmpty()) return

            // If the first argument is a single string literal, this is ok.
            if (node.arguments.first() is PyStringLiteralExpression) return

            // If the first argument is a call to shlex.quote, this is ok
            if (node.arguments.first() is PyCallExpression) {
                if (node.arguments.first().name != null && node.arguments.first().name!!.endsWith("quote"))
                    return
            }

            // If the first argument is a list of string literals, this is ok.
            if (node.arguments.first() is PyListLiteralExpression) {
                val list = node.arguments.first() as PyListLiteralExpression
                if (list.elements.all { el -> el is PyStringLiteralExpression }) return

                // If all of the non-string literals are calls to shlex.quote, this is ok
                // Also, stops the fixer from being recommended twice
                if (list.elements.any { el -> el is PyCallExpression && (el.callee?.name == "shlex_quote" || el.callee?.name == "quote") }) return
            }

            holder.registerProblem(node.arguments.first(), Checks.ShellInjectionCheck.getDescription(), ShellEscapeFixer())
        }
    }
}