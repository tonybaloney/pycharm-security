package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyBoolLiteralExpression
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyListLiteralExpression
import com.jetbrains.python.psi.PyStringLiteralExpression
import security.Checks
import security.fixes.ShellEscapeFixer
import security.helpers.QualifiedNames.getQualifiedName

class SubprocessShellModeInspection : PyInspection() {
    val check = Checks.SubprocessShellCheck;

    override fun getStaticDescription(): String? {
        return check.getDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            val shellMethodNames = arrayOf("call", "run", "Popen")
            val qualifiedName = getQualifiedName(node) ?: return

            // Check this is an import from the subprocess module
            if (qualifiedName.startsWith("subprocess.").not()) return
            val calleeName = node.callee?.name ?: return
            if (node.arguments.isNullOrEmpty()) return

            // Match the method name against one of shellMethodNames
            if (!listOf(*shellMethodNames).contains(calleeName)) return

            // Look for the shell=True argument
            val shellArgument = node.getKeywordArgument("shell") ?: return
            if (shellArgument !is PyBoolLiteralExpression) return
            if (shellArgument.value.not()) return

            // If the first argument is a single string literal, this is ok.
            if (node.arguments.first() is PyStringLiteralExpression) return

            // If the first argument is a call to shlex.quote, this is ok
            if (node.arguments.first() is PyCallExpression && node.arguments.first().name?.endsWith("quote") ?: return) return

            // If the first argument is a list of string literals, this is ok.
            if (node.arguments.first() is PyListLiteralExpression) {
                val list = node.arguments.first() as PyListLiteralExpression
                if (list.elements.all { el -> el is PyStringLiteralExpression }) return

                // If all of the non-string literals are calls to shlex.quote, this is ok
                // Also, stops the fixer from being recommended twice
                if (list.elements.any { el -> el is PyCallExpression && (el.callee?.name == "shlex_quote" || el.callee?.name == "quote") }) return
            }

            holder?.registerProblem(node.arguments.first(), Checks.SubprocessShellCheck.getDescription(custom=node.text), ShellEscapeFixer())
        }
    }
}