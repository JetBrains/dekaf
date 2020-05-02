package org.jetbrains.dekaf.inter.utils;

import java.util.Iterator;



public class EmptyIterator<E> implements Iterator<E> {

    @SuppressWarnings("rawtypes")
    private static final EmptyIterator instance = new EmptyIterator();

    public static<E> Iterator<E> one() {
        //noinspection unchecked
        return instance;
    }
    
    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public E next() {
        return null;
    }

}
