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
package net.sourceforge.plantuml.anim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class AnimationDecoder {

	private final List<String> result = new ArrayList<>();

	public AnimationDecoder(Iterable<CharSequence> data) {

		for (final Iterator<CharSequence> it = data.iterator(); it.hasNext();) {
			String line = it.next().toString();
			if (line.matches("^\\s*\\[script\\]\\s*$")) {
				final StringBuilder scriptText = new StringBuilder();
				while (true) {
					line = it.next().toString();
					if (line.matches("^\\s*\\[/script\\]\\s*$")) {
						final AnimationScript script = new AnimationScript();
						final String out = script.eval(scriptText.toString());
						for (final StringTokenizer st = new StringTokenizer(out, "\n"); st.hasMoreTokens();) {
							result.add(st.nextToken());
						}
						break;
					} else {
						scriptText.append(line);
						scriptText.append("\n");
					}
				}
			} else {
				result.add(line);
			}
		}
	}

	public List<String> decode() {
		return Collections.unmodifiableList(result);
	}

}
