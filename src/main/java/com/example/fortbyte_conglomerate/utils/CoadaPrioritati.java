package com.example.fortbyte_conglomerate.utils;

import java.util.*;

public class CoadaPrioritati<T extends Comparable<T>> implements Iterable<T> {
    // lista simplu-înlănțuită care păstrează ordinea elementelor
    private final Collection<T> elements;

    public CoadaPrioritati() {
        // se inițializează lista inlantuita cu elemente de tip T
        elements = new LinkedList<>();
    }

    public void add(T element) {
        // se obține iteratorul colecției
        Iterator<T> iterator = elements.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (element.compareTo(next) <= 0) {
                // se adaugă elementul la poziția corectă în colecție
                elements.add(element);
                return;
            }
        }
        // dacă elementul este mai mare decât toate elementele din colecție, se adaugă la sfârșitul acesteia
        elements.add(element);
    }


    public T remove() {
        // se obține iteratorul colecției
        Iterator<T> iterator = elements.iterator();
        T element = iterator.next();
        // se elimină primul element din colecție
        iterator.remove();
        return element;
    }


    public T peek() {
        // se obține iteratorul colecției
        Iterator<T> iterator = elements.iterator();
        return iterator.next();
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public void addAll(PriorityQueue<T> queue) {
        while (!queue.isEmpty()) {
            add(queue.remove());
        }
    }

    public void addAll(Collection<T> collection) {
        for (T element : collection) {
            add(element);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new QueueIterator();
    }

    private class QueueIterator implements Iterator<T> {
        private final Iterator<T> iterator;

        public QueueIterator() {
            iterator = elements.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            return iterator.next();
        }
    }

}
