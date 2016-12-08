package auxilary_layer;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiClass;

/**
 * Created by maorroey on 12/3/2016.
 */
public class PsiRewrite {
    public PsiElementFactory factory;
    public PsiClass psiClass;

    public void replace(PsiElement element1, PsiElement element2){
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                element1.replace(element2);
            }

        }.execute();
    }
}
