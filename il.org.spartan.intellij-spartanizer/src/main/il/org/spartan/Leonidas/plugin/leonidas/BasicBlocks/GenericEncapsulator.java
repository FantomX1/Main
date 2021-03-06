package il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import il.org.spartan.Leonidas.auxilary_layer.PsiRewrite;
import il.org.spartan.Leonidas.auxilary_layer.Utils;
import il.org.spartan.Leonidas.plugin.leonidas.Matcher;
import il.org.spartan.Leonidas.plugin.leonidas.MatchingResult;

import java.util.*;
import java.util.stream.Collectors;


/**
 * A base class for all basic blocks.
 * @author Oren Afek && Michal Cohen
 * @since 03-05-2017
 */
public abstract class GenericEncapsulator extends Encapsulator implements Cloneable {
    protected String template;
    protected String description = "";
    private List<BiConstraint> constraints = new ArrayList<>();
    private List<ReplacingRule> replacingRules = new ArrayList<>();

    public GenericEncapsulator(PsiElement e, String template) {
        super(e);
        children = Collections.emptyList();
        this.template = template;
    }

    public GenericEncapsulator(Encapsulator n, String template) {
        super(n);
        children = Collections.emptyList();
        this.template = template;
    }

    /**
     * For reflection use DO NOT REMOVE!
     */
    @SuppressWarnings("unused")
    protected GenericEncapsulator() {
    }

    public String getDescription() {
        return description;
    }

    public List<ReplacingRule> getReplacingRules() {
        return replacingRules;
    }

    /**
     * Does the given PsiElement is a stub representing the general form?
     *
     * @param other PSI Element
     * @return true iff other needs to be switched with the current generic component.
     */
    public abstract boolean conforms(PsiElement other);

    /**
     * Extracts the Id No. of the general block from the PsiElement
     * for example:
     * statement(0) -> 0
     *
     * @param e PsiElement
     * @return the id.
     */
    public abstract Integer extractId(PsiElement e);

    /**
     * Prunes the irrelevant concrete PsiElements and replaces with a new Generic Encapsulator
     *
     * @param e the concrete PsiElement.
     *          for example: the methodCallExpression: statement(0).
     * @return The replacer of the syntactic generic element with the GenericEncapsulator
     */
    public Encapsulator prune(Encapsulator e, Map<Integer, List<Matcher.Constraint>> m) {
        assert conforms(e.getInner());
        Encapsulator upperElement = getConcreteParent(e);
        GenericEncapsulator ge = create(upperElement, m);
        if (!isGeneric())
            return upperElement.getParent() == null ? ge : upperElement.generalizeWith(ge);
        ge.putId(ge.extractId(e.getInner()));
		ge.extractAndAssignDescription(e.getInner());
        return upperElement.getParent() == null ? ge : upperElement.generalizeWith(ge);
    }

    /**
     * @param prev an encapsulator
     * @param next the parent of prev
     * @return true iff prev is not the highest element that needs to be pruned.
     */
    protected abstract boolean goUpwards(Encapsulator prev, Encapsulator next);

    /**
     * Adds a description to the element
     *
     * @param e a stub element
     * @return this, for fluent API
     */
    public abstract GenericEncapsulator extractAndAssignDescription(PsiElement e);

    /**
     * Creates another one like me, with concrete PsiElement within, since in the toolbox, only stubs are created.
     *
     * @param e element within.
     * @param m a mapping between an id of generic element to it's list of constraints.
     * @return new <B>Specific</B> GenericEncapsulator
     */
    public abstract GenericEncapsulator create(Encapsulator e, Map<Integer, List<Matcher.Constraint>> m);

    /**
     * Do I generalize a concrete element
     *
     * @param e concrete element
     * @return true iff I generalize with e
     */
    @SuppressWarnings("InfiniteRecursion")
    public MatchingResult generalizes(Encapsulator e, Map<Integer, List<PsiElement>> m) {
        return new MatchingResult(constraints.stream()
                .allMatch(c -> c.accept(e, m)));
    }

    /**
     * @param es the matching elements of this generic basic block of the user.
     * @param m      the mapping between ids of generic elements and their concrete corresponding elements.
     * @return the replaced elements.
     */
    protected List<PsiElement> applyReplacingRules(List<PsiElement> es, Map<Integer, List<PsiElement>> m){
        return es.stream().map(e -> {
            PsiElement temp = e;
            for (ReplacingRule rr : replacingRules)
                temp = rr.replace(temp, m);
            return temp;
        }).collect(Collectors.toList());
    }

    /**
     * @param es the matching elements of this generic basic block of the user.
     * @param m the mapping between ids of generic elements and their concrete corresponding elements.
     * @param r PsiRewrite
     * @return the concrete replacer of this generic basic block.
     */
    public List<PsiElement> replaceByRange(List<PsiElement> es, Map<Integer, List<PsiElement>> m, PsiRewrite r) {
        es = applyReplacingRules(es, m);
        if (parent == null) return es;
        if (es.size() <= 1)
            return Utils.wrapWithList(r.replace(inner, es.get(0)));
        List<PsiElement> l = Lists.reverse(es);
        l.forEach(e -> r.addAfter(inner.getParent(), inner, e));
        r.deleteByRange(inner.getParent(), inner, inner);
        return es;
    }

    /**
     * @param e stub representing generic element.
     * @return the highest generic parent that should be pruned.
     */
    public Encapsulator getConcreteParent(Encapsulator e) {
        if (e.parent == null) return e;
        Encapsulator prev = e, next = e.getParent();
        for (; goUpwards(prev, next); next = next.getParent())
            prev = next;
        return prev;
    }

    /**
     * @param e stub representing generic element.
     * @param m the mapping between ids of generic elements and their concrete corresponding elements.
     * @return the highest generic parent that should be pruned.
     */
    public Encapsulator getConcreteParent(Encapsulator e, Map<Integer, List<Matcher.Constraint>> m) {
        return getConcreteParent(e);
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    /**
     * @param c constraint
     * When building the matcher, adds a constraint to the list of constraints.
     */
    protected void addConstraint(Constraint c) {
        constraints.add((e, m) -> c.accept(e));
    }

    /**
     * @param c constraint
     * When building the matcher, adds a constraint that uses the map to the list of constraints.
     */
    protected void addConstraint(BiConstraint c) {
        constraints.add(c);
    }

    /**
     * @param r replacing rule.
     * When building the replacer, adds a replacing rule to the basic block.
     */
    protected void addReplacingRule(ReplacingRule r) {
        replacingRules.add(r);
    }

    /**
     * @return the hidden generic elements that are part of the properties of some basic blocks.
     */
    public Map<Integer, GenericEncapsulator> getGenericElements() {
        return new HashMap<>();
    }

    public List<BiConstraint> getConstraints() {
        return constraints;
    }

    public void copyTo(GenericEncapsulator dst) {
        dst.replacingRules.addAll(replacingRules);
    }

    public interface Constraint {
        boolean accept(Encapsulator e);
    }

    public interface BiConstraint {
        boolean accept(Encapsulator e, Map<Integer, List<PsiElement>> m);
    }

    protected interface ReplacingRule {
        PsiElement replace(PsiElement encapsulator, Map<Integer, List<PsiElement>> m);
    }
}
