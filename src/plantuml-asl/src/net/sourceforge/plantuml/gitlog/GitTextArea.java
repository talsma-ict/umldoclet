/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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
package net.sourceforge.plantuml.gitlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GitTextArea {

	private final List<String> lines = new ArrayList<>();
	private final List<Commit> commits = new ArrayList<>();

	public void add(String s) {
		lines.add(s);
	}

	public List<Commit> getAllCommits() {
		if (commits.size() == 0)
			for (int y = 0; y < lines.size(); y++) {
				String s = lines.get(y);
				final String name = CursorPosition.getCommitNameInLine(s);
				final int x = s.indexOf("*");
				assert (name == null) == (x == -1);
				if (x == -1) {
					continue;
				}
				commits.add(new Commit(name, new CursorPosition(this, x, y)));
			}

		return Collections.unmodifiableList(commits);
	}

	public char charAt(int x, int y) {
		return lines.get(y).charAt(x);
	}

	public String getLine(int y) {
		if (y >= lines.size()) {
			return "";
		}
		return lines.get(y);
	}

	public Commit getCommitByName(String name) {
		for (Commit commit : getAllCommits()) {
			if (commit.getName().equals(name)) {
				return commit;
			}
		}
		return null;
	}

}
