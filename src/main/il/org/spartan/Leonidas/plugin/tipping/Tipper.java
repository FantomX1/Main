package il.org.spartan.Leonidas.plugin.tipping;

import com.intellij.psi.PsiElement;

/**
 * @author Oren Afek
 * @author Michal Cohen
 * @since 01-12-2016
 */
public interface Tipper<T extends PsiElement> {

    boolean canTip(PsiElement e);

    default String description() {
        return getClass().getSimpleName();
    }

    String description(final T t);

    Tip tip(final T node);

    Class<? extends T> getPsiClass();

}
