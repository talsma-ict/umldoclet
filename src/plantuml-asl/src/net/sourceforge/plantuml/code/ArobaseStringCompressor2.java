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
package net.sourceforge.plantuml.code;

import java.io.IOException;

import net.sourceforge.plantuml.StringUtils;

public class ArobaseStringCompressor2 implements StringCompressor {

	public String compress(String data) throws IOException {
		return clean2(data);
	}

	public String decompress(String s) throws IOException {
		return clean2(s);
	}

	private String clean2(String s) {
		// s = s.replace("\0", "");
		s = StringUtils.trin(s);
		// s = s.replace("\r", "").replaceAll("\n+$", "");
		if (s.startsWith("@start")) {
			return s;
		}
		return "@startuml\n" + s + "\n@enduml";
	}

}
