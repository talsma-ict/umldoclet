package nl.talsmasoftware.umldoclet.testing.generics;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sjoerd on 02-03-16.
 */
public class ConstantList<T, X extends CharSequence> extends AbstractList<T> {

    private final List<T> delegate;

    public ConstantList(T... values) {
        if (values == null) {
            delegate = Collections.emptyList();
        } else {
            delegate = Collections.unmodifiableList(Arrays.asList(values));
        }
    }
    
    public X getX() {
        return null;
    }

    @Override
    public T get(int index) {
        return delegate.get(index);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
