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
package net.sourceforge.plantuml.cute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Group implements Positionned {

	private final String groupName;
	private final List<Positionned> shapes;
	private final Group parent;
	private final Map<String, Group> children;

	// private final List<Group> children = new ArrayList<Group>();

	@Override
	public String toString() {
		return "Group " + groupName + " (" + shapes.size() + ") ";
	}

	// public static Group fromList(List<Positionned> shapes) {
	// return new Group("Automatic", shapes);
	// }

	public static Group createRoot() {
		return new Group(null, "ROOT");
	}

	private Group(Group parent, String groupName) {
		this.parent = parent;
		this.groupName = groupName;
		this.shapes = new ArrayList<Positionned>();
		this.children = new HashMap<String, Group>();
	}

	private Group(Group parent, String groupName, List<Positionned> shapes) {
		this.parent = parent;
		this.groupName = groupName;
		this.shapes = shapes;
		this.children = null;
	}

	public Group createChild(String childName) {
		final Group result = new Group(this, childName);
		this.children.put(childName, result);
		return result;
	}

	public void drawU(UGraphic ug) {
		for (Positionned shape : shapes) {
			shape.drawU(ug);
		}
	}

	public void add(Positionned shape) {
		shapes.add(shape);
	}

	public String getName() {
		return groupName;
	}

	public Positionned rotateZoom(RotationZoom rotationZoom) {
		if (rotationZoom.isNone()) {
			return this;
		}
		final List<Positionned> result = new ArrayList<Positionned>();
		for (Positionned shape : shapes) {
			result.add(shape.rotateZoom(rotationZoom));
		}
		return new Group(parent, groupName + "->" + rotationZoom, result);
	}

	public Positionned translate(UTranslate translation) {
		throw new UnsupportedOperationException();
	}

	public Group getParent() {
		return parent;
	}

	public Map<String, Group> getChildren() {
		return Collections.unmodifiableMap(children);
	}

}
