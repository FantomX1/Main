package il.org.spartan.spartanizer.dispatch;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.tippers.*;
import il.org.spartan.spartanizer.tipping.*;

/** Singleton containing all {@link Tipper}s which are active, allowing
 * selecting and applying the most appropriate such object for a given
 * {@link ASTNode}.
 * @author Yossi Gil
 * @since 2015-08-22 */
public class Toolbox {
  /** The default instance of this class */
  static Toolbox instance;

  public static Toolbox defaultInstance() {
    if (instance == null)
      refresh();
    return instance;
  }

  /** Make a {@link Toolbox} for a specific kind of wrings
   * @param clazz JD
   * @param w JS
   * @return a new instance containing only the wrings passed as parameter */
  @SafeVarargs public static <N extends ASTNode> Toolbox make(final Class<N> clazz, final Tipper<N>... ns) {
    return new Maker().add(clazz, ns);
  }

  /** Initialize this class' internal instance object */
  public static void refresh() {
    if (instance == null)
      instance = new Maker()//
          .add(EnhancedForStatement.class, new EnhancedForParameterRenameToCent())//
          .add(VariableDeclarationExpression.class, new ForRenameInitializerToCent()) //
          .add(ThrowStatement.class, new ThrowNotLastInBlock()) //
          .add(ClassInstanceCreation.class, new ClassInstanceCreationValueTypes()) //
          .add(SuperConstructorInvocation.class, new SuperConstructorInvocationRemover()) //
          .add(ReturnStatement.class, new ReturnLastInMethod()) //
          // Disabled to protect against infinite loop
          // .add(AnnotationTypeMemberDeclaration.class, new
          // BodyDeclarationModifiersSort.ofAnnotationTypeMember()) //
          // .add(AnnotationTypeDeclaration.class, new
          // BodyDeclarationModifiersSort.ofAnnotation()) //
          .add(ForStatement.class, //
              new BlockBreakToReturnInfiniteFor(), //
              new ReturnToBreakFiniteFor(), //
              null)//
          .add(WhileStatement.class, //
              new BlockBreakToReturnInfiniteWhile(), //
              new ReturnToBreakFiniteWhile(), //
              new ConvertWhileWithLastStatementUpdateToFor(), //
              new RemoveRedundentWhile(), //
              null) //
          .add(Assignment.class, //
              new AssignmentAndAssignment(), //
              new AssignmentAndReturn(), //
              new AssignmentToFromInfixIncludingTo(), //
              new AssignmentToPostfixIncrement(), //
              null) //
          .add(Block.class, //
              new BlockSimplify(), //
              new BlockSingleton(), //
              null) //
          .add(PostfixExpression.class, //
              new PostfixToPrefix(), //
              null) //
          .add(InfixExpression.class, //
              /* The following line was intentionally commented: Matteo, I
               * believe this generates many bugs --yg Bug Fixed, but not
               * integrated, as per request. Waiting for the enhancement (Term,
               * Factor, etc.) -- mo */
              // new InfixMultiplicationDistributive(), //
              new InfixMultiplicationEvaluate(), //
              new InfixDivisionEvaluate(), //
              new InfixRemainderEvaluate(), //
              new InfixComparisonSizeToZero(), //
              new InfixSubtractionZero(), //
              new InfixAdditionSubtractionExpand(), //
              new InfixEmptyStringAdditionToString(), //
              new InfixConcatenationEmptyStringLeft(), //
              new InfixFactorNegatives(), //
              new InfixAdditionEvaluate(), //
              // new ConcatStrings(), //removed for now so it won't break tests,
              // see issue #120
              new InfixSubractionEvaluate(), //
              new InfixTermsZero(), //
              new InfixPlusRemoveParenthesis(), //
              new InfixAdditionSort(), //
              new InfixComparisonBooleanLiteral(), //
              new InfixConditionalAndTrue(), //
              new InfixConditionalOrFalse(), //
              new InfixComparisonSpecific(), //
              new InfixMultiplicationByOne(), //
              new InfixMultiplicationByZero(), //
              new InfixMultiplicationSort(), //
              new InfixPseudoAdditionSort(), //
              new InfixSubtractionSort(), //
              new InfixDivisonSortRest(), //
              new InfixConditionalCommon(), //
              null)
          .add(MethodDeclaration.class, //
              new MethodDeclarationRenameReturnToDollar(), //
              new MethodDeclarationModifiersRedundant(), //
              // Disabled to protect against infinite loop
              new BodyDeclarationModifiersSort.ofMethod(), //
              // new BodyDeclarationAnnotationsSort.ofMethod() , //
              new BodyDeclarationModifiersSort.ofMethod(), //
              new MethodDeclarationRenameSingleParameterToCent(), //
              null)
          .add(MethodInvocation.class, //
              new MethodInvocationEqualsWithLiteralString(), //
              new MethodInvocationValueOfBooleanConstant(), //
              new MethodInvocationToStringToEmptyStringAddition(), //
              null)
          .add(SingleVariableDeclaration.class, //
              new SingleVariableDeclarationAbbreviation(), //
              new SingelVariableDeclarationUnderscoreDoubled(), //
              new VariableDeclarationRenameUnderscoreToDoubleUnderscore<>(), //
              // new
              // SingleVariableDeclarationEnhancedForRenameParameterToCent(), //
              null)//
          .add(VariableDeclarationFragment.class, //
              new DeclarationRedundantInitializer(), //
              new DeclarationAssignment(), //
              new DeclarationInitialiazerAssignment(), //
              new DeclarationInitialiazelUpdateAssignment(), //
              new DeclarationInitializerIfAssignment(), //
              new DeclarationInitializerIfUpdateAssignment(), //
              new DeclarationInitializerReturnVariable(), //
              new DeclarationInitializerReturnExpression(), //
              new DeclarationInitializerReturnAssignment(), //
              new DeclarationInitializerReturnUpdateAssignment(), //
              new DeclarationInitializerStatementTerminatingScope(), //
              new VariableDeclarationRenameUnderscoreToDoubleUnderscore<>(), null) //
          .add(IfStatement.class, //
              new IfTrueOrFalse(), //
              new RemoveRedundantIf(), //
              new IfLastInMethodThenEndingWithEmptyReturn(), //
              new IfLastInMethodElseEndingWithEmptyReturn(), //
              new IfLastInMethod(), //
              new IfReturnFooElseReturnBar(), //
              new IfReturnNoElseReturn(), //
              new IfAssignToFooElseAssignToFoo(), //
              new IfThenFooBarElseFooBaz(), //
              new IfBarFooElseBazFoo(), //
              new IfThrowFooElseThrowBar(), //
              new IfThrowNoElseThrow(), //
              new IfExpressionStatementElseSimilarExpressionStatement(), //
              new IfThenOrElseIsCommandsFollowedBySequencer(), //
              new IfFooSequencerIfFooSameSequencer(), //
              new IfCommandsSequencerNoElseSingletonSequencer(), //
              new IfPenultimateInMethodFollowedBySingleStatement(), //
              new IfThenIfThenNoElseNoElse(), //
              new IfEmptyThenEmptyElse(), //
              new IfDegenerateElse(), //
              new IfEmptyThen(), //
              new IfShortestFirst(), //
//              new IfNullReturnNull(), //
              null)//
          .add(PrefixExpression.class, //
              new PrefixIncrementDecrementReturn(), //
              new PrefixNotPushdown(), //
              new PrefixPlusRemove(), //
              null) //
          .add(ConditionalExpression.class, //
              new TernaryBooleanLiteral(), //
              new TernaryCollapse(), //
              new TernaryEliminate(), //
              new TernaryShortestFirst(), //
              new TernaryPushdown(), //
              new TernaryPushdownStrings(), //
//              new TernaryNullCoallescing(), //
              null) //
          .add(TypeDeclaration.class, //
              // new delmeTypeModifierCleanInterface(), //
              new TypeRedundantModifiers(), //
              // Disabled to protect against infinite loop
              new BodyDeclarationModifiersSort.ofType(), //
              // new BodyDeclarationAnnotationsSort.ofType(), //
              null) //
          .add(EnumDeclaration.class, //
              new EnumRedundantModifiers(), new BodyDeclarationModifiersSort.ofEnum(), //
              // new BodyDeclarationAnnotationsSort.ofEnum(), //
              new EnumRedundantModifiers(), new BodyDeclarationModifiersSort.ofEnum(), //
              // new EnumDeclarationModifierCleanEnum(), //
              null) //
          .add(FieldDeclaration.class, //
              new FieldRedundantModifiers(), //
              new BodyDeclarationModifiersSort.ofField(), //
              // new BodyDeclarationAnnotationsSort.ofField(), //
              null) //
          .add(CastExpression.class, //
              new CastToDouble2Multiply1(), //
              new CastToLong2Multiply1L(), //
              null) //
          .add(EnumConstantDeclaration.class, //
              new EnumConstantRedundantModifiers(), //
              new BodyDeclarationModifiersSort.ofEnumConstant(), //
              // new BodyDeclarationAnnotationsSort.ofEnumConstant(), //
              null) //
          .add(NormalAnnotation.class, //
              new AnnotationDiscardValueName(), //
              new AnnotationRemoveEmptyParentheses(), //
              null) //
          // TODO: Yossi, No, as I understood, initializers in java can have
          // annotations and modifiers,
          // just like every declaration. Even private class field which is
          // const
          // assigned at the class body
          // is called initializer, and there I know could be some modifiers.
          // TODO: Alex, I could not place an annotation on an initializer.
          // Suppose we can, then still, that this could not have been tested,
          // since the dispatcher does not
          // know about Initializers. Add initializers to DispatchingVisitor if
          // you can provide a test case
          .add(Initializer.class, new BodyDeclarationModifiersSort.ofInitializer(), //
              // new BodyDeclarationAnnotationsSort.ofInitializer(), //
              null) //
          .seal();
  }

  private static <N extends ASTNode> Tipper<N> find(final N n, final List<Tipper<N>> ns) {
    for (final Tipper<N> $ : ns)
      if ($.canTip(n))
        return $;
    return null;
  }

  private final Map<Class<? extends ASTNode>, List<Object>> inner = new HashMap<>();

  /** Find the first {@link Tipper} appropriate for an {@link ASTNode}
   * @param n JD
   * @return first {@link Tipper} for which the parameter is within scope, or
   *         <code><b>null</b></code> if no such {@link Tipper} is found. @ */
  public <N extends ASTNode> Tipper<N> find(final N ¢) {
    return find(¢, get(¢));
  }

  public <N extends ASTNode> Tipper<N> findTipper(final N n, @SuppressWarnings("unchecked") final Tipper<N>... ns) {
    for (final Tipper<N> $ : get(n))
      for (final Tipper<?> ¢ : ns)
        if (¢.getClass().equals($.getClass())) {
          if ($.canTip(n))
            return $;
          break;
        }
    return null;
  }

  @SuppressWarnings("unchecked") <N extends ASTNode> List<Tipper<N>> get(final Class<? extends ASTNode> ¢) {
    if (!inner.containsKey(¢))
      inner.put(¢, new ArrayList<>());
    return (List<Tipper<N>>) (List<?>) inner.get(¢);
  }

  <N extends ASTNode> List<Tipper<N>> get(final N ¢) {
    return get(¢.getClass());
  }

  /** A builder for the enclosing class.
   * @author Yossi Gil
   * @since 2015-08-22 */
  public static class Maker extends Toolbox {
    /** Associate a bunch of{@link Tipper} with a given sub-class of
     * {@link ASTNode}.
     * @param n JD
     * @param ns JD
     * @return <code><b>this</b></code>, for easy chaining. */
    @SafeVarargs public final <N extends ASTNode> Maker add(final Class<N> n, final Tipper<N>... ns) {
      final List<Tipper<N>> l = get(n);
      for (final Tipper<N> ¢ : ns) {
        if (¢ == null)
          break;
        assert ¢.wringGroup() != null : "Did you forget to use a specific kind for " + ¢.getClass().getSimpleName();
        if (!¢.wringGroup().isEnabled())
          continue;
        l.add(¢);
      }
      return this;
    }

    /** Terminate a fluent API chain.
     * @return newly created object */
    public Toolbox seal() {
      return this;
    }
  }
}