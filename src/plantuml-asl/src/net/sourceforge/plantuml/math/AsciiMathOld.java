/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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

import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import net.sourceforge.plantuml.BackSlash;
import net.sourceforge.plantuml.Dimension2DDouble;

public class AsciiMathOld {

	private static final String ASCIIMATH_PARSER_JS_LOCATION = "/net/sourceforge/plantuml/math/";

	private static String JAVASCRIPT_CODE;

	static {
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					AsciiMathOld.class.getResourceAsStream(ASCIIMATH_PARSER_JS_LOCATION + "AsciiMathParser.js"), "UTF-8"));
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

	private final Node mathML;

	public AsciiMathOld(String form) throws IOException, ScriptException, ParserConfigurationException,
			NoSuchMethodException {
		final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		engine.eval(JAVASCRIPT_CODE);
		final Invocable inv = (Invocable) engine;
		final Document dom = createDocument();
		mathML = (Node) inv.invokeFunction("plantuml", dom, form);
	}

	private Document createDocument() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.newDocument();
		return document;
	}

	private Dimension2D dim;

	public String getSvg() throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final Class<?> clConverter = Class.forName("net.sourceforge.jeuclid.converter.Converter");
		final Method getInstance = clConverter.getMethod("getInstance");
		final Object conv = getInstance.invoke(null);
		final Method convert = clConverter.getMethod("convert", Node.class, OutputStream.class, String.class,
				Class.forName("net.sourceforge.jeuclid.LayoutContext"));
		dim = (Dimension2D) convert.invoke(conv, mathML, baos, "image/svg+xml", getLayout());
		return new String(baos.toByteArray());
	}

	public BufferedImage getImage() throws IOException, ClassNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException {
		final Class<?> clConverter = Class.forName("net.sourceforge.jeuclid.converter.Converter");
		final Method getInstance = clConverter.getMethod("getInstance");
		final Object conv = getInstance.invoke(null);
		// final LayoutContext layoutContext = LayoutContextImpl.getDefaultLayoutContext();

		final Method render = clConverter.getMethod("render", Node.class,
				Class.forName("net.sourceforge.jeuclid.LayoutContext"));

		final BufferedImage result = (BufferedImage) render.invoke(conv, mathML, getLayout());
		dim = new Dimension2DDouble(result.getWidth(), result.getHeight());
		return result;
	}

	private Object getLayout() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		final Class<?> clLayoutContextIml = Class.forName("net.sourceforge.jeuclid.context.LayoutContextImpl");
		final Class<?> clParameter = Class.forName("net.sourceforge.jeuclid.context.Parameter");
		final Method getDefaultLayoutContext = clLayoutContextIml.getMethod("getDefaultLayoutContext");
		final Object layoutContext = getDefaultLayoutContext.invoke(null);

		final Method setParameter = clLayoutContextIml.getMethod("setParameter", clParameter, Object.class);
		setParameter.invoke(layoutContext, clParameter.getDeclaredField("SCRIPTSIZEMULTIPLIER").get(null), (float) 2);
		return layoutContext;
	}

	public Dimension2D getDimension() {
		return dim;
	}

}
