/*
 * Copyright (C) 2013 EMBL - European Bioinformatics Institute
 *
 * This file is part of MassCascade.
 *
 * MassCascade is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MassCascade is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MassCascade. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *   Stephan Beisken - initial API and implementation
 */

package uk.ac.ebi.masscascade.tables.lazytable.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class SimpleLazyList<E> implements LazyList<E> {

    private final static Object MISSING_DATA_ENTRY = new Object();

    private final int pageSize;
    private final int size;
    private final LazyListService<E> lazyListPeer;
    private final List<Object> listCache;
    private final List<OnLoadListener> listeners;
    private final List<IndexInterval> pendingRequests;
    private final PendingRequestHandler pendingRequestsHandler;
    private final boolean fetchLastPageOnly;
    private IndexInterval currentRequest;

    public SimpleLazyList(int pageSize, LazyListService<E> lazyListPeer) {

        this(pageSize, lazyListPeer, true);
    }

    public SimpleLazyList(int pageSize, LazyListService<E> lazyListPeer, boolean fetchLastPageOnly) {

        this.pageSize = pageSize;
        this.lazyListPeer = lazyListPeer;
        this.fetchLastPageOnly = fetchLastPageOnly;
        listeners = new LinkedList<OnLoadListener>();
        size = lazyListPeer.getSize();
        listCache = createListCache(size);
        initListCache(listCache, size);
        pendingRequests = Collections.synchronizedList(new ArrayList<IndexInterval>());
        pendingRequestsHandler = new PendingRequestHandler();
        pendingRequestsHandler.start();
    }

    public void close() {

        pendingRequestsHandler.terminate();
    }

    public void addOnLoadListener(OnLoadListener listener) {

        listeners.add(listener);
    }

    public void removeOnLoadListener(OnLoadListener listener) {

        listeners.remove(listener);
    }

    protected void fireOnLoadEvent(OnLoadEvent event) {

        for (OnLoadListener listener : listeners) {
            listener.elementLoaded(event);
        }
    }

    public boolean isLoaded(int index) {

        return listCache.get(index) != MISSING_DATA_ENTRY;
    }

    protected List<Object> createListCache(int size) {

        return Collections.synchronizedList(new ArrayList<Object>(size));
    }

    protected void initListCache(List<Object> listCache, int size) {

        for (int i = 0; i < size; i++) {
            listCache.add(MISSING_DATA_ENTRY);
        }
    }

    public void add(int index, E element) {

        lazyListPeer.add(index, element);
        listCache.add(index, element);
    }

    public boolean add(E o) {

        add(listCache.size(), o);
        return true;
    }

    public boolean addAll(Collection<? extends E> c) {

        Iterator<? extends E> iterator = c.iterator();
        while (iterator.hasNext()) {
            add(iterator.next());
        }
        return true;
    }

    public boolean addAll(int index, Collection<? extends E> c) {

        throw new UnsupportedOperationException();
    }

    public void clear() {

        throw new UnsupportedOperationException();
    }

    public boolean contains(Object o) {

        throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> c) {

        throw new UnsupportedOperationException();
    }

    protected IndexInterval getStartEndFromIndex(int index) {

        return getStartEndFromPage(getPage(index));
    }

    protected IndexInterval getStartEndFromPage(int page) {

        int startElement = page * pageSize;
        int endElement = Math.min(size, startElement + pageSize);

        return new IndexInterval(startElement, endElement);
    }

    protected int getPage(int index) {

        return index / pageSize;
    }

    public void getAsynchronous(final int index) {

        if (!isLoaded(index)) {
            if (!isRequested(index)) {
                pendingRequests.add(getStartEndFromIndex(index));
            }
            synchronized (pendingRequestsHandler) {
                pendingRequestsHandler.notify();
            }
        }
    }

    protected boolean isRequested(int index) {

        return isInPendingRequests(index) || (currentRequest != null && currentRequest.contains(index));
    }

    protected boolean isInPendingRequests(int index) {

        try {
            for (IndexInterval startEnd : pendingRequests) {
                if (startEnd.contains(index)) {
                    return true;
                }
            }
        } catch (Exception exception) {
            // do nothing
        }

        return false;
    }

    public E get(int index) {

        if (!isLoaded(index)) {
            fetch(getStartEndFromIndex(index));
        }
        return (E) listCache.get(index);
    }

    public void fetch(IndexInterval startEnd) {

        E[] data = lazyListPeer.getData(startEnd.getStart(), startEnd.getEnd());
        for (int i = startEnd.getStart(); i < startEnd.getEnd(); i++) {
            listCache.set(i, data[i - startEnd.getStart()]);
        }
        fireOnLoadEvent(new OnLoadEvent(this, startEnd));
    }

    public int indexOf(Object o) {

        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {

        return listCache.isEmpty();
    }

    public Iterator<E> iterator() {

        throw new UnsupportedOperationException();
    }

    public int lastIndexOf(Object o) {

        throw new UnsupportedOperationException();
    }

    public ListIterator<E> listIterator() {

        throw new UnsupportedOperationException();
    }

    public ListIterator<E> listIterator(int index) {

        throw new UnsupportedOperationException();
    }

    public E remove(int index) {

        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {

        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> c) {

        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> c) {

        throw new UnsupportedOperationException();
    }

    public E set(int index, E element) {

        throw new UnsupportedOperationException();
    }

    public int size() {

        return listCache.size();
    }

    public List<E> subList(int fromIndex, int toIndex) {

        throw new UnsupportedOperationException();
    }

    public <T> T[] toArray(T[] a) {

        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {

        throw new UnsupportedOperationException();
    }

    public class PendingRequestHandler extends Thread {

        private boolean terminate;

        public PendingRequestHandler() {

            terminate = false;
        }

        public synchronized void terminate() {

            terminate = true;
            notify();
        }

        public void run() {

            while (!terminate) {
                while (!pendingRequests.isEmpty()) {
                    if (fetchLastPageOnly) {
                        currentRequest = pendingRequests.get(pendingRequests.size() - 1);
                        pendingRequests.clear();
                    } else {
                        currentRequest = pendingRequests.remove(0);
                    }
                    fetch(currentRequest);
                }
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
    }
}
