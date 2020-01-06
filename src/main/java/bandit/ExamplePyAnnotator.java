package bandit;

import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyUtil;
import com.jetbrains.python.psi.impl.PyCallExpressionHelper;
import com.jetbrains.python.validation.PyAnnotator;
import org.jetbrains.annotations.Nullable;


public class ExamplePyAnnotator extends PyAnnotator {
    @Override
    public void visitPyCallExpression(PyCallExpression node) {
        if (node.getCallee() != null){
            @Nullable PyClass x = PyCallExpressionHelper.resolveCalleeClass(node);
            /// TODO : Inspect module, import naming etc. This just matches any call to load() :-/
            if (node.getCallee().getName().equals("load")){
                this.markError(node.getOriginalElement(), "Use of unsafe yaml load. Allows instantiation of arbitrary objects. Consider yaml.safe_load().");
            }
        }
    }
}