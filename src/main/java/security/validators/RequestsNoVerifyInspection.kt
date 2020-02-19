package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyBoolLiteralExpression
import com.jetbrains.python.psi.PyCallExpression
import security.Checks
import security.helpers.QualifiedNames.getQualifiedName
import security.helpers.SecurityVisitor
import security.helpers.skipDocstring

class RequestsNoVerifyInspection : PyInspection() {
    val check = Checks.RequestsNoVerifyCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return
            val requestsMethodNames = arrayOf("get", "post", "options", "delete", "put", "patch", "head")
            val calleeName = node.callee?.name ?: return
            if (!listOf(*requestsMethodNames).contains(calleeName)) return
            val qualifiedName = getQualifiedName(node) ?: return
            if (!qualifiedName.startsWith("requests.")) return
            val verifyArgument = node.getKeywordArgument("verify") ?: return
            if (verifyArgument !is PyBoolLiteralExpression) return
            if (verifyArgument.value) return
            holder.registerProblem(node, Checks.RequestsNoVerifyCheck.getDescription())
        }
    }
}