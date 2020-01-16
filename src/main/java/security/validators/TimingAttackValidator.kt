package security.validators

import com.jetbrains.python.psi.PyBinaryExpression
import com.jetbrains.python.psi.PyReferenceExpression
import com.jetbrains.python.validation.PyAnnotator
import security.Checks

class TimingAttackValidator: PyAnnotator() {
    val passwordVariableNames = arrayOf("password", "PASSWORD", "passwd", "secret", "token")

    override fun visitPyBinaryExpression(node: PyBinaryExpression?) {
        var a = 1
        val rightExpression = node?.rightExpression ?: return
        val leftExpression = node?.leftExpression ?: return
        if (rightExpression is PyReferenceExpression)
        {
            if (looksLikeAPassword(rightExpression))
                holder.createWarningAnnotation(node, Checks.TimingAttackCheck.toString())

        }
        if (leftExpression is PyReferenceExpression)
        {
            if (looksLikeAPassword(leftExpression))
                holder.createWarningAnnotation(node, Checks.TimingAttackCheck.toString())
        }
    }

    private fun looksLikeAPassword(expression: PyReferenceExpression): Boolean {
        return listOf<String>(*passwordVariableNames).contains(expression.name)
    }
}