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
package net.sourceforge.plantuml.elk.proxy.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.plantuml.elk.proxy.Reflect;

public class ElkEdge extends ElkWithProperty {
    // ::remove folder when __HAXE__

	public ElkEdge(Object obj) {
		super(obj);
	}

	public ElkNode getContainingNode() {
		return new ElkNode(Reflect.call(obj, "getContainingNode"));
	}

	public Collection<ElkLabel> getLabels() {
		final List<ElkLabel> result = new ArrayList<>();
		Collection internal = (Collection) Reflect.call(obj, "getLabels");
		for (Object element : internal) {
			result.add(new ElkLabel(element));
		}
		return result;
	}

	public List<ElkEdgeSection> getSections() {
		final List<ElkEdgeSection> result = new ArrayList<>();
		Collection internal = (Collection) Reflect.call(obj, "getSections");
		for (Object element : internal) {
			result.add(new ElkEdgeSection(element));
		}
		return result;
	}

	public boolean isHierarchical() {
		throw new UnsupportedOperationException();
	}

}
