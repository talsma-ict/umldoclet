/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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

import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.style.Styleable;

abstract class FtileDiamondWIP extends AbstractFtile implements Styleable {

	protected final HColor backColor;
	protected final HColor borderColor;
	protected final Swimlane swimlane;

	protected final TextBlock label;

	protected final TextBlock north;
	protected final TextBlock south;
	protected /* final */ TextBlock west;
	protected /* final */ TextBlock east;

	protected final double shadowing;

	public void swapEastWest() {
		final TextBlock tmp = this.west;
		this.west = this.east;
		this.east = tmp;

	}

	final public StyleSignatureBasic getStyleSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.activity, SName.diamond);
	}

	final public Style getStyle() {
		return getStyleSignature().getMergedStyle(skinParam().getCurrentStyleBuilder());
	}

	@Override
	final public Collection<Ftile> getMyChildren() {
		return Collections.emptyList();
	}

	protected FtileDiamondWIP(TextBlock label, ISkinParam skinParam, HColor backColor, HColor borderColor,
			Swimlane swimlane, TextBlock north, TextBlock south, TextBlock east, TextBlock west) {
		super(skinParam);
		Style style = getStyleSignature().getMergedStyle(skinParam.getCurrentStyleBuilder());
		this.borderColor = borderColor;
		this.backColor = backColor;
		this.shadowing = style.value(PName.Shadowing).asDouble();

		this.swimlane = swimlane;

		this.label = label;
		this.north = north;
		this.west = west;
		this.east = east;
		this.south = south;
	}

	final public Set<Swimlane> getSwimlanes() {
		if (swimlane == null)
			return Collections.emptySet();

		return Collections.singleton(swimlane);
	}

	final public Swimlane getSwimlaneIn() {
		return swimlane;
	}

	final public Swimlane getSwimlaneOut() {
		return swimlane;
	}

}
