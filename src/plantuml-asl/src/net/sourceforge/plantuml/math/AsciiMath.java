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
package net.sourceforge.plantuml.math;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.sourceforge.plantuml.BackSlash;
import net.sourceforge.plantuml.SvgString;

public class AsciiMath implements ScientificEquation {

	private static final String ASCIIMATH_PARSER_JS_LOCATION = "/net/sourceforge/plantuml/math/";

	private static String JAVASCRIPT_CODE;

	private final LatexBuilder builder;
	private final String tex;

	static {
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					AsciiMath.class.getResourceAsStream(ASCIIMATH_PARSER_JS_LOCATION + "ASCIIMathTeXImg.js"), "UTF-8"));
			final StringBuilder sb = new StringBuilder();
			String s = null;
			while ((s = br.readLine()) != null) {
				sb.append(s);
				sb.append(BackSlash.NEWLINE);
			}
			br.close();
			JAVASCRIPT_CODE = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public AsciiMath(String form) throws ScriptException, NoSuchMethodException {
		final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		engine.eval(JAVASCRIPT_CODE);
		final Invocable inv = (Invocable) engine;
		this.tex = patchColor((String) inv.invokeFunction("plantuml", form));
		this.builder = new LatexBuilder(tex);
	}

	private String patchColor(String latex) {
		return latex.replace("\\color{", "\\textcolor{");
	}

	public Dimension2D getDimension() {
		return builder.getDimension();
	}

	public SvgString getSvg(double scale, Color foregroundColor, Color backgroundColor) throws ClassNotFoundException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, InstantiationException, IOException {
		return builder.getSvg(scale, foregroundColor, backgroundColor);
	}

	public BufferedImage getImage(double scale, Color foregroundColor, Color backgroundColor)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return builder.getImage(scale, foregroundColor, backgroundColor);
	}

	public String getSource() {
		return tex;
	}

}
