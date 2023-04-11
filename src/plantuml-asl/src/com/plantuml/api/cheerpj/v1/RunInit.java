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
package com.plantuml.api.cheerpj.v1;

import java.io.IOException;

import com.plantuml.api.cheerpj.StaticMemory;

import net.sourceforge.plantuml.version.Version;

public class RunInit {

	public static void main(String[] argsArray) throws IOException {
		if (argsArray.length > 0)
			StaticMemory.cheerpjPath = argsArray[0];
		if (argsArray.length > 1)
			StaticMemory.elementIdDebugJava = argsArray[1];
		if (StaticMemory.cheerpjPath.endsWith("/") == false)
			StaticMemory.cheerpjPath = StaticMemory.cheerpjPath + "/";

		System.err.print("PlantUML Version: " + Version.versionString());
		System.err.print("Init ok. cheerpjPath is " + StaticMemory.cheerpjPath);
		if (StaticMemory.elementIdDebugJava != null)
			System.err.print("I will echo debug message to element id " + StaticMemory.elementIdDebugJava);
	}

}
