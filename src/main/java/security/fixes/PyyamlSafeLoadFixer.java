package security.fixes;


import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.python.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PyyamlSafeLoadFixer implements LocalQuickFix, IntentionAction, HighPriorityAction {

    @NotNull
    @Override
    public String getText() {
        return getName();
    }

    @Override
    @NotNull
    public String getFamilyName() {
        return "Use safe_load()";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        @Nullable PyCallExpression call = PsiTreeUtil.getParentOfType(file.findElementAt(editor.getCaretModel().getOffset()), PyCallExpression.class);
        PyElementGenerator elementGenerator = PyElementGenerator.getInstance(project);
        if (call != null){
            PyCallExpression new_el = (PyCallExpression)elementGenerator.createExpressionFromText(LanguageLevel.getDefault(), call.getText().replace("load", "safe_load"));
            ApplicationManager.getApplication().runWriteAction(() -> {
                call.replace(new_el);
            });
        }
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @Override
    public void applyFix(@NotNull final Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement element = descriptor.getPsiElement();
        if (element == null) {  // stale PSI
            return;
        }
    }
}