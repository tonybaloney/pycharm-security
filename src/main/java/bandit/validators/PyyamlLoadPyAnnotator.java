package bandit.validators;

import bandit.fixes.PyyamlSafeLoadFixer;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.Annotation;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.resolve.PyResolveContext;
import com.jetbrains.python.validation.PyAnnotator;

import java.util.List;
import java.util.Objects;


public class PyyamlLoadPyAnnotator extends PyAnnotator {
    @Override
    public void visitPyCallExpression(PyCallExpression node) {
        PyResolveContext resolveContext = PyResolveContext.defaultContext();

        if (node.getCallee() != null){
            List<PyCallExpression.PyMarkedCallee> markedCallees = node.multiResolveCallee(resolveContext);
            if (node.getCallee().getName().equals("load") && markedCallees != null){
                if (Objects.requireNonNull(markedCallees.get(0).getElement()).getQualifiedName().equals("yaml.load")) {
                    Annotation annotation = getHolder().createWarningAnnotation(node, "Use of unsafe yaml load. Allows instantiation of arbitrary objects. Consider yaml.safe_load().");
                    annotation.registerFix((IntentionAction)new PyyamlSafeLoadFixer(), node.getTextRange());
                }
            }
        }
    }
}