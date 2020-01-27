package security.fixes

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import com.jetbrains.python.psi.LanguageLevel
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyElementGenerator

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
        ApplicationManager.getApplication().runWriteAction { getPyCallExpressionAtCaret(file, editor)?.let { runFix(project, file, it) } }
    }

    fun runFix(project: Project, file: PsiFile, originalElement: PsiElement){
        if (originalElement !is PyCallExpression) return
        val elementGenerator = PyElementGenerator.getInstance(project)
        val newEl = elementGenerator.createExpressionFromText(LanguageLevel.getDefault(), originalElement.text.replace("load", "safe_load")) as PyCallExpression
        originalElement.replace(newEl)
    }

    fun getNewExpressionAtCaret(file: PsiFile, editor: Editor, project: Project): PyCallExpression? {
        return getNewCallExpressiontAtCaret(file, editor, project, "load", "safe_load")
    }

    override fun startInWriteAction(): Boolean {
        return true
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
       runFix(project, descriptor.psiElement.containingFile, descriptor.psiElement)
    }
}