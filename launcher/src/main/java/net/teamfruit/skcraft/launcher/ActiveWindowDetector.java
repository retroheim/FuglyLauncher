package net.teamfruit.skcraft.launcher;

import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import net.teamfruit.skcraft.launcher.ActiveWindowDetector.WindowResult.WindowResultBuilder;

/**
 * respecting https://stackoverflow.com/questions/5206633/find-out-what-application-window-is-in-focus-in-java
 */
public class ActiveWindowDetector {
	public interface Psapi extends StdCallLibrary {
		WinDef.DWORD GetModuleBaseNameW(Pointer hProcess, Pointer hModule, byte[] lpBaseName, int nSize);
	}

	private static class PsapiLoader {
		public static final Psapi INSTANCE;

		static {
			Psapi inst = null;
			try {
				inst = (Psapi) Native.loadLibrary("Psapi", Psapi.class);
			} catch (Throwable t) {
				Log.log.log(Level.WARNING, "could not detect active window: ", t);
			}
			INSTANCE = inst;
		}
	}

	@Builder
	@Data
	public static class WindowResult {
		@Default
		private final String windowname = "";
		@Default
		private final String filename = "";
	}

	public static WindowResult detectWindow() {
		WindowResultBuilder builder = WindowResult.builder();
		if (Platform.isWindows()) {
			final int PROCESS_VM_READ = 0x0010;
			final int PROCESS_QUERY_INFORMATION = 0x0400;
			final User32 user32 = User32.INSTANCE;
			final Kernel32 kernel32 = Kernel32.INSTANCE;

			WinDef.HWND windowHandle = user32.GetForegroundWindow();

			char[] windowname = new char[512];
			user32.GetWindowText(windowHandle, windowname, windowname.length);
			builder.windowname(new String(windowname));

			builder.filename(WindowUtils.getProcessFilePath(windowHandle));

//			IntByReference pid = new IntByReference();
//			user32.GetWindowThreadProcessId(windowHandle, pid);
//			WinNT.HANDLE processHandle = kernel32.OpenProcess(PROCESS_VM_READ|PROCESS_QUERY_INFORMATION, true, pid.getValue());
//
//			if (processHandle!=null) {
//				byte[] filename = new byte[512];
//				PsapiLoader.INSTANCE.GetModuleBaseNameW(processHandle.getPointer(), Pointer.NULL, filename, filename.length);
//				builder.filename(new String(filename));
//			}
		} else if (Platform.isLinux()) {
			final X11 x11 = WMCtrl.x11;
			X11.Display display = x11.XOpenDisplay(null);

			X11.Window window = WMCtrl.get_active_window(display);

			X11.XTextProperty windownameproperty = new X11.XTextProperty();
			x11.XGetWMName(display, window, windownameproperty);
			builder.windowname(windownameproperty.value);

			int windowpid = WMCtrl.get_window_pid(display, window);
			try {
				String filename = Paths.get(String.format("/proc/%d/exe", windowpid)).toRealPath().toString();
				builder.filename(filename);
			} catch (IOException e) {
				Log.log.log(Level.WARNING, "could not detect active window: ", e);
			}

			x11.XCloseDisplay(display);

			/*
			xwininfo -id $(xprop -root | awk '/NET_ACTIVE_WINDOW/ { print $5; exit }') | awk -F\" '/xwininfo:/ { print $2; exit }'
			readlink -e /proc/$(xprop -id $(xprop -root | awk '/NET_ACTIVE_WINDOW/ { print $5; exit }') _NET_WM_PID | awk '{ print $3; exit }')/exe | awk '{ print $NF; exit }'
			 */
			/*
			try {
				String[] command = { "bash", "-c", "xwininfo -id $(xprop -root | awk '/NET_ACTIVE_WINDOW/ { print $5; exit }') | awk -F\\\" '/xwininfo:/ { print $2; exit }'" };
				Process process = new ProcessBuilder().command(command).start();
				try {
					if (process.waitFor()==0) {
						StringWriter writer = new StringWriter();
						IOUtils.copy(process.getInputStream(), writer);
						builder.windowname(writer.toString());
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			} catch (IOException e) {
				Log.log.log(Level.WARNING, "could not detect active window: ", e);
			}
			try {
				String[] command = { "bash", "-c", "readlink -e /proc/$(xprop -id $(xprop -root | awk '/NET_ACTIVE_WINDOW/ { print $5; exit }') _NET_WM_PID | awk '{ print $3; exit }')/exe | awk '{ print $NF; exit }'" };
				Process process = new ProcessBuilder().command(command).start();
				try {
					if (process.waitFor()==0) {
						StringWriter writer = new StringWriter();
						IOUtils.copy(process.getInputStream(), writer);
						builder.filename(writer.toString());
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			} catch (IOException e) {
				Log.log.log(Level.WARNING, "could not detect active window: ", e);
			}
			*/
		} else if (Platform.isMac()) {
			final String scriptwindowname = "tell application \"System Events\"\n" +
					"    # Get the frontmost app's *process* object.\n" +
					"    set frontAppProcess to first application process whose frontmost is true\n" +
					"end tell\n" +
					"\n" +
					"# Tell the *process* to count its windows and return its front window's name.\n" +
					"tell frontAppProcess\n" +
					"    set window_name to name of front window\n" +
					"end tell";
			final String scriptfilename = "tell application \"System Events\"\n"+
					"\tname of application processes whose frontmost is true\n"+
					"end";
			if (appleScript==null)
				Log.log.log(Level.WARNING, "could not detect active window: no engine");
			else {
				try {
					builder.windowname((String) getAppleScript().eval(scriptwindowname));
				} catch (ScriptException e) {
					Log.log.log(Level.WARNING, "could not detect active window: ", e);
				}
				try {
					builder.filename((String) getAppleScript().eval(scriptfilename).toString());
				} catch (ScriptException e) {
					Log.log.log(Level.WARNING, "could not detect active window: ", e);
				}
			}
		}
		return builder.build();
	}

	@Getter(lazy = true, value = AccessLevel.PRIVATE)
	private static final ScriptEngine appleScript = new ScriptEngineManager().getEngineByName("AppleScriptEngine");

	public static void main(String[] args) {
		final JFrame jf = new JFrame("Test");
		final JPanel jp = new JPanel(new BorderLayout());
		final JTextArea jl = new JTextArea("App");
		jl.setEditable(false);

		Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					//Log.log.info(com.sun.jna.Version.);
					WindowResult detect = detectWindow();
					Log.log.info("Detect: ["+detect.getFilename()+"]: "+detect.getWindowname());
					jl.setText("["+detect.getFilename()+"]: "+detect.getWindowname());
				} catch (Throwable e) {
					e.printStackTrace();
					jl.setText(ExceptionUtils.getFullStackTrace(e));
				}
			}
		}, 0, 2, TimeUnit.SECONDS);

		jp.add(jl, BorderLayout.CENTER);
		jf.add(jp);

		jf.pack();
		jf.setSize(500, 200);
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
}
