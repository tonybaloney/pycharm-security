package security.validators

import com.jetbrains.python.psi.PyBoolLiteralExpression
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.validation.PyAnnotator
import security.Checks
import security.helpers.QualifiedNames.getQualifiedName

class SubprocessCallShellModeValidator : PyAnnotator() {
    override fun visitPyCallExpression(node: PyCallExpression) {
        if (node.callee == null)
            return
        if (getQualifiedName(node) != "subprocess.call")
            return
        if (node.getKeywordArgument("shell") == null)
            return
        if ((node.getKeywordArgument("shell") as PyBoolLiteralExpression?)!!.value.not())
            return
        holder.createWarningAnnotation(node, Checks.SubprocessCallShellCheck.toString())
    }
}