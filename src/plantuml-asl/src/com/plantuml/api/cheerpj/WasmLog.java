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
package com.plantuml.api.cheerpj;

import net.sourceforge.plantuml.utils.Log;

//::revert when __CORE__
//import com.leaningtech.client.Document;
//import com.leaningtech.client.Element;
//import com.leaningtech.client.Global;
//::done

public class WasmLog {

	public static long start;

	public static void log(String message) {
		// ::revert when __CORE__
		Log.info(message);
//		try {
//			if (start > 0) {
//				final long duration = System.currentTimeMillis() - start;
//				message = "(" + duration + " ms) " + message;
//			}
//			System.err.print(message);
//			if (StaticMemory.elementIdDebugJava == null)
//				return;
//			final Document document = Global.document;
//			if (document == null)
//				return;
//			final Element messageJava = document.getElementById(Global.JSString(StaticMemory.elementIdDebugJava));
//			if (messageJava == null)
//				return;
//			messageJava.set_textContent(Global.JSString(message));
//		} catch (Throwable t) {
//			System.err.print("Error " + t);
//			t.printStackTrace();
//		}
		// ::done
	}

}
