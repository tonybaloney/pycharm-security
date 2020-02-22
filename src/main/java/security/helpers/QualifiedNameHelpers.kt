package security.helpers

import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyReferenceExpression
import com.jetbrains.python.psi.resolve.PyResolveContext

object QualifiedNameHelpers {
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

fun qualifiedNameMatches(node: PyCallExpression, potential: Array<String>) : Boolean {
    val qualifiedName = QualifiedNameHelpers.getQualifiedName(node) ?: return false
    return listOf(*potential).contains(qualifiedName)
}

fun qualifiedNameMatches(node: PyCallExpression, potential: String) : Boolean {
    val qualifiedName = QualifiedNameHelpers.getQualifiedName(node) ?: return false
    return (qualifiedName.equals(potential))
}

fun qualifiedNameStartsWith(node: PyCallExpression, potential: String) : Boolean {
    val qualifiedName = QualifiedNameHelpers.getQualifiedName(node) ?: return false
    return (qualifiedName.startsWith(potential))
}

fun qualifiedNameStartsWith(node: PyCallExpression, potential: Array<String>) : Boolean {
    val qualifiedName = QualifiedNameHelpers.getQualifiedName(node) ?: return false
    return potential.any { qualifiedName.startsWith(it) }
}