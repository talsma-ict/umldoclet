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

public final class SmetanaDebugSlow {

	// ::remove file when __CORE__

	// ::comment when __CORE__
	public static boolean TRACE = false;
	public static boolean TRACE_FINAL_CALL = false;
	public static boolean VERY_VERBOSE = false;

	private static Purify purify;

	private static Purify purify() {
		if (purify == null)
			purify = new Purify();
		return purify;
	}
	// ::done

	static public void LOG(String s) {
		// ::comment when __CORE__
		if (TRACE)
			purify().logline(s);
		// ::done

	}

	static public void ENTERING(String signature, String methodName) {
		// ::comment when __CORE__
		if (TRACE)
			purify().entering(signature, methodName);
		// ::done
	}

	static public void LEAVING(String signature, String methodName) {
		// ::comment when __CORE__
		if (TRACE)
			purify().leaving(signature, methodName);
		// ::done
	}

	public static void reset() {
		// ::comment when __CORE__
		if (TRACE)
			purify().reset();
		// ::done
	}

	public static void printMe() {
		// ::comment when __CORE__
		if (TRACE && TRACE_FINAL_CALL)
			purify().printMe();
		// ::done
	}

}
