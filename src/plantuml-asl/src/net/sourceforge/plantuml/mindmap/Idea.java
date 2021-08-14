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
package net.sourceforge.plantuml.mindmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.ugraphic.color.HColor;

class Idea {

	private final Display label;
	private final int level;
	private final Idea parent;
	private final List<Idea> children = new ArrayList<>();
	private final IdeaShape shape;
	private final HColor backColor;
	private final StyleBuilder styleBuilder;
	private final String stereotype;

	public Idea(StyleBuilder styleBuilder, HColor backColor, Display label, IdeaShape shape, String stereotype) {
		this(styleBuilder, backColor, 0, null, label, shape, stereotype);
	}

	public Idea createIdea(StyleBuilder styleBuilder, HColor backColor, int newLevel, Display newDisplay,
			IdeaShape newShape, String stereotype) {
		final Idea result = new Idea(styleBuilder, backColor, newLevel, this, newDisplay, newShape, stereotype);
		this.children.add(result);
		return result;
	}

	private Idea(StyleBuilder styleBuilder, HColor backColor, int level, Idea parent, Display label, IdeaShape shape,
			String stereotype) {
		this.backColor = backColor;
		this.styleBuilder = styleBuilder;
		this.label = label;
		this.level = level;
		this.parent = parent;
		this.shape = shape;
		this.stereotype = stereotype;
	}

	@Override
	public String toString() {
		return label.toString();
	}

	public final int getLevel() {
		return level;
	}

	public final Display getLabel() {
		return label;
	}

	public Collection<Idea> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public Idea getParent() {
		return parent;
	}

	public final IdeaShape getShape() {
		return shape;
	}

	public final HColor getBackColor() {
		return backColor;
	}

	public final StyleBuilder getStyleBuilder() {
		return styleBuilder;
	}

	public final String getStereotype() {
		return stereotype;
	}

}
