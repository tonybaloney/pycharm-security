package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyReferenceExpression
import security.Checks
import security.helpers.SecurityVisitor
import security.helpers.calleeMatches
import security.helpers.hasImportedNamespace
import security.helpers.skipDocstring

class ParamikoHostkeyBypassInspection : PyInspection() {
    val check = Checks.ParamikoHostkeyBypassCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        val badPolicyNames = arrayOf("AutoAddPolicy", "WarningPolicy")

        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return
            if (!hasImportedNamespace(node.containingFile, "paramiko")) return
            if (!calleeMatches(node, "set_missing_host_key_policy")) return

            // Get first arg
            if (node.arguments.isNullOrEmpty()) return
            if (node.arguments.first() !is PyReferenceExpression) return
            if (!listOf(*badPolicyNames).contains(node.arguments.first().name)) return
            holder.registerProblem(node, Checks.ParamikoHostkeyBypassCheck.getDescription())
        }
    }
}