package security.validators

import com.jetbrains.python.psi.PyBoolLiteralExpression
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.validation.PyAnnotator
import security.Checks
import security.create
import security.helpers.QualifiedNames.getQualifiedName

class SubprocessCallShellModeValidator : PyAnnotator() {
    override fun visitPyCallExpression(node: PyCallExpression) {
        val qualifiedName = getQualifiedName(node) ?: return
        if (qualifiedName != "subprocess.call") return
        val shellArgument = node.getKeywordArgument("shell") ?: return
        if ((shellArgument as PyBoolLiteralExpression?)!!.value.not()) return
        holder.create(node, Checks.SubprocessCallShellCheck)
    }
}