/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher;

import com.google.common.base.Supplier;
import com.skcraft.launcher.FancyLauncherFrame;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.swing.SwingHelper;
import java.awt.Window;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class FancyLauncher {
    private static final Logger log = Logger.getLogger(FancyLauncher.class.getName());

    public static void main(final String[] args) {
        Launcher.setupLogger();
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                try {
                    Thread.currentThread().setContextClassLoader(FancyLauncher.class.getClassLoader());
                    UIManager.getLookAndFeelDefaults().put("ClassLoader", FancyLauncher.class.getClassLoader());
                    UIManager.getDefaults().put("SplitPane.border", BorderFactory.createEmptyBorder());
                    if (!SwingHelper.setLookAndFeel("com.skcraft.launcher.skin.LauncherLookAndFeel")) {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    }
                    Launcher launcher = Launcher.createFromArguments(FancyLauncher.class, args);
                    launcher.setMainWindowSupplier(new CustomWindowSupplier(launcher));
                    launcher.showLauncherWindow();
                }
                catch (Throwable t) {
                    log.log(Level.WARNING, "Load failure", t);
                    SwingHelper.showErrorDialog(null, "Uh oh! The updater couldn't be opened because a problem was encountered.", "Launcher error", t);
                }
            }
        });
    }

    private static class CustomWindowSupplier
    implements Supplier<Window> {
        private final Launcher launcher;

        private CustomWindowSupplier(Launcher launcher) {
            this.launcher = launcher;
        }

        @Override
        public Window get() {
            return new FancyLauncherFrame(this.launcher);
        }
    }

}

