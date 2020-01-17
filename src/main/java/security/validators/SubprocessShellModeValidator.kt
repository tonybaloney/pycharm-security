package security.validators

import com.intellij.codeInsight.intention.IntentionAction
import com.jetbrains.python.psi.*
import com.jetbrains.python.validation.PyAnnotator
import security.Checks
import security.create
import security.fixes.ShellEscapeFixer
import security.helpers.QualifiedNames.getQualifiedName

class SubprocessShellModeValidator : PyAnnotator() {
    override fun visitPyCallExpression(node: PyCallExpression) {
        val shellMethodNames = arrayOf("call", "run", "Popen")
        val qualifiedName = getQualifiedName(node) ?: return

        // Check this is an import from the subprocess module
        if (qualifiedName.startsWith("subprocess.").not()) return
        val calleeName = node.callee?.name ?: return
        if (node.arguments.isEmpty()) return

        // Match the method name against one of shellMethodNames
        if (!listOf(*shellMethodNames).contains(calleeName)) return

        // Look for the shell=True argument
        val shellArgument = node.getKeywordArgument("shell") ?: return
        if (shellArgument !is PyBoolLiteralExpression) return
        if (shellArgument.value.not()) return

        // If the first argument is a single string literal, this is ok.
        if (node.arguments.first() is PyStringLiteralExpression) return

        // If the first argument is a call to shlex.quote, this is ok
        if (node.arguments.first() is PyCallExpression && node.arguments.first().name?.endsWith("quote") ?: return) return

        // If the first argument is a list of string literals, this is ok.
        if (node.arguments.first() is PyListLiteralExpression){
            val list = node.arguments.first() as PyListLiteralExpression
            if (list.elements.all { el -> el is PyStringLiteralExpression }) return

            // If all of the non-string literals are calls to shlex.quote, this is ok
            // Also, stops the fixer from being recommended twice
            if (list.elements.any { el -> el is PyCallExpression && (el.callee?.name == "shlex_quote" || el.callee?.name == "quote") }) return
        }

        holder.create(node, Checks.SubprocessShellCheck).registerFix((ShellEscapeFixer() as IntentionAction), node.arguments.first().textRange)
    }
}