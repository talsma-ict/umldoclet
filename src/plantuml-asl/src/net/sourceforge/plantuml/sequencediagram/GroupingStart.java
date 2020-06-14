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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;

public class GroupingStart extends Grouping {

	private final List<GroupingLeaf> children = new ArrayList<GroupingLeaf>();
	private final HtmlColor backColorGeneral;

	final private GroupingStart parent;
	private boolean parallel = false;

	public GroupingStart(String title, String comment, HtmlColor backColorGeneral, HtmlColor backColorElement,
			GroupingStart parent, StyleBuilder styleBuilder) {
		super(title, comment, GroupingType.START, backColorElement, styleBuilder);
		this.backColorGeneral = backColorGeneral;
		this.parent = parent;
	}

	public Style[] getUsedStyles() {
		final Style[] result = super.getUsedStyles();
		if (result[0] != null) {
			result[0] = result[0].eventuallyOverride(PName.BackGroundColor, backColorGeneral);
		}
		return result;
	}

	List<GroupingLeaf> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public void addChildren(GroupingLeaf g) {
		children.add(g);
	}

	public int getLevel() {
		if (parent == null) {
			return 0;
		}
		return parent.getLevel() + 1;
	}

	@Override
	public HtmlColor getBackColorGeneral() {
		return backColorGeneral;
	}

	public boolean dealWith(Participant someone) {
		return false;
	}

	public Url getUrl() {
		return null;
	}

	public boolean hasUrl() {
		return false;
	}

	@Override
	public boolean isParallel() {
		return parallel || getTitle().equals("par2");
	}

	public void goParallel() {
		this.parallel = true;
	}

}
