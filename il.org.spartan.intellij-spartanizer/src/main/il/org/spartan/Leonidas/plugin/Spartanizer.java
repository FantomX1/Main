package il.org.spartan.Leonidas.plugin;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import il.org.spartan.Leonidas.auxilary_layer.az;
import il.org.spartan.Leonidas.auxilary_layer.iz;
import il.org.spartan.Leonidas.plugin.leonidas.SpartaDefeat;
import il.org.spartan.Leonidas.plugin.tipping.Tipper;

import static il.org.spartan.Leonidas.auxilary_layer.Utils.isAnnotationPresent;

/**
 * @author Oren Afek, Roey Maor
 * @since 09-12-2016
 */
public enum Spartanizer {
    ;

    static boolean canTip(PsiElement ¢) {
        return Toolbox.getInstance().canTip(¢);
    }

    static boolean shouldSpartanize(PsiElement e) {
        PsiFile f = iz.psiFile(e) ? az.psiFile(e) : e.getContainingFile();
        return !"SpartanizerUtils.java".equals(f.getName()) && !isAnnotationPresent(f, SpartaDefeat.class);
    }

    static void spartanizeRecursively(PsiElement e) {
        if (!shouldSpartanize(e))
            return;
        Toolbox toolbox = Toolbox.getInstance();
        e.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement ¢) {
                super.visitElement(¢);
                toolbox.executeAllTippers(¢);
            }
        });
    }

    static void spartanizeElement(PsiElement ¢) {
        if (shouldSpartanize(¢))
            Toolbox.getInstance().executeAllTippers(¢);
    }

    static void spartanizeElement(PsiElement e, Tipper t) {
        if (shouldSpartanize(e))
            Toolbox.getInstance().executeTipper(e, t);
    }

    public static void spartanizeFileOnePass(PsiFile f) {
        if (!shouldSpartanize(f))
            return;
        Toolbox toolbox = Toolbox.getInstance();
        f.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement ¢) {
                super.visitElement(¢);
                toolbox.executeAllTippers(¢);
            }
        });
    }

    public static void spartanizeFileRecursively(PsiFile f) {
        Toolbox toolbox = Toolbox.getInstance();
        toolbox.replaced = true;
        while (toolbox.replaced) {
            toolbox.replaced = false;
            spartanizeFileOnePass(f);
        }
        spartanizeFileOnePass(f);
    }

    public static void spartanizeFileOnePassNoNanos(PsiFile f) {
        if (!shouldSpartanize(f))
            return;
        Toolbox toolbox = Toolbox.getInstance();
        f.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement ¢) {
                super.visitElement(¢);
                toolbox.executeAllTippersNoNanos(¢);
            }
        });
    }

    // TODO this is a bad name. its not recursive.
    public static void spartanizeFileRecursivelyNoNanos(PsiFile f) {
        Toolbox toolbox = Toolbox.getInstance();
        toolbox.replaced = true;
        while (toolbox.replaced) {
            toolbox.replaced = false;
            spartanizeFileOnePassNoNanos(f);
        }
    }


    public static void spartanizeElementWithTipper(PsiElement e, String tipperName) {
        for (Tipper t : Toolbox.getInstance().getAllTippers())
			System.out.println(t.name());
    }
}
