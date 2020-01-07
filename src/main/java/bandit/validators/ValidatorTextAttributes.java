package bandit.validators;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.psi.LanguageLevel;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.FUNCTION_CALL;

public class ValidatorTextAttributes extends SyntaxHighlighterBase {
    private final Map<IElementType, TextAttributesKey> keys;
    private final LanguageLevel myLanguageLevel;

    static final TextAttributesKey PYYAML_LOAD_UNSAFE = TextAttributesKey.createTextAttributesKey("SECURITY.PYYAML_LOAD_UNSAFE", FUNCTION_CALL);

    public ValidatorTextAttributes(LanguageLevel myLanguageLevel) {
        this.keys = new HashMap<>();
        this.myLanguageLevel = myLanguageLevel;
    }

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return null;
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(keys.get(tokenType));
    }
}
