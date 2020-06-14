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

import smetana.core.debug.Debug;

public class JUtilsDebug {

	private final static Debug debug = null; //new Debug();

	static public void ENTERING(String signature, String methodName) {
		if (debug != null)
			debug.entering(signature, methodName);
	}

	static public void LEAVING(String signature, String methodName) {
		if (debug != null)
			debug.leaving(signature, methodName);
	}

	public static void reset() {
		if (debug != null)
			debug.reset();
	}

	public static void printMe() {
		if (debug != null)
			debug.printMe();
	}

}
