package security.validators;

import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyBoolLiteralExpression;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyImportStatement;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.jetbrains.python.validation.PyAnnotator;
import security.Checks;

import java.util.Collection;

public class FlaskDebugModeAnnotator extends PyAnnotator {
    @Override
    public void visitPyCallExpression(PyCallExpression node) {
        Collection<PyImportStatement> imports = PsiTreeUtil.findChildrenOfType(node.getContainingFile(), PyImportStatement.class);

        for (PyImportStatement statement: imports){
           /// TODO : Check that flask has been imported.
        }

        if (node.getCallee() == null)
            return;
        if (!node.getCallee().getName().equals("run"))
            return;
        if (!((PyReferenceExpression)node.getFirstChild()).asQualifiedName().toString().equals("app.run"))
            return;
        if (node.getKeywordArgument("debug") == null)
            return;
        if (((PyBoolLiteralExpression)node.getKeywordArgument("debug")).getValue() != true)
            return;
        getHolder().createWarningAnnotation(node, String.valueOf(Checks.FlaskDebugModeCheck));
    }
}
