package security.helpers

import com.jetbrains.python.psi.PyCallExpression

fun calleeMatches(node: PyCallExpression, potential: Array<String>) : Boolean {
    val calleeName = node.callee?.name ?: return false
    return listOf(*potential).contains(calleeName)
}

fun calleeMatches(node: PyCallExpression, potential: String) : Boolean {
    val calleeName = node.callee?.name ?: return false
    return potential.equals(calleeName)
}