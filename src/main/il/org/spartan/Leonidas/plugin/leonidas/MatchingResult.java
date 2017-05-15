package il.org.spartan.Leonidas.plugin.leonidas;

import com.intellij.psi.PsiElement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by  on 5/15/2017.
 */
public class MatchingResult {
    Map<Integer, List<PsiElement>> m = new HashMap<>();
    boolean b = false;

    public MatchingResult() {
    }

    public MatchingResult(boolean defaultRes) {
        b = defaultRes;
    }

    public boolean matches() {
        return b;
    }

    public boolean notMatches() {
        return !b;
    }

    public MatchingResult setMatches() {
        b = true;
        return this;
    }

    public MatchingResult setNotMatches() {
        b = false;
        return this;
    }

    public MatchingResult get(Integer i) {
        m.get(i);
        return this;
    }

    public MatchingResult put(Integer i, PsiElement e) {
        m.putIfAbsent(i, new LinkedList<>());
        m.get(i).add(e);
        return this;
    }

    public MatchingResult combineWith(MatchingResult mr) {
        if (!mr.b) {
            b = false;
            return this;
        }
        mr.m.forEach((k, v) -> m.put(k, v));
        return this;
    }

    public Map<Integer, List<PsiElement>> getMap() {
        return m;
    }
}
