package bandit;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class BanditExternalAnnotator extends ExternalAnnotator {
    private static final Logger LOGGER = Logger.getInstance(BanditExternalAnnotator.class);

    public BanditExternalAnnotator(){
        LOGGER.debug("Init Bandit External Annotator");
    }

    @Nullable
    @Override
    public Object collectInformation(@NotNull PsiFile file) {
        LOGGER.debug("Bandit.collectInformation(1)");
        return super.collectInformation(file);
    }

    @Nullable
    @Override
    public Object doAnnotate(Object collectedInfo) {
        LOGGER.debug("Bandit.doAnnotate");
        return super.doAnnotate(collectedInfo);
    }

    @Nullable
    @Override
    public Object collectInformation(@NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {
        LOGGER.debug("Bandit.collectInformation");
        return super.collectInformation(file, editor, hasErrors);
    }

    @Override
    public void apply(@NotNull PsiFile file, Object annotationResult, @NotNull AnnotationHolder holder) {
        LOGGER.debug("Bandit.apply");
        super.apply(file, annotationResult, holder);
    }
}
