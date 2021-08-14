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

import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public class ElkWithProperty {

	public final Object obj;

	public ElkWithProperty(Object obj) {
		this.obj = Objects.requireNonNull(obj);
	}

	@Override
	final public int hashCode() {
		return this.obj.hashCode();
	}

	@Override
	final public boolean equals(Object other) {
		return this.obj.equals(((ElkWithProperty) other).obj);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	final public void setProperty(Object key, Object value) {
		if (value instanceof EnumSet) {
			EnumSet result = null;
			for (Object foo : (Collection) value) {
				final ElkObjectProxy elk = (ElkObjectProxy) foo;
				if (result == null) {
					result = EnumSet.noneOf((Class) elk.getClass());
				}
				result.add(elk);
			}
			Reflect.call2(obj, "setProperty", key, result);
		} else if (value instanceof ElkObjectProxy) {
			final Object elk = ((ElkObjectProxy) value).getTrueObject();
			Reflect.call2(obj, "setProperty", key, elk);
		} else {
			Reflect.call2(obj, "setProperty", key, value);
		}
	}

}
