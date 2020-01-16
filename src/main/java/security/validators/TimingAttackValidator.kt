package security.validators

import com.intellij.codeInsight.intention.IntentionAction
import com.jetbrains.python.psi.PyBinaryExpression
import com.jetbrains.python.psi.PyReferenceExpression
import com.jetbrains.python.validation.PyAnnotator
import security.Checks
import security.fixes.UseCompareDigestFixer

class TimingAttackValidator: PyAnnotator() {
    val passwordVariableNames = arrayOf("password", "PASSWORD", "passwd", "secret", "token")

    override fun visitPyBinaryExpression(node: PyBinaryExpression?) {
        if (node == null) return
        val rightExpression = node.rightExpression ?: return
        val leftExpression = node.leftExpression ?: return
        if (rightExpression is PyReferenceExpression)
        {
            if (looksLikeAPassword(rightExpression)) {
                var an = holder.createWarningAnnotation(node, Checks.TimingAttackCheck.toString())
                an.registerFix((UseCompareDigestFixer() as IntentionAction), node.textRange)
                an.registerFix(Checks.TimingAttackCheck.getIntentionAction())
            }
        }
        if (leftExpression is PyReferenceExpression)
        {
            if (looksLikeAPassword(leftExpression)) {
                var an = holder.createWarningAnnotation(node, Checks.TimingAttackCheck.toString())
                an.registerFix((UseCompareDigestFixer() as IntentionAction), node.textRange)
                an.registerFix(Checks.TimingAttackCheck.getIntentionAction())
            }
        }
    }

    private fun looksLikeAPassword(expression: PyReferenceExpression): Boolean {
        return listOf(*passwordVariableNames).contains(expression.name)
    }
}