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
package net.sourceforge.plantuml.elk.proxy.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import net.sourceforge.plantuml.elk.proxy.Reflect;

public class ElkEdgeSection {

	public final Object obj;

	public ElkEdgeSection(Object obj) {
		this.obj = Objects.requireNonNull(obj);
	}

	@Override
	public int hashCode() {
		return this.obj.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this.obj.equals(((ElkEdgeSection) other).obj);
	}

	public double getStartX() {
		return (Double) Reflect.call(obj, "getStartX");
	}

	public double getStartY() {
		return (Double) Reflect.call(obj, "getStartY");
	}

	public double getEndX() {
		return (Double) Reflect.call(obj, "getEndX");
	}

	public double getEndY() {
		return (Double) Reflect.call(obj, "getEndY");
	}

	public Collection<ElkBendPoint> getBendPoints() {
		final List<ElkBendPoint> result = new ArrayList<>();
		Collection internal = (Collection) Reflect.call(obj, "getBendPoints");
		for (Object element : internal) {
			result.add(new ElkBendPoint(element));
		}
		return result;
	}

}
