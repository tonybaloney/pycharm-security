package security.fixes

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.psi.*


fun getPyExpressionAtCaret(file: PsiFile, editor: Editor): PyExpression? {
    return PsiTreeUtil.getParentOfType(file.findElementAt(editor.caretModel.offset), PyExpression::class.java) ?: return null
}

fun getPyCallExpressionAtCaret(file: PsiFile, editor: Editor): PyCallExpression? {
    return PsiTreeUtil.getParentOfType(file.findElementAt(editor.caretModel.offset), PyCallExpression::class.java) ?: return null
}

fun getListLiteralExpressionAtCaret(file: PsiFile, editor: Editor): PyListLiteralExpression? {
    return PsiTreeUtil.getParentOfType(file.findElementAt(editor.caretModel.offset), PyListLiteralExpression::class.java) ?: return null
}

fun getBinaryExpressionElementAtCaret(file: PsiFile, editor: Editor): PyBinaryExpression? {
    return PsiTreeUtil.getParentOfType(file.findElementAt(editor.caretModel.offset), PyBinaryExpression::class.java) ?: return null
}

fun getNewCallExpressiontAtCaret(file: PsiFile, editor: Editor, project: Project, old: String, new: String): PyCallExpression ? {
    val callElement = getPyCallExpressionAtCaret(file, editor) ?: return null
    val elementGenerator = PyElementGenerator.getInstance(project)
    return elementGenerator.createExpressionFromText(LanguageLevel.getDefault(), callElement.text.replace(old, new)) as PyCallExpression
}

fun import(file: PyFile, project: Project, target: String, alias: String = ""){
    val languageLevel = file.languageLevel
    val newImportFrom = PyElementGenerator.getInstance(project).createImportStatement(languageLevel, target, alias)
    if (file.importBlock.isNotEmpty()) {
        val lastImport = file.importBlock.last()
        if (file.importTargets.any{ im -> im.textMatches(newImportFrom) }.not())
            file.addAfter(newImportFrom, lastImport)
    } else {
        file.addBefore(newImportFrom, file.statements.first())
    }
}

fun importFrom(file: PyFile, project: Project, target: String, component: String, alias: String = ""){
    val languageLevel = file.languageLevel
    val newImportFrom = PyElementGenerator.getInstance(project).createFromImportStatement(languageLevel, target, component, alias)
    if (file.importBlock.isNotEmpty()) {
        val lastImport = file.importBlock.last()
        if (file.fromImports.any{ im -> im.textMatches(newImportFrom) }.not())
            file.addAfter(newImportFrom, lastImport)
    } else {
        file.addBefore(newImportFrom, file.statements.first())
    }
}