package security.validators

import com.jetbrains.python.psi.PyAssignmentStatement
import com.jetbrains.python.validation.PyAnnotator
import security.Checks
import security.create

class DjangoDebugModeSettingsValidator: PyAnnotator() {
    override fun visitPyAssignmentStatement(node: PyAssignmentStatement?) {
        if (node?.containingFile?.name != "settings.py") return;
        val leftExpression = node.leftHandSideExpression?.text ?: return
        if (leftExpression  != "DEBUG") return;
        val assignedValue = node.assignedValue ?: return
        if (assignedValue.textMatches("True").not()) return;
        holder.create(node, Checks.DjangoDebugModeCheck)
    }
}