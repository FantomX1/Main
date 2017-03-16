package il.org.spartan.ispartanizer.plugin.leonidas;

import com.intellij.psi.PsiMethodCallExpression;
import il.org.spartan.ispartanizer.PsiTypeHelper;
import il.org.spartan.ispartanizer.plugin.leonidas.GenericPsiElementStub.StubName;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author Oren Afek
 * @since 17/01/17
 */
public class StubNameTest extends PsiTypeHelper {

    private final Stream<Pair<StubName, PsiMethodCallExpression>> stubs;

    public StubNameTest() {
        stubs = Arrays.stream(GenericPsiElementStub.StubName.values())
                .map(stub -> new Pair<>(stub, stub.stubName()))
                .map(p -> new Pair<>(p.first, createTestMethodCallExpression(p.second)));

    }

    public void testValueOfLegal() {
        stubs.forEach(p -> assertEquals(p.first, StubName.valueOfMethodCall(p.second)));
    }

}