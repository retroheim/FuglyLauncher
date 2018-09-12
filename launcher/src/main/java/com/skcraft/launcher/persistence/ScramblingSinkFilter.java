/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher.persistence;

import com.google.common.io.ByteSink;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

class ScramblingSinkFilter
extends ByteSink {
    private final ByteSink delegate;
    private final String key;

    public ScramblingSinkFilter(ByteSink delegate, String key) {
        this.delegate = delegate;
        this.key = key;
    }

    @Override
    public OutputStream openStream() throws IOException {
        Cipher cipher = null;
        try {
            cipher = ScramblingSinkFilter.getCipher(1, this.key);
        }
        catch (Throwable e) {
            throw new IOException("Failed to create cipher", e);
        }
        return new CipherOutputStream(this.delegate.openStream(), cipher);
    }

    public static Cipher getCipher(int mode, String password) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        Random random = new Random(43287234L);
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 5);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = factory.generateSecret(new PBEKeySpec(password.toCharArray()));
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(mode, (Key)key, paramSpec);
        return cipher;
    }
}

