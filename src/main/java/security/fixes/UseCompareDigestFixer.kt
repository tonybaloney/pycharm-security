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
import com.jetbrains.python.psi.*

class UseCompareDigestFixer : LocalQuickFix, IntentionAction, HighPriorityAction {
    override fun getText(): String {
        return name
    }

    override fun getFamilyName(): String {
        return "Use compare_digest()"
    }

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        return true
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        ApplicationManager.getApplication().runWriteAction {
            getBinaryExpressionElementAtCaret(file, editor)?.let { runFix(project, file, it) }
        }
    }

    fun getNewExpressionAtCaret(file: PsiFile, project: Project, oldElement: PyBinaryExpression): PyCallExpression? {
        val elementGenerator = PyElementGenerator.getInstance(project)
        if (file !is PyFile) return null
        val languageLevel = file.languageLevel
        var compareDigestModule = "hmac"
        if (languageLevel.isAtLeast(LanguageLevel.PYTHON37))
            compareDigestModule = "secrets"
        importFrom(file, project, compareDigestModule, "compare_digest")
        val el = elementGenerator.createCallExpression(languageLevel, "compare_digest")
        el.argumentList?.addArgument(oldElement.leftExpression)
        oldElement.rightExpression?.let { el.argumentList?.addArgument(it) }
        return el
    }

    override fun startInWriteAction(): Boolean {
        return true
    }

    fun runFix(project: Project, file: PsiFile, originalElement: PsiElement){
        if (originalElement !is PyBinaryExpression) return
        val newEl = getNewExpressionAtCaret(file, project, originalElement) ?: return
        originalElement.replace(newEl)
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        runFix(project, descriptor.psiElement.containingFile, descriptor.psiElement)
    }
}