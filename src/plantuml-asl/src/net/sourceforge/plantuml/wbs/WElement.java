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
package net.sourceforge.plantuml.wbs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.mindmap.IdeaShape;

class WElement {

	private final Display label;
	private final int level;
	private final WElement parent;
	private final List<WElement> childrenLeft = new ArrayList<WElement>();
	private final List<WElement> childrenRight = new ArrayList<WElement>();
	private final IdeaShape shape;

	public WElement(Display label) {
		this(label, 0, null, IdeaShape.BOX);
	}

	private WElement(Display label, int level, WElement parent, IdeaShape shape) {
		this.label = label;
		this.level = level;
		this.parent = parent;
		this.shape = shape;
	}

	public boolean isLeaf() {
		return childrenLeft.size() == 0 && childrenRight.size() == 0;
	}

	public WElement createElement(int newLevel, Display newLabel, Direction direction, IdeaShape shape) {
		final WElement result = new WElement(newLabel, newLevel, this, shape);
		if (direction == Direction.LEFT) {
			this.childrenLeft.add(result);
		} else {
			this.childrenRight.add(result);
		}
		return result;
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

	public Collection<WElement> getChildren(Direction direction) {
		if (direction == Direction.LEFT) {
			return Collections.unmodifiableList(childrenLeft);
		}
		return Collections.unmodifiableList(childrenRight);
	}

	public WElement getParent() {
		return parent;
	}

	public final IdeaShape getShape() {
		return shape;
	}

}
