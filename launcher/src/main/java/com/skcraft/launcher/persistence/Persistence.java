/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.skcraft.launcher.persistence;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Closer;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.skcraft.launcher.persistence.MkdirByteSink;
import com.skcraft.launcher.persistence.Scrambled;
import com.skcraft.launcher.persistence.ScramblingSinkFilter;
import com.skcraft.launcher.persistence.ScramblingSourceFilter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NonNull;

public final class Persistence {
    private static final Logger log = Logger.getLogger(Persistence.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final WeakHashMap<Object, ByteSink> bound = new WeakHashMap();
    public static final DefaultPrettyPrinter L2F_LIST_PRETTY_PRINTER = new DefaultPrettyPrinter();

    private Persistence() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void bind(@NonNull Object object, @NonNull ByteSink sink) {
        if (object == null) {
            throw new NullPointerException("object");
        }
        if (sink == null) {
            throw new NullPointerException("sink");
        }
        WeakHashMap<Object, ByteSink> weakHashMap = bound;
        synchronized (weakHashMap) {
            bound.put(object, sink);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void commit(@NonNull Object object) throws IOException {
        ByteSink sink;
        if (object == null) {
            throw new NullPointerException("object");
        }
        WeakHashMap<Object, ByteSink> weakHashMap = bound;
        synchronized (weakHashMap) {
            sink = bound.get(object);
            if (sink == null) {
                throw new IOException("Cannot persist unbound object: " + object);
            }
        }
        Closer closer = Closer.create();
        try {
            OutputStream os = closer.register(sink.openBufferedStream());
            mapper.writeValue(os, object);
        }
        finally {
            closer.close();
        }
    }

    public static void commitAndForget(@NonNull Object object) {
        if (object == null) {
            throw new NullPointerException("object");
        }
        try {
            Persistence.commit(object);
        }
        catch (IOException e) {
            log.log(Level.WARNING, "Failed to save " + object.getClass() + ": " + object.toString(), e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <V> V read(ByteSource source, Class<V> cls, boolean returnNull) {
        V object;
        Closer closer = Closer.create();
        try {
            object = mapper.readValue(closer.register(source.openBufferedStream()), cls);
        }
        catch (IOException e) {
            if (!(e instanceof FileNotFoundException)) {
                log.log(Level.INFO, "Failed to load" + cls.getCanonicalName(), e);
            }
            if (returnNull) {
                V v = null;
                return v;
            }
            try {
                object = cls.newInstance();
            }
            catch (InstantiationException e1) {
                throw new RuntimeException("Failed to construct object with no-arg constructor", e1);
            }
            catch (IllegalAccessException e1) {
                throw new RuntimeException("Failed to construct object with no-arg constructor", e1);
            }
        }
        finally {
            try {
                closer.close();
            }
            catch (IOException iOException) {}
        }
        return object;
    }

    public static <V> V read(File file, Class<V> cls, boolean returnNull) {
        return Persistence.read(Files.asByteSource(file), cls, returnNull);
    }

    public static <V> V read(File file, Class<V> cls) {
        return Persistence.read(file, cls, false);
    }

    public static <V> V load(File file, Class<V> cls, boolean returnNull) {
        ByteSource source = Files.asByteSource(file);
        ByteSink sink = new MkdirByteSink(Files.asByteSink(file, new FileWriteMode[0]), file.getParentFile());
        Scrambled scrambled = cls.getAnnotation(Scrambled.class);
        if (cls.getAnnotation(Scrambled.class) != null) {
            source = new ScramblingSourceFilter(source, scrambled.value());
            sink = new ScramblingSinkFilter(sink, scrambled.value());
        }
        V object = Persistence.read(source, cls, returnNull);
        Persistence.bind(object, sink);
        return object;
    }

    public static <V> V load(File file, Class<V> cls) {
        return Persistence.load(file, cls, false);
    }

    public static void write(File file, Object object) throws IOException {
        Persistence.write(file, object, null);
    }

    public static void write(File file, Object object, PrettyPrinter prettyPrinter) throws IOException {
        file.getParentFile().mkdirs();
        if (prettyPrinter != null) {
            mapper.writer(prettyPrinter).writeValue(file, object);
        } else {
            mapper.writeValue(file, object);
        }
    }

    public static String writeValueAsString(Object object, PrettyPrinter prettyPrinter) throws IOException {
        if (prettyPrinter != null) {
            return mapper.writer(prettyPrinter).writeValueAsString(object);
        }
        return mapper.writeValueAsString(object);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    static {
        L2F_LIST_PRETTY_PRINTER.indentArraysWith(DefaultPrettyPrinter.Lf2SpacesIndenter.instance);
    }
}

