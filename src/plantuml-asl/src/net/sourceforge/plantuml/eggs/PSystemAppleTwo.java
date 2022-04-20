/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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
package net.sourceforge.plantuml.eggs;

import java.io.IOException;

import net.sourceforge.plantuml.PlainStringsDiagram;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.graphic.GraphicPosition;
import net.sourceforge.plantuml.version.PSystemVersion;

public class PSystemAppleTwo extends PlainStringsDiagram {

	public PSystemAppleTwo(UmlSource source) throws IOException {
		super(source);
		strings.add("                             <b><size:18>Apple //e for ever !                             ");
		strings.add(" ");

		image = PSystemVersion.getApple2Image();
		imagePosition = GraphicPosition.BOTTOM;
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Apple //e)");
	}

}
