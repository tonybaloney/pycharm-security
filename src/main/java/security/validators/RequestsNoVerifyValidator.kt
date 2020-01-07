package security.validators

import com.jetbrains.python.psi.PyBoolLiteralExpression
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.validation.PyAnnotator
import security.Checks
import security.helpers.QualifiedNames.getQualifiedName

class RequestsNoVerifyValidator : PyAnnotator() {
    override fun visitPyCallExpression(node: PyCallExpression) {
        if (node.callee == null) return
        val requestsMethodNames = arrayOf("get", "post", "options", "delete", "put", "patch", "head")
        if (!listOf(*requestsMethodNames).contains(node.callee!!.name)) return
        if (!getQualifiedName(node)!!.startsWith("requests.")) return
        if (node.getKeywordArgument("verify") == null) return
        if ((node.getKeywordArgument("verify") as PyBoolLiteralExpression?)!!.value) return
        holder.createWarningAnnotation(node, Checks.RequestsNoVerifyCheck.toString())
    }
}