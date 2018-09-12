/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.skcraft.concurrency.ObservableFuture;
import com.skcraft.concurrency.ProgressObservable;
import com.skcraft.launcher.Instance;
import com.skcraft.launcher.InstanceList;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.dialog.ProgressDialog;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.update.HardResetter;
import com.skcraft.launcher.update.Remover;
import com.skcraft.launcher.util.SharedLocale;
import java.awt.Window;
import java.util.concurrent.Callable;
import net.teamfruit.skcraft.launcher.TipList;

public class InstanceTasks {
    private final Launcher launcher;

    public InstanceTasks(Launcher launcher) {
        this.launcher = launcher;
    }

    public ObservableFuture<Instance> delete(Window window, Instance instance) {
        Remover resetter = new Remover(instance);
        ObservableFuture<Instance> future = new ObservableFuture<Instance>(this.launcher.getExecutor().submit(resetter), resetter);
        ProgressDialog.showProgress(window, future, SharedLocale.tr("instance.deletingTitle"), SharedLocale.tr("instance.deletingStatus", instance.getTitle()));
        SwingHelper.addErrorDialogCallback(window, future);
        return future;
    }

    public ObservableFuture<Instance> hardUpdate(Window window, Instance instance) {
        HardResetter resetter = new HardResetter(instance);
        ObservableFuture<Instance> future = new ObservableFuture<Instance>(this.launcher.getExecutor().submit(resetter), resetter);
        ProgressDialog.showProgress(window, future, SharedLocale.tr("instance.resettingTitle"), SharedLocale.tr("instance.resettingStatus", instance.getTitle()));
        SwingHelper.addErrorDialogCallback(window, future);
        return future;
    }

    public ObservableFuture<InstanceList> reloadInstances(Window window) {
        InstanceList.Enumerator loader = this.launcher.getInstances().createEnumerator();
        ObservableFuture<InstanceList> future = new ObservableFuture<InstanceList>(this.launcher.getExecutor().submit(loader), loader);
        ProgressDialog.showProgress(window, future, SharedLocale.tr("launcher.checkingTitle"), SharedLocale.tr("launcher.checkingStatus"));
        SwingHelper.addErrorDialogCallback(window, future);
        return future;
    }

    public ObservableFuture<TipList> reloadTips(Window window) {
        TipList.Enumerator loader = this.launcher.getTips().createEnumerator();
        ObservableFuture<TipList> future = new ObservableFuture<TipList>(this.launcher.getExecutor().submit(loader), loader);
        ProgressDialog.showProgress(window, future, SharedLocale.tr("launcher.checkingTipTitle"), SharedLocale.tr("launcher.checkingTipStatus"));
        SwingHelper.addErrorDialogCallback(window, future);
        return future;
    }
}

