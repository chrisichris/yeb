/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yeb;

import java.util.Arrays;
import java.util.Map;
import yeti.lang.*;

/**
 *
 * @author Christian
 */
public class HashStruct extends Hash implements Struct {
    final String[] names;
    private final boolean[] vars;
    private boolean allMutable;

    private static String[] getNames(Map values) {
        String[] result =
            (String[]) values.keySet().toArray(new String[values.size()]);
        Arrays.sort(result);
        return result;
    }

    public HashStruct(Map ma) {
        super(ma);
        this.names = getNames(ma);
        this.vars = null;
    }

    public int count() {
        return names.length;
    }

    public String name(int field) {
        return names[field];
    }

    public Object get(String field) {
        return super.get(field);
    }

    public Object get(int field) {
        return super.get(name(field));
    }

    public void set(String field, Object value) {
        super.put(field, value);
    }

    public Object ref(int field, int[] index, int at) {
        if (!allMutable) {
            if (vars != null && vars[field]) {
                index[at] = field;
                return this;
            }
            index[at] = -1;
            return get(field);
        }
        index[at] = field;
        return this;
    }
    
    public boolean equals(Object o) {
        if (o instanceof Map) {
            return super.equals(o);
        }else {
            if (o instanceof Struct) {
                Struct st = (Struct) o;
                int acnt = count(), bcnt = st.count(), i = 0, j = 0;
                while (i < acnt && j < bcnt) {
                    String an, bn;
                    if ((an = name(i)) == (bn = st.name(j))) {
                        Object a = get(i);
                        Object b = st.get(j);
                        if (a != b && (a == null || !a.equals(b)))
                            return false;
                    } else {
                        int cmp = an.compareTo(bn);
                        if (cmp > 0) --i;
                        if (cmp < 0) --j;
                    }
                    ++i;
                    ++j;
                }
                return true;
                
            }else
                return false;
        }
            
    }
    

    
}
