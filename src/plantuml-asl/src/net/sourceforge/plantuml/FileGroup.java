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
package net.sourceforge.plantuml;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;

public class FileGroup {

	private final List<File> result = new ArrayList<File>();
	private final String pattern;
	private final List<String> excluded;
	private final Option option;

	private final static Pattern2 predirPath = MyPattern.cmpile("^([^*?]*[/\\\\])?(.*)$");

	public FileGroup(String pattern, List<String> excluded, Option option) {
		this.pattern = pattern;
		this.excluded = excluded;
		this.option = option;
		if (pattern.indexOf("*") == -1 && pattern.indexOf("?") == -1) {
			initNoStar();
		} else if (pattern.indexOf("**") != -1) {
			recurse();
		} else {
			initWithSimpleStar();
		}
		Collections.sort(result);

	}

	private void recurse() {
		final Matcher2 m = predirPath.matcher(pattern);
		final boolean ok = m.find();
		if (ok == false) {
			throw new IllegalArgumentException();
		}
		final File parent;
		if (m.group(1) == null) {
			parent = new File(".");
		} else {
			parent = new File(m.group(1));
		}
		initWithDoubleStar(parent);
	}

	private void initNoStar() {
		final File f = new File(pattern);
		if (f.isDirectory()) {
			addSimpleDirectory(f);
		} else if (f.isFile()) {
			addResultFile(f);
		}
	}

	private void addResultFile(final File f) {
		final String path = getNormalizedPath(f);
		for (String x : excluded) {
			if (path.matches(toRegexp(x))) {
				return;
			}
		}
		result.add(f);
	}

	private void addSimpleDirectory(File dir) {
		if (OptionFlags.getInstance().isWord()) {
			addSimpleDirectory(dir, "(?i)^.*_extr\\d+\\.txt$");
		} else {
			addSimpleDirectory(dir, option.getPattern());
		}
	}

	private void addSimpleDirectory(File dir, String pattern) {
		if (dir.isDirectory() == false) {
			throw new IllegalArgumentException("dir=" + dir);
		}
		for (File f : dir.listFiles()) {
			if (f.getName().matches(pattern)) {
				addResultFile(f);
			}
		}
	}

	private static String getNormalizedPath(File f) {
		return f.getPath().replace('\\', '/');
	}

	private final static Pattern2 noStarInDirectory = MyPattern.cmpile("^(?:([^*?]*)[/\\\\])?([^/\\\\]*)$");

	private void initWithSimpleStar() {
		assert pattern.indexOf("**") == -1;
		final Matcher2 m = noStarInDirectory.matcher(pattern);
		if (m.find()) {
			File dir = new File(".");
			if (m.group(1) != null) {
				final String dirPart = m.group(1);
				dir = new File(dirPart);
			}

			final String filesPart = m.group(2);
			addSimpleDirectory(dir, toRegexp(filesPart));
		} else {
			recurse();
		}

	}

	private void initWithDoubleStar(File currentDir) {
		for (File f : currentDir.listFiles()) {
			if (f.isDirectory()) {
				initWithDoubleStar(f);
			} else if (f.isFile()) {
				final String path = getNormalizedPath(f);
				if (path.matches(toRegexp(pattern))) {
					addResultFile(f);
				}

			}
		}

	}

	public List<File> getFiles() {
		return Collections.unmodifiableList(result);
	}

	public static String toRegexp(String pattern) {
		pattern = pattern.replace("\\", "/");
		pattern = pattern.replace(".", "\\.");
		pattern = pattern.replace("?", "[^/]");
		pattern = pattern.replace("/**/", "(/|/.{0,}/)");
		pattern = pattern.replace("**", ".{0,}");
		pattern = pattern.replace("*", "[^/]{0,}");
		pattern = "(?i)^(\\./)?" + pattern + "$";
		return pattern;
	}

}
