package security.validators

import com.intellij.codeInsight.intention.IntentionAction
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.validation.PyAnnotator
import security.Checks
import security.fixes.TempfileMksFixer
import security.helpers.QualifiedNames.getQualifiedName

class TempfileMktempValidator : PyAnnotator() {
    override fun visitPyCallExpression(node: PyCallExpression) {
        val calleeName = node.callee?.name ?: return
        if (calleeName != "mktemp") return
        val qualifiedName = getQualifiedName(node) ?: return
        if (qualifiedName != "tempfile.mktemp") return
        val annotation = holder.createWarningAnnotation(node, Checks.TempfileMktempCheck.toString())
        annotation.registerFix((TempfileMksFixer() as IntentionAction), node.textRange)
    }
}