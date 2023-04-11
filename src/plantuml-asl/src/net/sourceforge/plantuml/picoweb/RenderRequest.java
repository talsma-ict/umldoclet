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
package net.sourceforge.plantuml.picoweb;

import net.sourceforge.plantuml.json.Json;
import net.sourceforge.plantuml.json.JsonArray;
import net.sourceforge.plantuml.json.JsonObject;

/**
 * POJO of the json sent to "POST /render"
 */
public class RenderRequest {

	private final String[] options;

	private final String source;

	public RenderRequest(String[] options, String source) {
		this.options = options;
		this.source = source;
	}

	public String[] getOptions() {
		return options;
	}

	public String getSource() {
		return source;
	}

	public static RenderRequest fromJson(String json) {
		final JsonObject parsed = Json.parse(json).asObject();
		final String[] options;

		if (parsed.contains("options")) {
			final JsonArray jsonArray = parsed.get("options").asArray();
			options = new String[jsonArray.size()];
			for (int i = 0; i < jsonArray.size(); i++) {
				options[i] = jsonArray.get(i).asString();
			}
		} else {
			options = new String[0];
		}

		return new RenderRequest(options, parsed.get("source").asString());
	}
}
