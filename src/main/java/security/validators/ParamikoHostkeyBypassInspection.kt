package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyReferenceExpression
import security.Checks

class ParamikoHostkeyBypassInspection : PyInspection() {
    val check = Checks.ParamikoHostkeyBypassCheck;

    override fun getStaticDescription(): String? {
        return check.getDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            val badPolicyNames = arrayOf("AutoAddPolicy", "WarningPolicy")
            val calleeName = node.callee?.name ?: return
            if (calleeName != "set_missing_host_key_policy") return
            // Get first arg
            if (node.arguments.isNullOrEmpty()) return
            if (node.arguments.first() !is PyReferenceExpression) return
            if (!listOf(*badPolicyNames).contains(node.arguments.first().name)) return
            holder?.registerProblem(node, Checks.ParamikoHostkeyBypassCheck.getDescription(custom=node.text))
        }
    }
}