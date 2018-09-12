/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.skcraft.launcher.auth;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skcraft.launcher.auth.Account;
import com.skcraft.launcher.persistence.Scrambled;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import lombok.NonNull;

@Scrambled(value="ACCOUNT_LIST")
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE, fieldVisibility=JsonAutoDetect.Visibility.NONE)
public class AccountList
extends AbstractListModel<Account>
implements ComboBoxModel<Account> {
    @JsonProperty
    private List<Account> accounts = new ArrayList<Account>();
    private transient Account selected;

    public synchronized void add(@NonNull Account account) {
        if (account == null) {
            throw new NullPointerException("account");
        }
        if (!this.accounts.contains(account)) {
            this.accounts.add(account);
            Collections.sort(this.accounts);
            this.fireContentsChanged(this, 0, this.accounts.size());
        }
    }

    public synchronized void remove(@NonNull Account account) {
        if (account == null) {
            throw new NullPointerException("account");
        }
        Iterator<Account> it = this.accounts.iterator();
        while (it.hasNext()) {
            Account other = it.next();
            if (!other.equals(account)) continue;
            it.remove();
            this.fireContentsChanged(this, 0, this.accounts.size() + 1);
            break;
        }
    }

    public synchronized void setAccounts(@NonNull List<Account> accounts) {
        if (accounts == null) {
            throw new NullPointerException("accounts");
        }
        this.accounts = accounts;
        Collections.sort(accounts);
    }

    @JsonIgnore
    @Override
    public synchronized int getSize() {
        return this.accounts.size();
    }

    @Override
    public synchronized Account getElementAt(int index) {
        try {
            return this.accounts.get(index);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public void setSelectedItem(Object item) {
        if (item == null) {
            this.selected = null;
            return;
        }
        if (item instanceof Account) {
            this.selected = (Account)item;
        } else {
            String id = String.valueOf(item).trim();
            Account account = new Account(id);
            for (Account test : this.accounts) {
                if (!test.equals(account)) continue;
                account = test;
                break;
            }
            this.selected = account;
        }
        if (this.selected.getId() == null || this.selected.getId().isEmpty()) {
            this.selected = null;
        }
    }

    @JsonIgnore
    @Override
    public Account getSelectedItem() {
        return this.selected;
    }

    public synchronized void forgetPasswords() {
        for (Account account : this.accounts) {
            account.setPassword(null);
        }
    }

    public List<Account> getAccounts() {
        return this.accounts;
    }
}

