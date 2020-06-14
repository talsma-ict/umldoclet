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

package smetana.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import smetana.core.amiga.Area;

public class CFunctionImpl extends UnsupportedC implements CFunction, Area {

	private final Class codingClass;
	private final String name;
	private final Method method;

	public static CFunctionImpl create(Class codingClass, String name) {
		return new CFunctionImpl(codingClass, name);
	}

	private CFunctionImpl(Class codingClass, String name) {
		this.codingClass = codingClass;
		this.name = name;
		for (Method m : codingClass.getMethods()) {
			if (m.getName().equals(name)) {
				this.method = m;
				return;
			}
		}
		JUtils.LOG("CANNOT FIND METHOD " + name + " IN " + codingClass);
		throw new IllegalStateException("codingClass=" + codingClass + " name=" + name);
	}

	@Override
	public String toString() {
		return codingClass.getName() + "::" + name;
	}

	public Object exe(Object... args) {
		JUtils.LOG("-------");
		for (Object arg : args) {
			JUtils.LOG("arg=" + arg);
		}
		JUtils.LOG("method="+method);
		try {
			return this.method.invoke(null, args);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException(toString());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException(toString());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException(toString());
		}
	}

	public String getName() {
		return name;
	}

	public void memcopyFrom(Area source) {
		throw new UnsupportedOperationException();
	}
	
}
