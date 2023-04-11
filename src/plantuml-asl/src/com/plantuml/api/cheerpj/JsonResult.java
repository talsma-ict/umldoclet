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

import net.sourceforge.plantuml.ErrorUml;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.error.PSystemError;

//::revert when __CORE__
//import com.leaningtech.client.Global;
//::done

public class JsonResult {

	private final StringBuilder sb = new StringBuilder();

	private JsonResult(long startingTime) {
		sb.append("{");
		this.append("duration", System.currentTimeMillis() - startingTime);
	}

	private Object done() {
		sb.append("}");
		// ::revert when __CORE__
		return sb.toString();
		// return Global.JSString(sb.toString());
		// ::done
	}

	public static Object noDataFound(long startingTime) {
		final JsonResult res = new JsonResult(startingTime);
		res.append("status", "No data found");
		return res.done();
	}

	public static Object fromCrash(long startingTime, Throwable t) {
		final JsonResult res = new JsonResult(startingTime);
		res.append("status", "General failure");
		res.append("exception", t.toString());
		return res.done();
	}

	public static Object ok(long startingTime, ImageData imageData, Diagram diagram) {
		final JsonResult res = new JsonResult(startingTime);
		res.append("status", "ok");
		if (imageData != null) {
			res.append("width", imageData.getWidth());
			res.append("height", imageData.getHeight());
		}
		res.append("description", diagram.getDescription().getDescription());
		return res.done();
	}

	public static Object fromError(long startingTime, PSystemError system) {
		final JsonResult res = new JsonResult(startingTime);
		res.append("status", "Parsing error");

		final ErrorUml err = system.getErrorsUml().iterator().next();
		res.append("line", err.getPosition());
		res.append("error", err.getError());

		return res.done();
	}

	private void append(String key, String value) {
		appendKeyOnly(key);
		sb.append('\"');
		sb.append(value);
		sb.append('\"');
	}

	private void append(String key, long value) {
		appendKeyOnly(key);
		sb.append(value);
	}

	protected void appendKeyOnly(String key) {
		if (sb.length() > 1)
			sb.append(',');
		sb.append('\"');
		sb.append(key);
		sb.append('\"');
		sb.append(':');
	}

}
