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
package net.sourceforge.plantuml.activitydiagram3;

import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.Rainbow;

public class LinkRendering {

	private final Rainbow rainbow;
	private final Display display;

	public LinkRendering(Rainbow rainbow) {
		this(rainbow, Display.NULL);
	}

	private LinkRendering(Rainbow rainbow, Display display) {
		if (rainbow == null) {
			throw new IllegalArgumentException();
		}
		this.rainbow = rainbow;
		this.display = display;
	}

	public static LinkRendering none() {
		return new LinkRendering(Rainbow.none());
	}

	public LinkRendering withDisplay(Display display) {
		return new LinkRendering(rainbow, display);
	}

	public Display getDisplay() {
		return display;
	}

	public Rainbow getRainbow() {
		return rainbow;
	}

	public Rainbow getRainbow(Rainbow defaultColor) {
		if (rainbow.size() == 0) {
			return defaultColor;
		}
		return rainbow;
	}

	public boolean isNone() {
		return Display.isNull(display) && rainbow.size() == 0;
	}

	@Override
	public String toString() {
		return super.toString() + " " + display + " " + rainbow;
	}
}
