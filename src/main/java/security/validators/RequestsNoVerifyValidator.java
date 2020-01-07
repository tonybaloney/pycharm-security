package security.validators;

import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyBoolLiteralExpression;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyImportStatement;
import com.jetbrains.python.psi.resolve.PyResolveContext;
import com.jetbrains.python.validation.PyAnnotator;
import security.Checks;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class RequestsNoVerifyValidator extends PyAnnotator {
    @Override
    public void visitPyCallExpression(PyCallExpression node) {
        Collection<PyImportStatement> imports = PsiTreeUtil.findChildrenOfType(node.getContainingFile(), PyImportStatement.class);
        PyResolveContext resolveContext = PyResolveContext.defaultContext();

        if (node.getCallee() == null)
            return;

        List<PyCallExpression.PyMarkedCallee> markedCallees = node.multiResolveCallee(resolveContext);

        for (PyImportStatement statement: imports){
           /// TODO : Check that requests has been imported.
        }

        if (node.getCallee() == null)
            return;
        String[] requestsMethodNames = {"get", "post", "options", "delete", "put", "patch", "head"};
        if (!Arrays.asList(requestsMethodNames).contains(node.getCallee().getName()))
            return;
        if (!Objects.requireNonNull(markedCallees.get(0).getElement()).getQualifiedName().startsWith("requests."))
            return;
        if (node.getKeywordArgument("verify") == null)
            return;
        if (((PyBoolLiteralExpression)node.getKeywordArgument("verify")).getValue() == true)
            return;
        getHolder().createWarningAnnotation(node, String.valueOf(Checks.RequestsNoVerifyCheck));
    }
}
