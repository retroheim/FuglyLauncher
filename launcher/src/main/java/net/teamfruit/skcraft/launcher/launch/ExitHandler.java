package net.teamfruit.skcraft.launcher.launch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.skcraft.launcher.LauncherUtils;
import com.skcraft.launcher.swing.MessageLog;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.util.SharedLocale;

import lombok.extern.java.Log;

@Log
public class ExitHandler {
	private String[] logLines;

	public ExitHandler(int exitcode, MessageLog log) {
		if (exitcode!=0) {
			String logString = log.getPastableText();
			this.logLines = logString.split("\n");
		}
	}

	public boolean handleRestart() {
		if (logLines!=null) {
			boolean restartRequired = false;

			for (int file = logLines.length-1; file>=0; --file) {
				String inputStream = logLines[file];
				int e = inputStream.lastIndexOf("#!@!#");

				if (e>=0&&e<inputStream.length()-"#!@!#".length()-1) {
					String restartText = inputStream.substring(e+"#!@!#".length()).trim();
					if ("RESTART".equalsIgnoreCase(restartText)) {
						restartRequired = true;
						break;
					}
				}
			}

			return restartRequired;
		}
		return false;
	}

	public boolean handleCrashReport() {
		if (logLines!=null) {
			String errorText = null;

			for (int file = logLines.length-1; file>=0; --file) {
				String inputStream = logLines[file];
				int e = inputStream.lastIndexOf("#@!@#");

				if (e>=0&&e<inputStream.length()-"#@!@#".length()-1) {
					errorText = inputStream.substring(e+"#@!@#".length()).trim();
					break;
				}
			}

			if (errorText!=null) {
				File errorFile = new File(errorText);

				if (errorFile.isFile()) {
					log.info("Crash report detected, opening: "+errorText);
					FileInputStream input = null;

					try {
						input = new FileInputStream(errorFile);
						BufferedReader reader = new BufferedReader(new InputStreamReader(input));
						StringBuilder result;
						String line;
						for (result = new StringBuilder(); (line = reader.readLine())!=null; result.append(line))
							if (result.length()>0)
								result.append("\n");

						reader.close();

	                    SwingHelper.showMessageDialog(null, SharedLocale.tr("runner.crashMinecraft", errorFile.getName()),
	                    		SharedLocale.tr("runner.crashMinecraftTitle"), result.toString(), JOptionPane.ERROR_MESSAGE);

					} catch (IOException arg13) {
						log.log(Level.WARNING, "Couldn\'t open crash report", arg13);
					} finally {
						LauncherUtils.closeQuietly(input);
					}
				} else
					log.log(Level.WARNING, "Crash report detected, but unknown format: "+errorText);

				return true;
			}
		}
		return false;
	}

}
