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
package net.sourceforge.plantuml.graphic;

import net.sourceforge.plantuml.BackSlash;

public class Text implements HtmlCommand {

	private final String text;

	public static final Text TEXT_BS_BS_N = new Text(BackSlash.BS_BS_N);

	Text(String text) {
		this.text = text.replaceAll("\\\\\\[", "[").replaceAll("\\\\\\]", "]");
		if (text.indexOf(BackSlash.CHAR_NEWLINE) != -1) {
			throw new IllegalArgumentException();
		}
		if (text.length() == 0) {
			throw new IllegalArgumentException();
		}
	}

	public String getText() {
		assert text.length() > 0;
		return text;
	}

	public boolean isNewline() {
		return text.equals(BackSlash.BS_BS_N);
	}
}
