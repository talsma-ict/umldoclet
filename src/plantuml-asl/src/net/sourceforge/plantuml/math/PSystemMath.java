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
import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public class PSystemMath extends AbstractPSystem {

	private String math = "";
	private float scale = 1;
	private Color color = Color.BLACK;
	private Color backColor = Color.WHITE;

	public PSystemMath() {
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Math)");
	}

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int num, FileFormatOption fileFormat, long seed)
			throws IOException {
		final ScientificEquationSafe asciiMath = ScientificEquationSafe.fromAsciiMath(math);
		return asciiMath.export(os, fileFormat, scale, color, backColor);
	}

	public void doCommandLine(String line) {
		final String lineLower = StringUtils.trin(StringUtils.goLowerCase(line));
		final String colorParam = "color ";
		final String backParam = "backgroundcolor ";
		if (lineLower.startsWith(colorParam)) {
			final Color col3 = getColor(line.substring(colorParam.length()));
			if (col3 != null) {
				color = col3;
			}
		} else if (lineLower.startsWith(backParam)) {
			final Color col3 = getColor(line.substring(backParam.length()));
			if (col3 != null) {
				backColor = col3;
			}
		} else if (lineLower.startsWith("scale ")) {
			final String value = line.substring("scale ".length());
			try {
				final float scale1 = Float.parseFloat(value);
				if (scale1 > 0) {
					scale = scale1;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (lineLower.startsWith("dpi ")) {
			final String value = line.substring("dpi ".length());
			try {
				final float dpi1 = Float.parseFloat(value);
				if (dpi1 > 0) {
					scale = dpi1 / 96;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.math = line;

		}
	}

	private Color getColor(final String col) {
		final HColor col2 = col == null ? null : HColorSet.instance().getColorOrWhite(col);
		final Color col3 = new ColorMapperIdentity().toColor(col2);
		return col3;
	}

}
