package security.validators

import com.intellij.codeInsight.intention.IntentionAction
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.validation.PyAnnotator
import security.Checks
import security.fixes.TempfileMksFixer
import security.helpers.QualifiedNames.getQualifiedName

class TempfileMktempValidator : PyAnnotator() {
    override fun visitPyCallExpression(node: PyCallExpression) {
        if (node.callee == null) return
        if (node.callee!!.name != "mktemp") return
        if (getQualifiedName(node) != "tempfile.mktemp") return
        val annotation = holder.createWarningAnnotation(node, Checks.TempfileMktempCheck.toString())
        annotation.registerFix((TempfileMksFixer() as IntentionAction), node.textRange)
    }
}