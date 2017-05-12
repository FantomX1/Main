package il.org.sparan.classfiles;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.util.*;

import org.eclipse.jdt.annotation.*;
import org.junit.*;

import fluent.ly.*;
import il.org.spartan.*;
import il.org.spartan.classfiles.*;

public class JRETest {
  @Test public final void testAsList() {
     final List<File> l = JRE.asList();
    assert l != null;
     final ClassRepository cpi = new ClassRepository(l);
    assertEquals(l.size(), cpi.getRoots().length);
    assert cpi.getClasses().contains("java.lang.Object");
    assert !cpi.getClasses().contains(this.getClass().getName());
  }

  @Test public final void testFromClass() {
     final List<File> l = JRE.fromClass(this.getClass());
    assert l != null;
    azzert.assertPositive(l.size());
  }
}
