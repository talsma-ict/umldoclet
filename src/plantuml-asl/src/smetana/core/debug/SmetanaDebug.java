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

package smetana.core.debug;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SmetanaDebug {
	// ::remove folder when __HAXE__
	static private final Map<String, String> methods = new LinkedHashMap<String, String>();

	static public void LOG(String s) {

	}

	static public void ENTERING(String signature, String methodName) {
//		if (methods.containsKey(methodName) == false)
//			methods.put(methodName, methodName);
	}

	static public void LIST_METHODS() {
		int i = 0;
		for (String s : methods.keySet()) {
			System.err.println("i=" + i + " " + s);
			i++;
		}
	}

	static public void LEAVING(String signature, String methodName) {
	}

	public static void reset() {
	}

	public static void printMe() {
	}

}
