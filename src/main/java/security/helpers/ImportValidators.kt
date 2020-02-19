package security.helpers

import com.intellij.psi.PsiFile
import com.jetbrains.python.psi.PyFile

fun hasImportedNamespace(file: PsiFile, match: String) : Boolean {
    if (file !is PyFile) return false
    val imports = file.importBlock ?: return false
    if (imports.isEmpty()) return false
    for (imp in imports){
        if (imp.fullyQualifiedObjectNames.isEmpty()) continue
        if (imp.fullyQualifiedObjectNames.first() == match) return true
        if (imp.fullyQualifiedObjectNames.first().startsWith("$match.")) return true
    }
    return false
}