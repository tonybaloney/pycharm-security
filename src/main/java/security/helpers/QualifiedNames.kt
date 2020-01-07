package security.helpers

import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.resolve.PyResolveContext

object QualifiedNames {
    fun getQualifiedName(callExpression: PyCallExpression): String? {
        val resolveContext = PyResolveContext.defaultContext()
        val markedCallees = callExpression.multiResolveCallee(resolveContext)
        return if (markedCallees.isEmpty()) null else markedCallees[0].element?.qualifiedName
    }
}