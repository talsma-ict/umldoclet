/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.preproc;

import java.io.IOException;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.utils.StartUtils;

public class UncommentReadLine extends ReadLineInstrumented implements ReadLine {

	private static final Pattern2 unpause = MyPattern.cmpile(StartUtils.PAUSE_PATTERN);

	private final ReadLine raw;
	private String headerToRemove;
	private boolean paused;

	public UncommentReadLine(ReadLine source) {
		this.raw = source;
	}

	@Override
	public String toString() {
		return "UncommentReadLine of " + raw;
	}

	@Override
	StringLocated readLineInst() throws IOException {
		final StringLocated result = raw.readLine();

		if (result == null) {
			return null;
		}

		final String tmp = StartUtils.beforeStartUml(result.getString());
		if (tmp != null) {
			headerToRemove = tmp;
		}
		if (paused) {
			final Matcher2 m2 = unpause.matcher(result.getString());
			if (m2.find()) {
				headerToRemove = m2.group(1);
			}
		}
		if (headerToRemove != null && headerToRemove.startsWith(result.getString())) {
			return new StringLocated("", result.getLocation());
		}
		if (headerToRemove != null && result.getString().startsWith(headerToRemove)) {
			return result.sub(headerToRemove.length(), result.getString().length());
		}
		return result;
	}

	@Override
	void closeInst() throws IOException {
		this.raw.close();
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

}
