package il.org.spartan.ispartanizer.auxilary_layer;

import il.org.spartan.ispartanizer.PsiTypeHelper;

public class WrapperTest extends PsiTypeHelper {

    public void testGetAndSet() {
        Wrapper<Integer> wrapper = new Wrapper<>();
        assertNull(wrapper.get());
        Integer x = 2;
        wrapper.set(x);
        assertEquals(x, wrapper.get());
    }

    public void testCtorWithInit() {
        Integer x = 2, y = 5;
        Wrapper<Integer> wrapper = new Wrapper<>(x);
        assertEquals(x, wrapper.get());
        wrapper.set(y);
        assertNotSame(x, wrapper.get());
        assertEquals(y, wrapper.get());
    }

    public void testToString() {
        Wrapper<Integer> wrapper = new Wrapper<>();
        assertEquals("null", wrapper.toString());
        Integer x = 2;
        wrapper.set(x);
        assertEquals(x.toString(), wrapper.toString());
        Wrapper<String> wrapper2 = new Wrapper<>("string");
        assertEquals("string", wrapper2.toString());
    }

    public void testHashCode() {
        Wrapper<Integer> wrapper = new Wrapper<>();
        assertEquals(0, wrapper.hashCode());
        Integer x = 2;
        wrapper.set(2);
        assertEquals(x.hashCode(), wrapper.hashCode());
        wrapper.set(x);
        assertEquals(x.hashCode(), wrapper.hashCode());
        assertEquals(new Integer(2).hashCode(), wrapper.hashCode());
    }

    public void testEquals() {
        Wrapper<Integer> wrapper = new Wrapper<>();
        assertFalse(wrapper.equals(null));
        assertTrue(wrapper.equals(wrapper));
        assertTrue(wrapper.equals(new Wrapper<Integer>()));
    }
}