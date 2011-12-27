/*
 * Copyright 2011 Christian Essl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */

package org.yeb;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import yeti.lang.AList;
import yeti.lang.LList;
import yeti.lang.Struct;
import yeti.lang.Struct3;

/**
 *
 * @author Christian Essl
 */
abstract public class TMap implements Serializable {

    public static final Object NULL_VALUE = new Object();
    public static final TMap EMPTY = EmptyHashMap.EMPTY;

    public static final class Mutable {

        private TMap map;

        private Mutable(TMap map) {
            this.map = map;
        }

        public AList keyValueList() {
            return map.keyValueList();
        }

        public int size() {
            return map.size();
        }

        final public Object get(Object key) {
            return get(key, null);
        }

        final public Object get(Object key, Object defaultValue) {
            if (key == null) {
                key = NullKey.SINGLETON;
            }
            return map.get0(key, computeHash(key), 0, defaultValue);
        }

        final public void update(Object key, Object value) {
            if (key == null) {
                key = NullKey.SINGLETON;
            }
            this.map = map.updateOpen0(key, computeHash(key), 0, value);
        }

        final public void remove(Object key) {
            if (key == null) {
                key = NullKey.SINGLETON;
            }
            map = map.removedOpen0(key, computeHash(key), 0);
        }

        public TMap close(boolean pack) {
            map.close(pack);
            TMap r = map;
            map = null;
            return r;
        }

        public TMap close() {
            return close(false);
        }
    }

    final public Mutable mutable() {
        return new Mutable(this);
    }

    final public Object get(Object key) {
        return get(key, null);
    }

    final public Object get(Object key, Object defaultValue) {
        if (key == null) {
            key = NullKey.SINGLETON;
        }
        return get0(key, computeHash(key), 0, defaultValue);
    }

    final public TMap update(Object key, Object value) {
        if (key == null) {
            key = NullKey.SINGLETON;
        }
        return update0(key, computeHash(key), 0, value, true);
    }

    final public TMap remove(Object key) {
        if (key == null) {
            key = NullKey.SINGLETON;
        }
        return removed0(key, computeHash(key), 0, true);
    }

    abstract public AList keyValueList();

    abstract public int size();

    abstract Object get0(Object key, int hash, int level, Object defaultValue);

    abstract TMap update0(Object key, int hash, int level, Object value, boolean close);

    abstract TMap updateOpen0(Object key, int hash, int level, Object value);

    abstract TMap removed0(Object key, int hash, int level, boolean close);

    abstract TMap removedOpen0(Object key, int hash, int level);

    abstract boolean isClosed();

    abstract void close(boolean compact);

    static final boolean compareKey(Object key, Object key2) {
        if (key == key2) {
            return true;
        } else {
            return key.equals(key2);
        }
    }

    static final int computeHash(Object key) {
        int hash = key.hashCode();
        return hash;
        /*int h = hash + ~(hash << 9);
        h = h ^(h >>> 14);
        h = h + (h << 4);
        h = h ^ (h >>> 10);
        return h;*/
    }

    static final Struct keyValue(Object key, Object value) {
        Struct3 str = new Struct3(new String[]{"key", "value"}, new boolean[]{false, false});
        str._0 = key;
        str._1 = value;
        return str;
    }

    static final Object[] removeFromArray(Object[] arr, int pos) {
        Object[] newArr = new Object[arr.length - 2];
        System.arraycopy(arr, 0, newArr, 0, pos);
        System.arraycopy(arr, pos + 2, newArr, pos, newArr.length - pos);
        return newArr;
    }

    static final Object[] addToArray(Object[] arr, int pos, Object key, Object value) {
        Object[] newArr = new Object[arr.length + 2];
        System.arraycopy(arr, 0, newArr, 0, pos);
        System.arraycopy(arr, pos, newArr, pos + 2, arr.length - pos);
        newArr[pos] = key;
        newArr[pos + 1] = value;
        return newArr;
    }

    static final Object[] replaceInArray(Object[] arr, int pos, Object key, Object value) {
        Object[] newArr = new Object[arr.length];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        newArr[pos] = key;
        newArr[pos + 1] = value;
        return newArr;
    }

    static final Object[] addToMutArray(Object[] arr, int pos, Object key, Object value, int currOldSize) {
        Object[] newArr = arr;
        final int elemCurrOldSize = currOldSize * 2;
        {
            int minCapacity = elemCurrOldSize + 2;
            int oldCapacity = arr.length;
            if (minCapacity > oldCapacity) {
                int newCapacity = (oldCapacity * 3) / 2 + 1;
                newCapacity = newCapacity % 2 == 0 ? newCapacity : newCapacity + 1;
                if (newCapacity < minCapacity) {
                    newCapacity = minCapacity;
                } else {
                    if (newCapacity % 2 > 0) {
                        newCapacity = newCapacity + 1;
                    }
                }
                // minCapacity is usually close to size, so this is a win:
                newArr = new Object[newCapacity];
                System.arraycopy(arr, 0, newArr, 0, arr.length);
            }

        }
        System.arraycopy(arr, pos, newArr, pos + 2, elemCurrOldSize - pos);
        newArr[pos] = key;
        newArr[pos + 1] = value;
        return newArr;
    }

    static final Object[] removeFromMutArray(Object[] arr, int pos) {
        int size = arr.length;
        int numMoved = size - pos - 2;
        if (numMoved > 0) {
            System.arraycopy(arr, pos + 2, arr, pos,
                    numMoved);
        }
        arr[size - 1] = null; // Let gc do its work
        arr[size - 2] = null;
        return arr;
    }

    static final Object[] replaceInMutArray(Object[] arr, int pos, Object key, Object value) {
        arr[pos] = key;
        arr[pos + 1] = value;
        return arr;
    }
}

class NullKey extends Object implements Serializable {

    final static NullKey SINGLETON = new NullKey();

    private NullKey() {
    }

    private Object readResolve() {
        return NullKey.SINGLETON;
    }

    public boolean equals(Object obj) {
        return obj == this;
    }

    public int hashCode() {
        return 234567899;
    }
}

class EmptyHashMap extends TMap {

    public static final EmptyHashMap EMPTY = new EmptyHashMap();

    private EmptyHashMap() {
    }

    Object get0(Object key, int computeHash, int level, Object defaultValue) {
        return defaultValue;
    }

    TMap update0(Object key, int hash, int level, Object value, boolean close) {
        return (new HashTrieMap(0, new Object[0], 0, close)).update0(key, hash, level, value, close);
    }

    TMap updateOpen0(Object key, int hash, int level, Object value) {
        return (new HashTrieMap(0, new Object[0], 0, false)).updateOpen0(key, hash, level, value);
    }

    TMap removed0(Object key, int hash, int level, boolean close) {
        return this;
    }

    TMap removedOpen0(Object key, int hash, int level) {
        return new HashTrieMap(0, new Object[0], 0, false);
    }

    public int size() {
        return 0;
    }

    public AList keyValueList() {
        return null;
    }

    boolean isClosed() {
        return true;
    }

    void close(boolean pack) {
    }

    private Object readResolve() {
        return TMap.EMPTY;
    }

    public boolean equals(Object obj) {
        return obj == this;
    }

    public int hashCode() {
        return 98235432;
    }
}

class HashMapCollision extends TMap {

    private Object[] keyValues;
    private final int myHash;
    private boolean closed;
    private int size;

    HashMapCollision(int hash, Object[] keyValues, boolean close, int size) {
        this.keyValues = keyValues;
        this.myHash = hash;
        this.closed = close;
        this.size = size;
    }

    public int size() {
        return size;
    }

    Object get0(Object key, int hash, int level, Object defaultValue) {
        for (int i = 0; i < keyValues.length; i = i + 2) {
            if (keyValues[i] == null) {
                return defaultValue;
            }
            if (compareKey(keyValues[i], key)) {
                return keyValues[i + 1];
            }
        }
        return defaultValue;
    }

    TMap update0(Object key, int hash, int level, Object value, boolean close) {
        if (hash == this.myHash) {
            //hash collision again
            int pos = -1;
            for (int i = 0; i < keyValues.length; i = i + 2) {
                if (keyValues[i] == null) {
                    break;
                }
                if (compareKey(key, keyValues[i])) {
                    pos = i;
                    break;
                }
            }
            if (pos == -1) {
                //different key so add
                Object[] newKeyValues = addToArray(keyValues, 0, key, value);
                return new HashMapCollision(hash, newKeyValues, close, size + 1);
            } else {
                //same key
                if (keyValues[pos + 1] == value) {
                    //same value nothing changes
                    return this;
                } else {
                    //replace value
                    Object[] newKeyValues = replaceInArray(keyValues, pos, key, value);
                    return new HashMapCollision(hash, newKeyValues, close, size);
                }
            }
        } else {
            //different hash we make a HashTrieMap
            //instead of us (so ath the same level)
            TMap m = new HashTrieMap(0, new Object[0], 0, close);
            for (int i = 0; i < keyValues.length; i = i + 2) {
                if (keyValues[i] == null) {
                    break;
                }
                m = m.update0(keyValues[i], myHash, level, keyValues[i + 1], close);
            }
            return m.update0(key, hash, level, value, close);
        }
    }

    TMap updateOpen0(Object key, int hash, int level, Object value) {
        if (closed) {
            return update0(key, hash, level, value, false);
        }

        if (hash == this.myHash) {
            //hash collision again
            int pos = -1;
            for (int i = 0; i < keyValues.length; i = i + 2) {
                if (keyValues[i] == null) {
                    break;
                }
                if (compareKey(key, keyValues[i])) {
                    pos = i;
                    break;
                }
            }
            if (pos == -1) {
                //different key so add
                this.keyValues = addToMutArray(keyValues, 0, key, value, size());
                size = size + 1;
            } else {
                //same key
                if (keyValues[pos + 1] == value) {
                    //same value nothing changes
                    return this;
                } else {
                    //replace value
                    this.keyValues = replaceInArray(keyValues, pos, key, value);
                }
            }
        } else {
            //different hash we make a HashTrieMap
            //instead of us (so ath the same level)
            TMap m = new HashTrieMap(0, new Object[0], 0, false);
            for (int i = 0; i < keyValues.length; i = i + 2) {
                if (keyValues[i] == null) {
                    break;
                }
                m = m.updateOpen0(keyValues[i], myHash, level, keyValues[i + 1]);
            }
            return m.updateOpen0(key, hash, level, value);
        }
        return this;
    }

    TMap removed0(Object key, int hash, int level, boolean close) {
        if (hash == this.myHash) {
            //onyl if the hash fits we are affected at all

            //find the key
            int pos = -1;
            for (int i = 0; i < keyValues.length; i = i + 2) {
                if (keyValues[i] == null) {
                    break;
                }
                if (compareKey(key, keyValues[i])) {
                    pos = i;
                    break;
                }
            }

            if (pos == -1) //no key found so we are not affected
            {
                return this;
            } else {
                if (this.size() == 1) {
                    //a key was found and wehn we remove it our length will be 0 so we will be empty
                    return TMap.EMPTY;
                } else {
                    //otherwise clone and remove from array the mapping
                    Object[] nKeyValues = removeFromArray(keyValues, pos);
                    return new HashMapCollision(hash, nKeyValues, close, size - 1);
                }
            }
        } else {
            //the hash did not fit so we return ourself.
            return this;
        }
    }

    TMap removedOpen0(Object key, int hash, int level) {
        if (closed) {
            return removed0(key, hash, level, false);
        }

        if (hash == this.myHash) {
            //onyl if the hash fits we are affected at all

            //find the key
            int pos = -1;
            for (int i = 0; i < keyValues.length; i = i + 2) {
                if (keyValues[i] == null) {
                    break;
                }
                if (compareKey(key, keyValues[i])) {
                    pos = i;
                    break;
                }
            }

            if (pos == -1) //no key found so we are not affected
            {
                return this;
            } else {
                if (this.size() == 1) {
                    //a key was found and wehn we remove it our length will be 0 so we will be empty
                    return TMap.EMPTY;
                } else {
                    //otherwise clone and remove from array the mapping
                    keyValues = removeFromArray(keyValues, pos);
                    size = size - 1;
                    return this;
                }
            }
        } else {
            //the hash did not fit so we return ourself.
            return this;
        }
    }

    public AList keyValueList() {
        return HMList.create(keyValues, 0, null);
    }

    boolean isClosed() {
        return closed;
    }

    void close(boolean pack) {
        if (this.closed) {
            return;
        }
        //pack the array
        if (pack) {
            int newSize = size * 2;
            if (newSize < keyValues.length) {
                Object[] newArr = new Object[newSize];
                System.arraycopy(keyValues, 0, newArr, 0, newSize);
                keyValues = newArr;
            }
        }
        //mark it as closed
        closed = true;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof HashMapCollision)) {
            return false;
        } else {
            HashMapCollision o = (HashMapCollision) obj;
            if (myHash == o.myHash && o.closed == closed && size == o.size) {
                for (int i = 0; i < size; i++) {
                    int j = i * 2;
                    if (!keyValues[j].equals(o.keyValues[j])) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public int hashCode() {
        return myHash * 2;
    }
}

class HashTrieMap extends TMap {

    private int bitmap;//which elements in an virtual 32slot array are occurpied
    private Object[] elems; //consists of pairs of [key,value] or [null,HashMap2]
    private int size; //total size of this + subtrress
    private boolean closed;

    HashTrieMap(int bitmap, Object[] elems, int size, boolean closed) {
        this.bitmap = bitmap;
        this.elems = elems;
        this.size = size;
        this.closed = closed;
    }

    public int size() {
        return this.size;
    }

    private int childSize() {
        return Integer.bitCount(bitmap);
    }

    protected Object get0(Object key, int hash, int level, Object defaultValue) {
        int index = (hash >>> level) & 0x1f; //our hash blok move by level and null out with 11111 
        int mask = (1 << index); //the bit in the 31 slot array of this hash
        int offset = -1;//the actual index in the elems array
        /*if (bitmap == -1) {
        offset = (index & 0x1f) * 2;
        }else */
        if ((bitmap & mask) != 0) {
            offset = (Integer.bitCount(bitmap & (mask - 1))) * 2;
            //return elems[offset].get0(key,hash,level+5);
        } else {
            //
            return defaultValue;
        }
        Object keyOrNull = elems[offset];
        Object valueOrMap = elems[offset + 1];

        if (keyOrNull == null) {
            //we have a submap
            return ((TMap) valueOrMap).get0(key, hash, level + 5, defaultValue);
        } else {
            //we have a key; compare it and return
            if (compareKey(key, keyOrNull)) {
                return valueOrMap;
            }
            return defaultValue;
        }
    }

    protected TMap update0(Object key, int hash, int level, Object value, boolean close) {
        int index = (hash >>> level) & 0x1f;
        int mask = (1 << index);
        int offset = (Integer.bitCount(bitmap & (mask - 1))) * 2;
        if ((bitmap & mask) != 0) {
            //a hash matches att offset in elemenst


            //see wheter we have a value or we have a map
            Object keyOrNull = elems[offset];
            Object valueOrMap = elems[offset + 1];
            if (keyOrNull == null) {
                //we have a submap

                //udate the submap
                TMap sub = (TMap) valueOrMap;
                TMap newSub = sub.update0(key, hash, level + 5, value, close);
                if (newSub == sub) {
                    return this;
                } else {
                    //the submap has changed so replace it in your array
                    Object[] elemsNew = replaceInArray(elems, offset, null, newSub);
                    return new HashTrieMap(bitmap, elemsNew, size + (newSub.size() - sub.size()), close);
                }
            } else {
                //we have a value in the elemsn
                //if the key matches replace it otherwise add it
                if (compareKey(key, keyOrNull)) {
                    //the two keys match so replace the value (if it is different)
                    if (value == valueOrMap) {
                        return this;
                    } else {
                        //same key so we have to replace
                        Object[] elemsNew = replaceInArray(elems, offset, key, value);
                        return new HashTrieMap(bitmap, elemsNew, size, close);
                    }
                } else {
                    //different key in same hash bucket so we have to join them either
                    //in a collision or hashtrie
                    int keyOrNullHash = computeHash(keyOrNull);
                    TMap newSub;
                    if (hash == keyOrNullHash) {

                        newSub = new HashMapCollision(hash, new Object[]{keyOrNull, valueOrMap, key, value}, close, 2);
                    } else {
                        newSub = new HashTrieMap(0, new Object[0], 0, close);
                        newSub = newSub.update0(key, hash, level + 5, value, close);
                        newSub = newSub.update0(keyOrNull, keyOrNullHash, level + 5, valueOrMap, close);
                    }
                    //and replace the old key/value with the new map
                    Object[] elemsNew = replaceInArray(elems, offset, null, newSub);
                    return new HashTrieMap(bitmap, elemsNew, size + 1, close);
                }
            }
        } else {
            //the hash does not mathc any bucket yet
            //so we have to insert the key/value directly into the array
            //and update the bitmap to indicate that we have a new bucket.
            Object[] elemsNew = addToArray(elems, offset, key, value);
            int bitmapNew = bitmap | mask;
            return new HashTrieMap(bitmapNew, elemsNew, size + 1, close);
        }
    }

    protected TMap updateOpen0(Object key, int hash, int level, Object value) {
        if (closed) {
            return update0(key, hash, level, value, false);
        }

        int index = (hash >>> level) & 0x1f;
        int mask = (1 << index);
        int offset = (Integer.bitCount(bitmap & (mask - 1))) * 2;
        if ((bitmap & mask) != 0) {
            //a hash matches att offset in elemenst

            //see wheter we have a value or we have a map
            Object keyOrNull = elems[offset];
            Object valueOrMap = elems[offset + 1];
            if (keyOrNull == null) {
                //we have a submap

                //udate the submap
                TMap sub = (TMap) valueOrMap;
                int oldSubSize = sub.size();

                TMap newSub = sub.updateOpen0(key, hash, level + 5, value);
                this.size = this.size + (newSub.size() - oldSubSize);
                if (newSub == sub) {
                    return this;
                } else {
                    //the submap has changed so replace it in your array
                    this.elems = replaceInMutArray(elems, offset, null, newSub);
                    return this;
                }
            } else {
                //we have a value in the elemsn
                //if the key matches replace it otherwise add it
                if (compareKey(key, keyOrNull)) {
                    //the two keys match so replace the value (if it is different)
                    if (value == valueOrMap) {
                        return this;
                    } else {
                        //same key so we have to replace
                        elems = replaceInMutArray(elems, offset, key, value);
                        return this;
                    }
                } else {
                    //different key in same hash bucket so we have to join them either
                    //in a collision or hashtrie
                    int keyOrNullHash = computeHash(keyOrNull);
                    TMap newSub;
                    if (hash == keyOrNullHash) {
                        newSub = new HashMapCollision(hash, new Object[]{keyOrNull, valueOrMap, key, value}, false, 2);
                    } else {
                        newSub = new HashTrieMap(0, new Object[0], 0, false);
                        newSub = newSub.updateOpen0(key, hash, level + 5, value);
                        newSub = newSub.updateOpen0(keyOrNull, keyOrNullHash, level + 5, valueOrMap);
                    }
                    //and replace the old key/value with the new map
                    elems = replaceInMutArray(elems, offset, null, newSub);
                    size = size + 1;
                    return this;
                }
            }
        } else {
            //the hash does not mathc any bucket yet
            //so we have to insert the key/value directly into the array
            //and update the bitmap to indicate that we have a new bucket.
            int levelElements = Integer.bitCount(bitmap);
            elems = addToMutArray(elems, offset, key, value, levelElements);
            size = size + 1;
            this.bitmap = bitmap | mask;
            return this;
        }
    }

    protected TMap removed0(Object key, int hash, int level, boolean close) {
        int index = (hash >>> level) & 0x1f;
        int mask = (1 << index);
        int offset = (Integer.bitCount(bitmap & (mask - 1))) * 2;

        if ((bitmap & mask) != 0) {
            //we have a slot for this hash
            Object keyOrNull = elems[offset];
            Object valueOrMap = elems[offset + 1];
            if (keyOrNull == null) {
                //we have a sub map in the slot so delegate remove to this
                TMap sub = (TMap) valueOrMap;
                TMap subNew = sub.removed0(key, hash, level + 5, close);

                //if nothing changed return yourself
                if (sub == subNew) {
                    return this;
                }

                //if the sub is now empty clean it
                if (subNew.size() == 0) {
                    //update bitmap
                    int bitmapNew = bitmap ^ mask;
                    //Check if we are also empty
                    if (bitmapNew == 0) {
                        //if so than we just returnt the empty map
                        return TMap.EMPTY;
                    } else {
                        //otherwiese we have to remove the empty submap
                        Object[] elemsNew = removeFromArray(elems, offset);
                        int sizeNew = size - sub.size();
                        return new HashTrieMap(bitmapNew, elemsNew, sizeNew, close);
                    }
                } else {
                    //the sub has changed so we have to replace it
                    Object[] elemsNew = replaceInArray(elems, offset, null, subNew);
                    int sizeNew = size + (subNew.size() - sub.size());
                    return new HashTrieMap(bitmap, elemsNew, sizeNew, close);
                }
            } else {
                //we have a key and a value (no map)
                //just check wheter the key matches if so remove it
                //otherwise keep unchanged
                if (compareKey(key, keyOrNull)) {
                    int bitmapNew = bitmap ^ mask;
                    if (bitmapNew == 0) {
                        return TMap.EMPTY;
                    } else {
                        //remove from elems array key and value
                        Object[] elemsNew = removeFromArray(elems, offset);
                        int sizeNew = size - 1;
                        return new HashTrieMap(bitmapNew, elemsNew, sizeNew, close);
                    }
                } else {
                    //key does not match stay unchanged
                    return this;
                }

            }
        } else {
            //no slot for the hash
            //stay unchanged
            return this;
        }
    }

    TMap removedOpen0(Object key, int hash, int level) {
        if (closed) {
            return removed0(key, hash, level, false);
        }

        int index = (hash >>> level) & 0x1f;
        int mask = (1 << index);
        int offset = (Integer.bitCount(bitmap & (mask - 1))) * 2;

        if ((bitmap & mask) != 0) {
            //we have a slot for this hash
            Object keyOrNull = elems[offset];
            Object valueOrMap = elems[offset + 1];
            if (keyOrNull == null) {
                //we have a sub map in the slot so delegate remove to this
                TMap sub = (TMap) valueOrMap;
                TMap subNew = sub.removedOpen0(key, hash, level + 5);

                //if nothing changed return yourself
                if (sub == subNew) {
                    return this;
                }

                //if the sub is now empty clean it
                if (subNew.size() == 0) {
                    //update bitmap
                    bitmap = bitmap ^ mask;

                    //Check if we are also empty
                    if (bitmap == 0) {
                        //if so than we just returnt the empty map
                        return TMap.EMPTY;
                    } else {
                        //otherwiese we have to remove the empty submap
                        elems = removeFromArray(elems, offset);
                        size = size - sub.size();
                        return this;
                    }
                } else {
                    //the sub has changed so we have to replace it
                    elems = replaceInArray(elems, offset, null, subNew);
                    size = size + (subNew.size() - sub.size());
                    return this;
                }
            } else {
                //we have a key and a value (no map)
                //just check wheter the key matches if so remove it
                //otherwise keep unchanged
                if (compareKey(key, keyOrNull)) {
                    bitmap = bitmap ^ mask;
                    if (bitmap == 0) {
                        return TMap.EMPTY;
                    } else {
                        //remove from elems array key and value
                        elems = removeFromArray(elems, offset);
                        size = size - 1;
                        return this;
                    }
                } else {
                    //key does not match stay unchanged
                    return this;
                }

            }
        } else {
            //no slot for the hash
            //stay unchanged
            return this;
        }
    }

    public AList keyValueList() {
        return HMList.create(elems, 0, null);
    }

    boolean isClosed() {
        return closed;
    }

    void close(boolean pack) {
        if (this.closed) {
            return;
        }
        if (pack) {
            //pack the array
            int neededLength = (Integer.bitCount(this.bitmap)) * 2;
            if (elems.length > neededLength) {
                Object[] newElems = new Object[neededLength];
                System.arraycopy(elems, 0, newElems, 0, neededLength);
                this.elems = newElems;
            }
        }

        //close yourself
        closed = true;

        //recursively do submaps
        for (int i = 0; i < elems.length; i = i + 2) {
            if (elems[i] == null) {
                Object mapOrNull = elems[i + 1];
                if (mapOrNull == null) {
                    return;
                }
                ((TMap) mapOrNull).close(pack);
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof HashTrieMap)) {
            return false;
        } else {
            HashTrieMap o = (HashTrieMap) obj;
            if (bitmap == o.bitmap && o.closed == closed && size == o.size) {
                for (int i = 0; i < elems.length; i = i + 2) {
                    Object keyOrNull = elems[i];
                    Object valueOrMap = elems[+1];
                    //check if we have ended in the bigger array
                    if (keyOrNull == null && valueOrMap == null) {
                        return true;
                    }
                    if (keyOrNull == null && o.elems[i] != null) {
                        return false;
                    } else {
                        if (!keyOrNull.equals(o.elems[i])) {
                            return false;
                        }
                    }
                    if (!valueOrMap.equals(o.elems[i + 1])) {
                        return false;
                    }

                }
                return true;
            } else {
                return false;
            }
        }
    }

    public int hashCode() {
        return Arrays.hashCode(elems) + bitmap * 17 + size * 34;
    }
}

class HMList extends LList {

    final Object[] array;
    final int i;
    final AList l;

    static AList create(Object[] second, int index, AList first) {
        if (first == null) {
            //search in the array for another list
            //if so return this one
            for (int i = index; i < second.length; i = i + 2) {
                Object keyOrNull = second[i];
                Object valueOrMap = second[i + 1];
                if (keyOrNull == null) {
                    TMap map = (TMap) valueOrMap;
                    return new HMList(second, i + 2, map.keyValueList());
                } else {
                    //we just have values
                    return new HMList(second, i, null);
                }
            }
            //we neither have a first list and the map is end
            return null;
        } else {
            //use the rest map but keep this
            return new HMList(second, index, first);
        }
    }

    HMList(Object[] array, int i, AList l) {
        super(null, null);
        this.array = array;
        this.i = i;
        this.l = l;
    }
    private final static String[] STRUCT_NAMES = {"key", "value"};

    public Object first() {
        if (l != null) {
            return l.first();
        } else {
            Object k = array[i];
            k = k == NullKey.SINGLETON ? null : k;
            Object v = array[i + 1];
            Struct st = TMap.keyValue(k, v);
            return st;
        }
    }

    public AList rest() {
        if (l != null) {
            return create(array, i, l.rest());
        }
        return create(array, i + 2, null);
    }
}
