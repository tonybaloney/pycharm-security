package bandit.validators;

import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.resolve.PyResolveContext;
import com.jetbrains.python.validation.PyAnnotator;

import java.util.List;


public class PyyamlLoadPyAnnotator extends PyAnnotator {
    @Override
    public void visitPyCallExpression(PyCallExpression node) {
        PyResolveContext resolveContext = PyResolveContext.defaultContext();

        if (node.getCallee() != null){
            List<PyCallExpression.PyMarkedCallee> markedCallees = node.multiResolveCallee(resolveContext);
            if (node.getCallee().getName().equals("load") && markedCallees != null){
                if (markedCallees.get(0).getElement().getQualifiedName().equals("yaml.load"))
                    this.markError(node.getOriginalElement(), "Use of unsafe yaml load. Allows instantiation of arbitrary objects. Consider yaml.safe_load().");
            }
        }
    }
}