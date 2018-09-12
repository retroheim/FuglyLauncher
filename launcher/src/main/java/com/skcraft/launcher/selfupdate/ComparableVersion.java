/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher.selfupdate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Stack;

public class ComparableVersion
implements Comparable<ComparableVersion> {
    private String value;
    private String canonical;
    private ListItem items;

    public ComparableVersion(String version) {
        this.parseVersion(version);
    }

    public final void parseVersion(String version) {
        this.value = version;
        this.items = new ListItem();
        version = version.toLowerCase(Locale.ENGLISH);
        ListItem list = this.items;
        Stack<ListItem> stack = new Stack<ListItem>();
        stack.push(list);
        boolean isDigit = false;
        int startIndex = 0;
        for (int i = 0; i < version.length(); ++i) {
            char c = version.charAt(i);
            if (c == '.') {
                if (i == startIndex) {
                    list.add(new IntegerItem(0));
                } else {
                    list.add(ComparableVersion.parseItem(isDigit, version.substring(startIndex, i)));
                }
                startIndex = i + 1;
                continue;
            }
            if (c == '-') {
                if (i == startIndex) {
                    list.add(new IntegerItem(0));
                } else {
                    list.add(ComparableVersion.parseItem(isDigit, version.substring(startIndex, i)));
                }
                startIndex = i + 1;
                if (!isDigit) continue;
                list.normalize();
                if (i + 1 >= version.length() || !Character.isDigit(version.charAt(i + 1))) continue;
                ListItem listItem = list;
                list = new ListItem();
                listItem.add(list);
                stack.push(list);
                continue;
            }
            if (Character.isDigit(c)) {
                if (!isDigit && i > startIndex) {
                    list.add(new StringItem(version.substring(startIndex, i), true));
                    startIndex = i;
                }
                isDigit = true;
                continue;
            }
            if (isDigit && i > startIndex) {
                list.add(ComparableVersion.parseItem(true, version.substring(startIndex, i)));
                startIndex = i;
            }
            isDigit = false;
        }
        if (version.length() > startIndex) {
            list.add(ComparableVersion.parseItem(isDigit, version.substring(startIndex)));
        }
        while (!stack.isEmpty()) {
            list = (ListItem)stack.pop();
            list.normalize();
        }
        this.canonical = this.items.toString();
    }

    private static Item parseItem(boolean isDigit, String buf) {
        return isDigit ? new IntegerItem(new Integer(buf)) : new StringItem(buf, false);
    }

    @Override
    public int compareTo(ComparableVersion o) {
        return this.items.compareTo(o.items);
    }

    public String toString() {
        return this.value;
    }

    public boolean equals(Object o) {
        return o instanceof ComparableVersion && this.canonical.equals(((ComparableVersion)o).canonical);
    }

    public int hashCode() {
        return this.canonical.hashCode();
    }

    private static class ListItem
    extends ArrayList<Item>
    implements Item {
        private ListItem() {
        }

        @Override
        public int getType() {
            return 2;
        }

        @Override
        public boolean isNull() {
            return this.size() == 0;
        }

        void normalize() {
            Item item;
            ListIterator iterator = this.listIterator(this.size());
            while (iterator.hasPrevious() && (item = (Item)iterator.previous()).isNull()) {
                iterator.remove();
            }
        }

        @Override
        public int compareTo(Item item) {
            if (item == null) {
                if (this.size() == 0) {
                    return 0;
                }
                Item first = (Item)this.get(0);
                return first.compareTo(null);
            }
            switch (item.getType()) {
                case 0: {
                    return -1;
                }
                case 1: {
                    return 1;
                }
                case 2: {
                    Iterator left = this.iterator();
                    Iterator right = ((ListItem)item).iterator();
                    while (left.hasNext() || right.hasNext()) {
                        Item l = left.hasNext() ? (Item)left.next() : null;
                        Item r = right.hasNext() ? (Item)right.next() : null;
                        int result = l == null ? -1 * r.compareTo(l) : l.compareTo(r);
                        if (result == 0) continue;
                        return result;
                    }
                    return 0;
                }
            }
            throw new RuntimeException("invalid item: " + item.getClass());
        }

        @Override
        public String toString() {
            StringBuffer buffer = new StringBuffer("(");
            Iterator iter = this.iterator();
            while (iter.hasNext()) {
                buffer.append(iter.next());
                if (!iter.hasNext()) continue;
                buffer.append(',');
            }
            buffer.append(')');
            return buffer.toString();
        }
    }

    private static class StringItem
    implements Item {
        private static final String[] QUALIFIERS = new String[]{"snapshot", "alpha", "beta", "milestone", "rc", "", "sp"};
        private static final List<String> _QUALIFIERS = Arrays.asList(QUALIFIERS);
        private static final Properties ALIASES = new Properties();
        private static String RELEASE_VERSION_INDEX;
        private String value;

        public StringItem(String value, boolean followedByDigit) {
            if (followedByDigit && value.length() == 1) {
                switch (value.charAt(0)) {
                    case 'a': {
                        value = "alpha";
                        break;
                    }
                    case 'b': {
                        value = "beta";
                        break;
                    }
                    case 'm': {
                        value = "milestone";
                    }
                }
            }
            this.value = ALIASES.getProperty(value, value);
        }

        @Override
        public int getType() {
            return 1;
        }

        @Override
        public boolean isNull() {
            return StringItem.comparableQualifier(this.value).compareTo(RELEASE_VERSION_INDEX) == 0;
        }

        public static String comparableQualifier(String qualifier) {
            int i = _QUALIFIERS.indexOf(qualifier);
            return i == -1 ? "" + _QUALIFIERS.size() + "-" + qualifier : String.valueOf(i);
        }

        @Override
        public int compareTo(Item item) {
            if (item == null) {
                return StringItem.comparableQualifier(this.value).compareTo(RELEASE_VERSION_INDEX);
            }
            switch (item.getType()) {
                case 0: {
                    return -1;
                }
                case 1: {
                    return StringItem.comparableQualifier(this.value).compareTo(StringItem.comparableQualifier(((StringItem)item).value));
                }
                case 2: {
                    return -1;
                }
            }
            throw new RuntimeException("invalid item: " + item.getClass());
        }

        public String toString() {
            return this.value;
        }

        static {
            ALIASES.put("ga", "");
            ALIASES.put("final", "");
            ALIASES.put("cr", "rc");
            RELEASE_VERSION_INDEX = String.valueOf(_QUALIFIERS.indexOf(""));
        }
    }

    private static class IntegerItem
    implements Item {
        private Integer value;

        public IntegerItem(Integer i) {
            this.value = i;
        }

        @Override
        public int getType() {
            return 0;
        }

        @Override
        public boolean isNull() {
            return this.value == 0;
        }

        @Override
        public int compareTo(Item item) {
            if (item == null) {
                return this.value == 0 ? 0 : 1;
            }
            switch (item.getType()) {
                case 0: {
                    return this.value.compareTo(((IntegerItem)item).value);
                }
                case 1: {
                    return 1;
                }
                case 2: {
                    return 1;
                }
            }
            throw new RuntimeException("invalid item: " + item.getClass());
        }

        public String toString() {
            return this.value.toString();
        }
    }

    private static interface Item {
        public static final int INTEGER_ITEM = 0;
        public static final int STRING_ITEM = 1;
        public static final int LIST_ITEM = 2;

        public int compareTo(Item var1);

        public int getType();

        public boolean isNull();
    }

}

