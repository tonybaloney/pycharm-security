package security.validators

import com.jetbrains.python.psi.PyBoolLiteralExpression
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyReferenceExpression
import com.jetbrains.python.validation.PyAnnotator
import security.Checks

class FlaskDebugModeValidator : PyAnnotator() {
    override fun visitPyCallExpression(node: PyCallExpression) {
        if (node.callee == null) return
        if (node.callee!!.name != "run") return
        if ((node.firstChild as PyReferenceExpression).asQualifiedName().toString() != "app.run") return
        if (node.getKeywordArgument("debug") == null) return
        if (!(node.getKeywordArgument("debug") as PyBoolLiteralExpression?)!!.value) return
        holder.createWarningAnnotation(node, Checks.FlaskDebugModeCheck.toString())
    }
}