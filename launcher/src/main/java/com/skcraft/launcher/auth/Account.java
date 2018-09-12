/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.skcraft.launcher.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Strings;
import java.util.Date;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Account
implements Comparable<Account> {
    private String id;
    private String password;
    private Date lastUsed;

    public Account() {
    }

    public Account(String id) {
        this.setId(id);
    }

    public void setPassword(String password) {
        if (password != null && password.isEmpty()) {
            password = null;
        }
        this.password = Strings.emptyToNull(password);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Account account = (Account)o;
        if (!this.id.equalsIgnoreCase(account.id)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.id.toLowerCase().hashCode();
    }

    @Override
    public int compareTo(@NonNull Account o) {
        if (o == null) {
            throw new NullPointerException("o");
        }
        Date otherDate = o.getLastUsed();
        if (otherDate == null && this.lastUsed == null) {
            return 0;
        }
        if (otherDate == null) {
            return -1;
        }
        if (this.lastUsed == null) {
            return 1;
        }
        return - this.lastUsed.compareTo(otherDate);
    }

    public String toString() {
        return this.getId();
    }

    public String getId() {
        return this.id;
    }

    public String getPassword() {
        return this.password;
    }

    public Date getLastUsed() {
        return this.lastUsed;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }
}

