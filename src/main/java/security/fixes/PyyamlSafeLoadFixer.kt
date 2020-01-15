package security.fixes

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import com.jetbrains.python.psi.PyCallExpression

class PyyamlSafeLoadFixer : LocalQuickFix, IntentionAction, HighPriorityAction {
    override fun getText(): String {
        return name
    }

    override fun getFamilyName(): String {
        return "Use safe_load()"
    }

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        return true
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val callElement = getCallElementAtCaret(file, editor) ?: return
        val newElement = getNewExpressionAtCaret(file, editor, project) ?: return
        ApplicationManager.getApplication().runWriteAction { callElement.replace(newElement) }
    }

    fun getNewExpressionAtCaret(file: PsiFile, editor: Editor, project: Project): PyCallExpression? {
        return getNewCallExpressiontAtCaret(file, editor, project, "load", "safe_load")
    }

    override fun startInWriteAction(): Boolean {
        return true
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        return
    }
}