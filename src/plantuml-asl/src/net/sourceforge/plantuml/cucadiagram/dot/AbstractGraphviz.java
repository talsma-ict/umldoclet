/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.cucadiagram.dot;

import java.io.File;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.security.SFile;

abstract class AbstractGraphviz implements Graphviz {

	private final File dotExe;
	private final String dotString;
	private final String[] type;

	static boolean isWindows() {
		return File.separatorChar == '\\';
	}

	private static String findExecutableOnPath(String name) {
		final String path = System.getenv("PATH");
		if (path != null) {
			for (String dirname : path.split(SFile.pathSeparator)) {
				final File file = new File(dirname, name);
				if (file.isFile() && file.canExecute()) {
					return file.getAbsolutePath();
				}
			}
		}
		return null;
	}

	AbstractGraphviz(ISkinParam skinParam, String dotString, String... type) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		this.dotExe = searchDotExe();
		this.dotString = dotString;
		this.type = type;
	}
	
	protected boolean findExecutableOnPath() {
		return true;
	}

	final protected File searchDotExe() {
		String getenv = GraphvizUtils.getenvGraphvizDot();
		if (findExecutableOnPath() && getenv == null) {
			getenv = findExecutableOnPath(getExeName());
		}
		if (getenv == null) {
			return specificDotExe();
		}
		return new File(getenv);
	}

	abstract protected File specificDotExe();

	abstract protected String getExeName();

	final public ProcessState createFile3(OutputStream os) {
		if (dotString == null) {
			throw new IllegalArgumentException();
		}

		if (getExeState() != ExeState.OK) {
			// createPngNoGraphviz(os, new
			// FileFormatOption(FileFormat.valueOf(type[0].goUpperCase())));
			throw new IllegalStateException();
		}
		final String cmd[] = getCommandLine();
		ProcessRunner p = null;
		ProcessState state = null;
		try {
			Log.info("Starting Graphviz process " + Arrays.asList(cmd));
			Log.info("DotString size: " + dotString.length());
			p = new ProcessRunner(cmd);
			state = p.run(dotString.getBytes(), os);
			// if (state == ProcessState.TERMINATED_OK) {
			// result = true;
			// }
			Log.info("Ending process ok");
		} catch (Throwable e) {
			e.printStackTrace();
			Log.error("Error: " + e);
			Log.error("The command was " + cmd);
			Log.error("");
			Log.error("Try java -jar plantuml.jar -testdot to figure out the issue");
			Log.error("");
		} finally {
			Log.info("Ending Graphviz process");
		}
		if (OptionFlags.getInstance().isCheckDotError() && p != null && p.getError().length() > 0) {
			Log.error("GraphViz error stream : " + p.getError());
			if (OptionFlags.getInstance().isCheckDotError()) {
				throw new IllegalStateException("Dot error " + p.getError());
			}
		}
		if (OptionFlags.getInstance().isCheckDotError() && p != null && p.getOut().length() > 0) {
			Log.error("GraphViz out stream : " + p.getOut());
			if (OptionFlags.getInstance().isCheckDotError()) {
				throw new IllegalStateException("Dot out " + p.getOut());
			}
		}
		return state;
	}

	final public ExeState getExeState() {
		return ExeState.checkFile(dotExe);
	}

	final public String dotVersion() {
		final String cmd[] = getCommandLineVersion();
		return executeCmd(cmd);
	}

	private String executeCmd(final String cmd[]) {
		final ProcessRunner p = new ProcessRunner(cmd);
		final ProcessState state = p.run(null, null);
		if (state.differs(ProcessState.TERMINATED_OK())) {
			return "?";
		}
		final StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotEmpty(p.getOut())) {
			sb.append(p.getOut());
		}
		if (StringUtils.isNotEmpty(p.getError())) {
			if (sb.length() > 0) {
				sb.append(' ');
			}
			sb.append(p.getError());
		}
		return StringUtils.trin(sb.toString().replace('\n', ' '));
	}

	final String[] getCommandLine() {
		if (OptionFlags.ADD_NICE_FOR_DOT) {
			final String[] result = new String[type.length + 1 + 3];
			result[0] = "/bin/nice";
			result[1] = "-n";
			result[2] = "10";
			result[3] = getDotExe().getAbsolutePath();
			for (int i = 0; i < type.length; i++) {
				result[i + 4] = "-T" + type[i];
			}
			return result;
		}
		final String[] result = new String[type.length + 1];
		result[0] = getDotExe().getAbsolutePath();
		for (int i = 0; i < type.length; i++) {
			result[i + 1] = "-T" + type[i];
		}
		return result;
	}

	final String[] getCommandLineVersion() {
		return new String[] { getDotExe().getAbsolutePath(), "-V" };
	}

	public final File getDotExe() {
		return dotExe;
	}

	public final String getDotString() {
		return dotString;
	}

	public final List<String> getType() {
		return Arrays.asList(type);
	}

}
