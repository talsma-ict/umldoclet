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
package net.sourceforge.plantuml.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.graphic.FontStyle;

public class StyledString {

	private final String text;
	private final FontStyle style;

	private StyledString(String text, FontStyle style) {
		this.text = text;
		this.style = style;
	}

	@Override
	public String toString() {
		return style + "[" + text + "]";
	}

	public final String getText() {
		return text;
	}

	public final FontStyle getStyle() {
		return style;
	}

	public static List<StyledString> build(String s) {
		final List<StyledString> result = new ArrayList<StyledString>();
		while (s.length() > 0) {
			final int i1 = s.indexOf(StringUtils.BOLD_START);
			if (i1 == -1) {
				result.add(new StyledString(s, FontStyle.PLAIN));
				s = "";
				break;
			}
			final int i2 = s.indexOf(StringUtils.BOLD_END);
			if (i1 > 0)
				result.add(new StyledString(s.substring(0, i1), FontStyle.PLAIN));

			if (i2 == -1) {
				result.add(new StyledString(s.substring(i1 + 1), FontStyle.BOLD));
				s = "";
			} else {
				result.add(new StyledString(s.substring(i1 + 1, i2), FontStyle.BOLD));
				s = s.substring(i2 + 1);
			}
		}
		return Collections.unmodifiableList(result);

	}

}
