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
package net.sourceforge.plantuml.activitydiagram3.ftile.vertical;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.style.Styleable;
import net.sourceforge.plantuml.ugraphic.color.HColor;

abstract class FtileDiamondWIP extends AbstractFtile implements Styleable {

	protected final HColor backColor;
	protected final HColor borderColor;
	protected final Swimlane swimlane;

	protected final TextBlock label;

	protected final TextBlock north;
	protected final TextBlock south;
	protected final TextBlock west;
	protected final TextBlock east;

	protected final double shadowing;

	final public StyleSignature getDefaultStyleDefinition() {
		return StyleSignature.of(SName.root, SName.element, SName.activityDiagram, SName.activity, SName.diamond);
	}

	@Override
	final public Collection<Ftile> getMyChildren() {
		return Collections.emptyList();
	}

	protected FtileDiamondWIP(TextBlock label, ISkinParam skinParam, HColor backColor, HColor borderColor,
			Swimlane swimlane, TextBlock north, TextBlock south, TextBlock east, TextBlock west) {
		super(skinParam);
		if (UseStyle.useBetaStyle()) {
			Style style = getDefaultStyleDefinition().getMergedStyle(skinParam.getCurrentStyleBuilder());
			this.borderColor = borderColor; //style.value(PName.LineColor).asColor(skinParam.getThemeStyle(), getIHtmlColorSet());
			this.backColor = backColor; //style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(), getIHtmlColorSet());
			this.shadowing = style.value(PName.Shadowing).asDouble();
		} else {
			this.backColor = backColor;
			this.borderColor = borderColor;
			this.shadowing = skinParam().shadowing(null) ? 3 : 0;
		}

		this.swimlane = swimlane;

		this.label = label;
		this.north = north;
		this.west = west;
		this.east = east;
		this.south = south;
	}

	final public Set<Swimlane> getSwimlanes() {
		if (swimlane == null) {
			return Collections.emptySet();
		}
		return Collections.singleton(swimlane);
	}

	final public Swimlane getSwimlaneIn() {
		return swimlane;
	}

	final public Swimlane getSwimlaneOut() {
		return swimlane;
	}

}
