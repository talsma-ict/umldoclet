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
package net.sourceforge.plantuml.math;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.script.ScriptException;

import net.sourceforge.plantuml.klimt.MutableImage;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.UImageSvg;

public class AsciiMath implements ScientificEquation {
	// ::remove folder when __CORE__

	private final LatexBuilder builder;
	private final String tex;

	public AsciiMath(String form) throws ScriptException, NoSuchMethodException {
		this.tex = new ASCIIMathTeXImg().getTeX(form);
		this.builder = new LatexBuilder(tex);
	}

	public XDimension2D getDimension() {
		return builder.getDimension();
	}

	public UImageSvg getSvg(double scale, Color foregroundColor, Color backgroundColor)
			throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, InstantiationException, IOException {
		return builder.getSvg(scale, foregroundColor, backgroundColor);
	}

	public MutableImage getImage(Color foregroundColor, Color backgroundColor)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return builder.getImage(foregroundColor, backgroundColor);
	}

	public String getSource() {
		return tex;
	}

}
