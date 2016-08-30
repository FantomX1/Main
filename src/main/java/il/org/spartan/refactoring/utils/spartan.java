package il.org.spartan.refactoring.utils;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

public interface spartan {
  static String repeat(final int i, final char c) {
    return new String(new char[i]).replace('\0', c);
  }

  static String shorten(final ArrayType t) {
    return shorten(t.getElementType()) + repeat(t.getDimensions(), 's');
  }

  static String shorten(@SuppressWarnings("unused") final IntersectionType __) {
    return null;
  }

  static String shorten(final List<? extends org.eclipse.jdt.core.dom.Type> ts) {
    return shorten(lisp.onlyOne(ts));
  }

  static String shorten(final Name n) {
    return n instanceof SimpleName ? shorten(n.toString()) //
        : n instanceof QualifiedName ? shorten(((QualifiedName) n).getName()) //
            : null;
  }

  static String shorten(final NameQualifiedType t) {
    return shorten(t.getName());
  }

  static String shorten(final ParameterizedType t) {
    switch (t.getType().toString()) {
      case "Collection":
      case "Iterable":
      case "List":
      case "Queue":
      case "Set":
        final String $ = shorten(expose.typeArguments(t));
        if ($ == null)
          return null;
        return $ + "s";
      default:
        return null;
    }
  }

  static String shorten(final PrimitiveType t) {
    return t.getPrimitiveTypeCode().toString().substring(0, 1);
  }

  static String shorten(final QualifiedType t) {
    return shorten(t.getName());
  }

  static String shorten(final SimpleType t) {
    return shorten(t.getName());
  }

  static String shorten(final String s) {
    return new JavaTypeNameParser(s).shortName();
  }

  static String shorten(final org.eclipse.jdt.core.dom.Type t) {
    return t instanceof NameQualifiedType ? shorten((NameQualifiedType) t)
        : t instanceof PrimitiveType ? shorten((PrimitiveType) t)
            : t instanceof QualifiedType ? shorten((QualifiedType) t)
                : t instanceof SimpleType ? shorten((SimpleType) t)
                    : t instanceof WildcardType ? shortName((WildcardType) t)
                        : t instanceof ArrayType ? shorten((ArrayType) t)
                            : t instanceof IntersectionType ? shorten((IntersectionType) t) //
                                : t instanceof ParameterizedType ? shorten((ParameterizedType) t)//
                                    : t instanceof UnionType ? shortName((UnionType) t) : null;
  }

  static String shortName(@SuppressWarnings("unused") final UnionType __) {
    return null;
  }

  static String shortName(final WildcardType t) {
    return shorten(t.getBound());
  }
}
