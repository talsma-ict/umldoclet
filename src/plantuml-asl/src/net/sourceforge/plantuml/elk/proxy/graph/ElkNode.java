/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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

import net.sourceforge.plantuml.elk.proxy.Reflect;

public class ElkNode extends ElkWithProperty {

	public ElkNode(Object obj) {
		super(obj);
	}

	public ElkNode getParent() {
		final Object tmp = Reflect.call(obj, "getParent");
		if (tmp == null) {
			return null;
		}
		return new ElkNode(tmp);
	}

	public double getX() {
		return (Double) Reflect.call(obj, "getX");
	}

	public double getY() {
		return (Double) Reflect.call(obj, "getY");
	}

	public Collection<ElkLabel> getLabels() {
		final Collection<ElkLabel> result = new ArrayList<>();
		final Collection internal = (Collection) Reflect.call(obj, "getLabels");
		for (Object element : internal) {
			result.add(new ElkLabel(element));
		}
		return result;
	}

	public double getWidth() {
		return (Double) Reflect.call(obj, "getWidth");
	}

	public double getHeight() {
		return (Double) Reflect.call(obj, "getHeight");
	}

	public void setDimensions(double width, double height) {
		Reflect.call2(obj, "setDimensions", width, height);
	}

}
