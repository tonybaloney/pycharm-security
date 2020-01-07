package security.fixes

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import com.jetbrains.python.psi.LanguageLevel
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyElementGenerator

class TempfileMksFixer : LocalQuickFix, IntentionAction, HighPriorityAction {
    override fun getText(): String {
        return name
    }

    override fun getFamilyName(): String {
        return "Use tempfile.mkstemp()"
    }

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        return true
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val call = PsiTreeUtil.getParentOfType(file.findElementAt(editor.caretModel.offset), PyCallExpression::class.java)
        val elementGenerator = PyElementGenerator.getInstance(project)
        if (call == null) return
        val newEl = elementGenerator.createExpressionFromText(LanguageLevel.getDefault(), call.text.replace("mktemp", "mkstemp")) as PyCallExpression
        ApplicationManager.getApplication().runWriteAction { call.replace(newEl) }
    }

    override fun startInWriteAction(): Boolean {
        return false
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
                ?: // stale PSI
                return
    }
}