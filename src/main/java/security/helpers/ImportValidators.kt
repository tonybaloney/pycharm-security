package security.helpers

import com.jetbrains.python.psi.PyFile

object ImportValidators {
    fun hasImportedNamespace(file: PyFile, match: String) : Boolean {
        val imports = file.importBlock ?: return false
        if (imports.isEmpty()) return false
        for (imp in imports){
            if (imp.fullyQualifiedObjectNames.isEmpty()) continue
            if (imp.fullyQualifiedObjectNames.first() == match) return true
            if (imp.fullyQualifiedObjectNames.first().startsWith("$match.")) return true
        }
        return false
    }
}