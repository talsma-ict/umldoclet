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
package net.sourceforge.plantuml.vizjs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VizJsEngine {

	public static boolean isOk() {
		try {
			final Class classVizJS = Class.forName("ch.braincell.viz.VizJS");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private final Object viz;
	private final Method mExecute;

	public VizJsEngine() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Class classVizJS = Class.forName("ch.braincell.viz.VizJS");
		final Method mCreate = classVizJS.getMethod("create");
		mExecute = classVizJS.getMethod("execute", String.class);
		this.viz = mCreate.invoke(null);
		System.err.println("Creating one engine");
	}

	public String execute(String dot) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		return (String) mExecute.invoke(viz, dot);
	}

}
