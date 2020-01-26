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
import com.jetbrains.python.psi.PyElementGenerator
import com.jetbrains.python.psi.PyListLiteralExpression

class DjangoAddMiddlewareFixer : LocalQuickFix, IntentionAction, HighPriorityAction {
    val middleware: String

    constructor(middleware: String){
        this.middleware = middleware
    }

    override fun getText(): String {
        return name
    }

    override fun getFamilyName(): String {
        return "Add middleware"
    }

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        return true
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val listElement = getListLiteralExpressionAtCaret(file, editor) ?: return
        val newElement = getNewExpression(project, listElement) ?: return
        ApplicationManager.getApplication().runWriteAction { listElement.replace(newElement) }
    }

    fun getNewExpression(project: Project, oldList: PyListLiteralExpression): PyListLiteralExpression? {
        val elementGenerator = PyElementGenerator.getInstance(project)
        val newList = oldList.copy() as PyListLiteralExpression
        newList.add(elementGenerator.createStringLiteralFromString(this.middleware))
        return newList
    }

    override fun startInWriteAction(): Boolean {
        return true
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        if (descriptor.psiElement !is PyListLiteralExpression) return
        val newEl = getNewExpression(project, descriptor.psiElement as PyListLiteralExpression) ?: return
        descriptor.psiElement.replace(newEl)
    }
}