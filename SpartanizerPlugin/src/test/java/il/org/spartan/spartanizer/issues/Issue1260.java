package il.org.spartan.spartanizer.issues;

import java.util.*;
import java.util.stream.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.*;

import il.org.spartan.*;
import il.org.spartan.utils.*;

/** Tests for reducers
 * @author oran1248
 * @since 2017-04-20 */
@SuppressWarnings("static-method")
public class Issue1260 {
  @Test public void list() {
    ListMergingReducer<Integer> r = new ListMergingReducer<>();
    azzert.that(IntStream.range(1, 31).boxed().collect(Collectors.toList()), is(r.reduce(IntStream.range(1, 11).boxed().collect(Collectors.toList()),
        IntStream.range(11, 21).boxed().collect(Collectors.toList()), IntStream.range(21, 31).boxed().collect(Collectors.toList()))));
    azzert.that(new LinkedList<Integer>(), is(r.reduce()));
    List<Integer> l = new LinkedList<>();
    azzert.that(l, is(r.reduce(l)));
  }

  @Test @SuppressWarnings("boxing") public void integer() {
    IntegerAdditionReducer r = new IntegerAdditionReducer();
    azzert.that(Box.box(0), is(r.reduce()));
    azzert.that(Box.box(1), is(r.reduce(1)));
    azzert.that(Box.box(3), is(r.reduce(1, 2)));
    azzert.that(Box.box(6), is(r.reduce(1, 2, 3)));
  }

  @Test @SuppressWarnings("boxing") public void firstNotNull() {
    FirstNotNullReducer<Integer> r = new FirstNotNullReducer<>();
    azzert.that(null, is(r.reduce()));
    azzert.that(Box.box(1), is(r.reduce(1, null, 2, null)));
    azzert.that(Box.box(2), is(r.reduce(null, null, 2, 3)));
  }

  @Test @SuppressWarnings("boxing") public void booleanOr() {
    BooleanOrReducer r = new BooleanOrReducer();
    azzert.that(Box.box(false), is(r.reduce()));
    azzert.that(Box.box(false), is(r.reduce(null, null)));
    azzert.that(Box.box(true), is(r.reduce(null, false, true, false)));
  }
}
