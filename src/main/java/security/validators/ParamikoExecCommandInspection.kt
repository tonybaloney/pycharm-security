package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.PyStringLiteralExpression
import security.Checks
import security.fixes.ShellEscapeFixer
import security.helpers.ImportValidators
import security.helpers.SecurityVisitor
import security.helpers.calleeMatches
import security.helpers.skipDocstring

class ParamikoExecCommandInspection : PyInspection() {
    val check = Checks.ParamikoExecCommandCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return

            if (!ImportValidators.hasImportedNamespace(node.containingFile as PyFile, "paramiko")) return

            if (!calleeMatches(node, "exec_command")) return

            if (node.arguments.isNullOrEmpty()) return

            // If the first argument is a single string literal, this is ok.
            if (node.arguments.first() is PyStringLiteralExpression) return

            // If the first argument is a call to shlex.quote, this is ok
            if (node.arguments.first() is PyCallExpression) {
                if (node.arguments.first().name != null && node.arguments.first().name!!.endsWith("quote"))
                    return
            }

            holder.registerProblem(node.arguments.first(), Checks.ParamikoExecCommandCheck.getDescription(), ShellEscapeFixer())
        }
    }
}