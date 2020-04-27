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

import java.awt.Color;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Icon;

public class TeXIconBuilder {

	private Icon icon;

	public TeXIconBuilder(String tex, Color foregroundColor) throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {

		// TeXFormula formula = new TeXFormula(latex);
		final Class<?> clTeXFormula = Class.forName("org.scilab.forge.jlatexmath.TeXFormula");
		final Object formula = clTeXFormula.getConstructor(String.class).newInstance(tex);

		// TeXIcon icon = formula.new TeXIconBuilder().setStyle(TeXConstants.STYLE_DISPLAY).setSize(20).build();
		final Class<?> clTeXIconBuilder = clTeXFormula.getClasses()[0];
		final Object builder = clTeXIconBuilder.getConstructors()[0].newInstance(formula);
		clTeXIconBuilder.getMethod("setStyle", int.class).invoke(builder, 0);
		clTeXIconBuilder.getMethod("setSize", float.class).invoke(builder, (float) 20);
		icon = (Icon) clTeXIconBuilder.getMethod("build").invoke(builder);

		final int margin = 1;
		final Insets insets = new Insets(margin, margin, margin, margin);
		icon.getClass().getMethod("setInsets", insets.getClass()).invoke(icon, insets);
		icon.getClass().getMethod("setForeground", foregroundColor.getClass()).invoke(icon, foregroundColor);
	}

	public Icon getIcon() {
		return icon;
	}

}
