/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher.persistence;

import com.google.common.io.ByteSource;
import com.skcraft.launcher.persistence.ScramblingSinkFilter;
import java.io.IOException;
import java.io.InputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

class ScramblingSourceFilter
extends ByteSource {
    private final ByteSource delegate;
    private final String key;

    public ScramblingSourceFilter(ByteSource delegate, String key) {
        this.delegate = delegate;
        this.key = key;
    }

    @Override
    public InputStream openStream() throws IOException {
        Cipher cipher = null;
        try {
            cipher = ScramblingSinkFilter.getCipher(2, this.key);
        }
        catch (Throwable e) {
            throw new IOException("Failed to create cipher", e);
        }
        return new CipherInputStream(this.delegate.openStream(), cipher);
    }
}

