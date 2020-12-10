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
package net.sourceforge.plantuml.activitydiagram3;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileKilled;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.color.Colors;

public class InstructionSimple extends MonoSwimable implements Instruction {

	private boolean killed = false;
	private final Display label;
	private final Colors colors;
	private final LinkRendering inlinkRendering;
	private final BoxStyle style;
	private final Url url;
	private final Stereotype stereotype;

	public boolean containsBreak() {
		return false;
	}

	public InstructionSimple(Display label, LinkRendering inlinkRendering, Swimlane swimlane, BoxStyle style, Url url,
			Colors colors, Stereotype stereotype) {
		super(swimlane);
		if (colors == null) {
			throw new IllegalArgumentException();
		}
		this.stereotype = stereotype;
		this.url = url;
		this.style = style;
		this.label = label;
		this.inlinkRendering = inlinkRendering;
		if (inlinkRendering == null) {
			throw new IllegalArgumentException();
		}
		this.colors = colors;
	}

	public Ftile createFtile(FtileFactory factory) {
		Ftile result = factory.activity(label, getSwimlaneIn(), style, colors, stereotype);
		if (url != null) {
			result = factory.addUrl(result, url);
		}
		result = eventuallyAddNote(factory, result, result.getSwimlaneIn());
		if (killed) {
			return new FtileKilled(result);
		}
		return result;
	}

	public void add(Instruction other) {
		throw new UnsupportedOperationException();
	}

	final public boolean kill() {
		this.killed = true;
		return true;
	}

	public LinkRendering getInLinkRendering() {
		return inlinkRendering;
	}

}
