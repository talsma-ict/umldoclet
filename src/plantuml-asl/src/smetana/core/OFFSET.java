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

package smetana.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OFFSET {

	private static int CPT = 10000;
	private static Map<Integer, OFFSET> byID = new HashMap<Integer, OFFSET>();
	private static Map<Object, OFFSET> primaryKey = new HashMap<Object, OFFSET>();

	private final Class cl;
	private final String field;
	private final int id;

	private OFFSET(Class cl, String field) {
		this.cl = cl;
		this.field = field;
		this.id = CPT++;
		JUtils.LOG("REAL CREATING OF " + this);
	}

	@Override
	public String toString() {
		return cl.getName() + "::" + field;
	}

	public static OFFSET create(Class cl, String field) {
		final Object key = Arrays.asList(cl, field);
		JUtils.LOG("getting OFFSET " + key);
		OFFSET result = primaryKey.get(key);
		if (result != null) {
			JUtils.LOG("FOUND!");
			return result;
		}
		result = new OFFSET(cl, field);
		byID.put(result.id, result);
		primaryKey.put(key, result);
		return result;
	}

	public int toInt() {
		return id;
	}

	public static OFFSET fromInt(int value) {
		final OFFSET result = byID.get(value);
		if (result == null) {
			throw new IllegalArgumentException("value=" + value);
		}
		return result;
	}

	public final Class getTheClass() {
		return cl;
	}

	public final String getField() {
		return field;
	}

}
