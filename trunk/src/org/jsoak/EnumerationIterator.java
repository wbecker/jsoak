package org.jsoak;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIterator<E> implements Iterable<E>
{
  private final Enumeration<E> e;

  public EnumerationIterator(final Enumeration<E> e)
  {
    this.e = e;
  }

  @Override
  public Iterator iterator()
  {
    return new EnumerationIter(e);
  }

  class EnumerationIter<E> implements Iterator<E>
  {
    private final Enumeration<E> en;

    public EnumerationIter(final Enumeration<E> en)
    {
      this.en = en;
    }

    @Override
    public boolean hasNext()
    {
      return en.hasMoreElements();
    }

    @Override
    public E next()
    {
      return en.nextElement();
    }

    @Override
    public void remove()
    {
      throw new RuntimeException("remove() is not supported");
    }
  }
}
