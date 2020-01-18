package security.validators

import com.jetbrains.python.psi.PyBoolLiteralExpression
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.validation.PyAnnotator
import security.Checks
import security.create
import security.helpers.QualifiedNames.getQualifiedName

class RequestsNoVerifyValidator : PyAnnotator() {
    override fun visitPyCallExpression(node: PyCallExpression) {
        val requestsMethodNames = arrayOf("get", "post", "options", "delete", "put", "patch", "head")
        val calleeName = node.callee?.name ?: return
        if (!listOf(*requestsMethodNames).contains(calleeName)) return
        val qualifiedName = getQualifiedName(node) ?: return
        if (!qualifiedName.startsWith("requests.")) return
        val verifyArgument = node.getKeywordArgument("verify") ?: return
        if (verifyArgument !is PyBoolLiteralExpression) return
        if (verifyArgument.value) return
        holder.create(node, Checks.RequestsNoVerifyCheck)
    }
}