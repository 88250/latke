/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.latke.cache.local.util;

import java.io.Serializable;


/**
 * This is a generic thread safe double linked map. It's very simple and all
 * the operations are so quick that course grained synchronization is more than
 * acceptable.
 * 
 * @param <K> the type of the key of this map's elements
 * @param <V> the type of the nodes of this map
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.5, Oct 26, 2010
 */
public final class DoubleLinkedMap<K, V> implements Serializable {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /** 
     * record size to avoid having to iterate.
     */
    private int size = 0;
    /**
     * LRU double linked map head node.
     */
    private DoubleLinkedMapNode<K, V> first;
    /**.
     * LRU double linked map tail node.
     */
    private DoubleLinkedMapNode<K, V> last;

    /**
     * Removes a velue in the map by the specified key.
     *
     * @param key the specified key
     * @return {@code true} if removed, {@code false} for not found
     */
    public synchronized boolean remove(final K key) {
        final DoubleLinkedMapNode<K, V> node = getNode(key);
        if (node != null) {
            removeNode(node);

            return true;
        }

        return false;
    }

    /**
     * Gets a value in the map by the specified key.
     *
     * @param key the specified key
     * @return the value of the specified key, if not found, returns
     *         <code>null</code>
     */
    public synchronized V get(final K key) {
        if (first == null) {
            return null;
        } else {
            DoubleLinkedMapNode<K, V> current = first;
            while (current != null) {
                if (current.getKey().equals(key)) {
                    return current.getValue();
                } else {
                    current = current.getNext();
                }
            }
        }

        return null;
    }

    /**
     * Adds a new value to the end of the linked map.
     *
     * @param key the key of the new value
     * @param value the new value
     */
    public synchronized void addLast(final K key, final V value) {
        final DoubleLinkedMapNode<K, V> node =
                new DoubleLinkedMapNode<K, V>(key, value);
        addLastNode(node);
    }

    /**
     * Adds a new value to the start of the linked map.
     *
     * <p>
     * Throws {@link IllegalArgumentException} if the specified key is null
     * </p>
     *
     * @param key the key of the new value
     * @param value the new value
     */
    public synchronized void addFirst(final K key, final V value) {
        if (null == key) {
            throw new IllegalArgumentException("Key is null!");
        }

        final DoubleLinkedMapNode<K, V> node =
                new DoubleLinkedMapNode<K, V>(key, value);
        addFirstNode(node);
    }

    /**
     * Moves an existing node to the start of the linked map.
     *
     * @param key the key of the node to set as the head.
     */
    public synchronized void makeFirst(final K key) {
        final DoubleLinkedMapNode<K, V> node = getNode(key);
        if (node.getPrev() == null) {
            // already the first node or not a node.
            return;
        }

        node.getPrev().setNext(node.getNext());

        if (node.getNext() == null) {
            // last but not the first.
            last = node.getPrev();
            last.setNext(null);
        } else {
            // neither the last nor the first.
            node.getNext().setPrev(node.getPrev());
        }

        first.setPrev(node);
        node.setNext(first);
        node.setPrev(null);
        first = node;
    }

    /**
     * Remove all of the nodes of the linked map.
     */
    @SuppressWarnings("unchecked")
    public synchronized void removeAll() {
        for (DoubleLinkedMapNode<K, V> me = first; me != null;) {
            if (me.getPrev() != null) {
                me.setPrev(null);
            }

            final DoubleLinkedMapNode<K, V> next = me.getNext();
            me = next;
        }

        first = null;
        last = null;
        // make sure this will work, could be add while this is happening.
        size = 0;
    }

    /**
     * Removes the last value.
     *
     * @return last value removed if success, {@code null} otherwise
     */
    public synchronized V removeLast() {
        final DoubleLinkedMapNode<K, V> lastNode = removeLastNode();

        if (null != lastNode) {
            return lastNode.getValue();
        }

        return null;
    }

    /**
     * Returns the current size of the map.
     *
     * @return the current size of the map
     */
    public synchronized int size() {
        return size;
    }

    /**
     * Removes the last node of the linked map.
     *
     * @return the last node if there was one to be removed.
     */
    private synchronized DoubleLinkedMapNode<K, V> removeLastNode() {
        final DoubleLinkedMapNode<K, V> ret = last;

        if (last != null) {
            removeNode(last);
        }

        return ret;
    }

    /**
     * Gets a node in the map by the specified key.
     *
     * @param key the specified key
     * @return a node of specified key, if not found, returns <code>null</code>
     */
    private synchronized DoubleLinkedMapNode<K, V> getNode(final K key) {
        if (first == null) {
            return null;
        } else {
            DoubleLinkedMapNode<K, V> current = first;
            while (current != null) {
                if (current.getKey().equals(key)) {
                    return current;
                } else {
                    current = current.getNext();
                }
            }
        }

        return null;
    }

    /**
     * Removes the specified node of the linked map.
     *
     * @param node the specified node to be removed
     */
    private synchronized void removeNode(final DoubleLinkedMapNode<K, V> node) {
        if (node.getNext() == null) {
            if (node.getPrev() == null) {
                // make sure it really is the only node before setting head and
                // tail to null. It is possible that we will be passed a node
                // which has already been removed from the map, in which case
                // we should ignore it.
                if (node == first && node == last) {
                    first = null;
                    last = null;
                }

            } else {
                // last but not the first.
                last = node.getPrev();
                last.setNext(null);
                node.setPrev(null);
            }

        } else if (node.getPrev() == null) {
            // first but not the last.
            first = node.getNext();
            first.setPrev(null);
            node.setNext(null);
        } else {
            // neither the first nor the last.
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
            node.setPrev(null);
            node.setNext(null);
        }

        size--;
    }

    /**
     * Adds a new node to the end of the linked map.
     *
     * @param node the feature to be added to the end of the linked map
     */
    private synchronized void addLastNode(final DoubleLinkedMapNode<K, V> node) {
        if (first == null) {
            // empty map.
            first = node;
        } else {
            last.setNext(node);
            node.setPrev(last);
        }

        last = node;
        size++;
    }

    /**
     * Adds a new node to the start of the linked map.
     *
     * @param node the feature to be added to the start of the linked map
     */
    private synchronized void addFirstNode(final DoubleLinkedMapNode<K, V> node) {
        if (last == null) {
            // empty map.
            last = node;
        } else {
            first.setPrev(node);
            node.setNext(first);
        }

        first = node;
        size++;
    }
}
/**
 * A node of {@link DoubleLinkedMap double linked map}.
 *
 * @param <K> the type of the key of this node's element
 * @param <V> the type of this node
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.1, Aug 15, 2010
 */
final class DoubleLinkedMapNode<K, V> implements Serializable {

    /**
     * Generated serial version id.
     */
    private static final long serialVersionUID = -5617593667027497669L;
    /**
     * Payload of this node.
     */
    private V value;
    /**
     * Key of this node.
     */
    private K key;
    /**
     * Double linked map previous reference.
     */
    private DoubleLinkedMapNode<K, V> prev;
    /**
     * Double linked map next reference.
     */
    private DoubleLinkedMapNode<K, V> next;

    /**
     * Constructs a double linked map node with the specified key and value.
     *
     * @param key the key of the specified node's content instance
     * @param value the specified node's content instance
     */
    public DoubleLinkedMapNode(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key of this node.
     *
     * @return the key of this node
     */
    public K getKey() {
        return key;
    }

    /**
     * Gets the value of this node.
     *
     * @return the node's content instance.
     */
    public V getValue() {
        return value;
    }

    /**
     * Gets the next node of this node.
     *
     * @return the next node of this node
     */
    protected DoubleLinkedMapNode<K, V> getNext() {
        return next;
    }

    /**
     * Sets the next node of this node by the specified next node.
     *
     * @param next the specified next node
     */
    protected void setNext(final DoubleLinkedMapNode<K, V> next) {
        this.next = next;
    }

    /**
     * Gets the previous node of this node.
     *
     * @return the previous node of this node
     */
    protected DoubleLinkedMapNode<K, V> getPrev() {
        return prev;
    }

    /**
     * Sets the previous node of this node by the specified previous node.
     *
     * @param prev the specified previous node
     */
    protected void setPrev(final DoubleLinkedMapNode<K, V> prev) {
        this.prev = prev;
    }
}
