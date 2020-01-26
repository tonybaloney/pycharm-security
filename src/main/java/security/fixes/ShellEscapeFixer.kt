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
import security.helpers.QualifiedNames.getQualifiedName


class ShellEscapeFixer : LocalQuickFix, IntentionAction, HighPriorityAction {
    override fun getText(): String {
        return name
    }

    override fun getFamilyName(): String {
        return "Escape Input"
    }

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        return true
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val el = getPyExpressionAtCaret(file, editor) ?: return
        ApplicationManager.getApplication().runWriteAction {
            if (el is PyListLiteralExpression)
                el.replace(getNewExpressionFromList(file, project, el) ?: return@runWriteAction)
            else
                el.replace(getNewExpressionFromPyExpression(file, project, el) ?: return@runWriteAction)
        }
    }

    fun getNewExpressionFromPyExpression(file: PsiFile, project: Project, oldElement: PyExpression): PyCallExpression?
    {
        if (file !is PyFile) return null
        importFrom(file, project, "shlex", "quote", "shlex_quote")
        return getNewEscapedExpression(file, project, oldElement)
    }

    fun getNewEscapedExpression(file: PsiFile, project: Project, oldElement: PyExpression): PyCallExpression? {
        val elementGenerator = PyElementGenerator.getInstance(project)
        if (file !is PyFile) return null
        var newEl = elementGenerator.createCallExpression(file.languageLevel, "shlex_quote")
        newEl.argumentList?.addArgument(oldElement)
        return newEl
    }

    fun getNewExpressionFromList(file: PsiFile, project: Project, oldElement: PyListLiteralExpression): PyListLiteralExpression? {
        val elementGenerator = PyElementGenerator.getInstance(project)
        if (file !is PyFile) return null
        importFrom(file, project, "shlex", "quote", "shlex_quote")
        var list = elementGenerator.createListLiteral()
        for (item in oldElement.elements){
            if (item is PyReferenceExpression){
                list.add(getNewEscapedExpression(file, project, item) as @org.jetbrains.annotations.NotNull PsiElement)
                continue
            } else if (item is PyCallExpression) {
                if (getQualifiedName(item) != "shlex.quote") {
                    list.add(getNewEscapedExpression(file, project, item) as @org.jetbrains.annotations.NotNull PsiElement)
                    continue
                }
            }
            list.add(item)
        }
        return list
    }

    override fun startInWriteAction(): Boolean {
        return true
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val el = descriptor.psiElement
        if (el is PyListLiteralExpression) {
            val newEl = getNewExpressionFromList(descriptor.psiElement.containingFile, project, el) ?: return
            el.replace(newEl) ?: return
        }
        else {
            val newEl = getNewExpressionFromPyExpression(descriptor.psiElement.containingFile, project, el as PyExpression) ?: return
            el.replace(newEl) ?: return
        }
    }
}