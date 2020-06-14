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
package net.sourceforge.plantuml.style;

import java.util.EnumMap;
import java.util.Map;

public class FromSkinparamToStyle {

	private Style style;

	public FromSkinparamToStyle(String key, String value, AutomaticCounter counter) {
		final Map<PName, Value> map = new EnumMap<PName, Value>(PName.class);
		SName styleName = null;
		if (key.equalsIgnoreCase("participantBackgroundColor")) {
			styleName = SName.participant;
			map.put(PName.BackGroundColor, new ValueImpl(value, counter));
		} else if (key.equalsIgnoreCase("SequenceLifeLineBorderColor")) {
			styleName = SName.lifeLine;
			map.put(PName.LineColor, new ValueImpl(value, counter));
		} else if (key.equalsIgnoreCase("noteBackgroundColor")) {
			styleName = SName.note;
			map.put(PName.BackGroundColor, new ValueImpl(value, counter));
		} else if (key.equalsIgnoreCase("arrowColor")) {
			styleName = SName.message;
			map.put(PName.LineColor, new ValueImpl(value, counter));
		} else if (key.equalsIgnoreCase("arrowFontColor")) {
			styleName = SName.message;
			map.put(PName.FontColor, new ValueImpl(value, counter));
		} else if (key.equalsIgnoreCase("noteFontColor")) {
			styleName = SName.note;
			map.put(PName.FontColor, new ValueImpl(value, counter));
		} else if (key.equalsIgnoreCase("defaulttextalignment")) {
			styleName = SName.root;
			map.put(PName.HorizontalAlignment, new ValueImpl(value, counter));
		} else if (key.equalsIgnoreCase("defaultFontName")) {
			styleName = SName.root;
			map.put(PName.FontName, new ValueImpl(value, counter));
		}
		if (styleName != null && map.size() > 0) {
			style = new Style(StyleKind.STYLE, styleName.name(), map);
		}
	}

	public Style getStyle() {
		return style;
	}

}
