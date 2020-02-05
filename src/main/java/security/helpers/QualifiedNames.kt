package security.helpers

import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyReferenceExpression
import com.jetbrains.python.psi.resolve.PyResolveContext

object QualifiedNames {
    var resolveContext: PyResolveContext = PyResolveContext.defaultContext()

    fun getQualifiedName(callExpression: PyCallExpression): String? {
        val markedCallees = callExpression.multiResolveCallee(resolveContext)
        if (markedCallees.isEmpty()) {
            val firstChild = callExpression.firstChild ?: return null
            if (firstChild !is PyReferenceExpression) return null
            val qualifiedName = (firstChild).asQualifiedName() ?: return null
            return qualifiedName.toString()
        }
        else
            return markedCallees[0].element?.qualifiedName ?: markedCallees[0].element?.name
    }
}