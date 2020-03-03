package security.helpers

import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfTypes
import com.jetbrains.python.documentation.doctest.PyDocstringFile
import security.settings.SecuritySettings

fun skipDocstring(node: PsiElement): Boolean {
    if (SecuritySettings.instance.ignoreDocstrings)
        if (node.parentOfTypes(PyDocstringFile::class) != null)
            return true
    return false
}