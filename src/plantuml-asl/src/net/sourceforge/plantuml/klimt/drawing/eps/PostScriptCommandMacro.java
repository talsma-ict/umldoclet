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
package net.sourceforge.plantuml.klimt.drawing.eps;

public class PostScriptCommandMacro implements PostScriptCommand {
    // ::remove folder when __HAXE__

	final private String name;
	final private PostScriptData data = new PostScriptData();

	public PostScriptCommandMacro(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void add(PostScriptCommand cmd) {
		data.add(cmd);
	}

	public String toPostString() {
		return name;
	}

	public String getPostStringDefinition() {
		final StringBuilder sb = new StringBuilder();
		sb.append("/" + name + " {\n");
		sb.append(data.toPostString());
		sb.append("} def\n");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return data.toPostString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		final PostScriptCommandMacro other = (PostScriptCommandMacro) obj;
		return this.data.toPostString().equals(other.data.toPostString());
	}

}
