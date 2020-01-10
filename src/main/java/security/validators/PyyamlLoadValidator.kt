package security.validators

import com.intellij.codeInsight.intention.IntentionAction
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.validation.PyAnnotator
import security.Checks
import security.fixes.PyyamlSafeLoadFixer
import security.helpers.QualifiedNames.getQualifiedName

class PyyamlLoadValidator : PyAnnotator() {
    override fun visitPyCallExpression(node: PyCallExpression) {
        val calleeName = node.callee?.name ?: return
        if (calleeName != "load") return
        val qualifiedName = getQualifiedName(node) ?: return
        if (!qualifiedName.equals("yaml.load")) return
        val annotation = holder.createWarningAnnotation(node, Checks.PyyamlUnsafeLoadCheck.toString())
        annotation.registerFix((PyyamlSafeLoadFixer() as IntentionAction), node.textRange)
    }
}