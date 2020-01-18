package security.validators

import com.jetbrains.python.psi.PyAssignmentStatement
import com.jetbrains.python.psi.PyListLiteralExpression
import com.jetbrains.python.psi.PyStringLiteralExpression
import com.jetbrains.python.validation.PyAnnotator
import security.Checks
import security.create
import security.fixes.DjangoAddMiddlewareFixer

class DjangoMiddlewareValidator: PyAnnotator() {
    override fun visitPyAssignmentStatement(node: PyAssignmentStatement?) {
        if (node?.containingFile?.name != "settings.py") return;
        val leftExpression = node.leftHandSideExpression?.text ?: return
        if (leftExpression != "MIDDLEWARE") return;
        val assignedValue = node.assignedValue ?: return
        if (assignedValue !is PyListLiteralExpression) return
        val middleware = assignedValue.elements.filter { el -> el is PyStringLiteralExpression }.map{ (it as PyStringLiteralExpression).stringValue }

        if (middleware.contains("django.middleware.csrf.CsrfViewMiddleware").not()) {
            holder.create(node, Checks.DjangoCsrfMiddlewareCheck)
                .registerFix(DjangoAddMiddlewareFixer("django.middleware.csrf.CsrfViewMiddleware"))
        }
        if (middleware.contains("django.middleware.clickjacking.XFrameOptionsMiddleware").not()) {
            holder.create(node, Checks.DjangoClickjackMiddlewareCheck)
                    .registerFix(DjangoAddMiddlewareFixer("django.middleware.clickjacking.XFrameOptionsMiddleware"))
        }
    }
}