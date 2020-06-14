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
package net.sourceforge.plantuml.sequencediagram;

import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleDefinition;
import net.sourceforge.plantuml.style.WithStyle;

public abstract class Grouping implements Event, WithStyle {

	private final String title;
	private final GroupingType type;
	private final String comment;
	private final HtmlColor backColorElement;

	// private final StyleBuilder styleBuilder;

	final private Style style;
	final private Style styleHeader;

	public StyleDefinition getDefaultStyleDefinition() {
		return StyleDefinition.of(SName.root, SName.element, SName.sequenceDiagram, SName.group);
	}

	private StyleDefinition getHeaderStyleDefinition() {
		return StyleDefinition.of(SName.root, SName.element, SName.sequenceDiagram, SName.groupHeader);
	}

	public Style[] getUsedStyles() {
		return new Style[] {
				style,
				styleHeader == null ? styleHeader : styleHeader.eventuallyOverride(PName.BackGroundColor,
						backColorElement) };
	}

	public Grouping(String title, String comment, GroupingType type, HtmlColor backColorElement,
			StyleBuilder styleBuilder) {
		this.title = title;
		// this.styleBuilder = styleBuilder;
		this.comment = comment;
		this.type = type;
		this.backColorElement = backColorElement;
		this.style = getDefaultStyleDefinition().getMergedStyle(styleBuilder);
		this.styleHeader = getHeaderStyleDefinition().getMergedStyle(styleBuilder);
	}

	@Override
	public final String toString() {
		return super.toString() + " " + type + " " + title;
	}

	final public String getTitle() {
		return title;
	}

	final public GroupingType getType() {
		return type;
	}

	public abstract int getLevel();

	public abstract HtmlColor getBackColorGeneral();

	final public String getComment() {
		return comment;
	}

	public final HtmlColor getBackColorElement() {
		return backColorElement;
	}

	public abstract boolean isParallel();

}
