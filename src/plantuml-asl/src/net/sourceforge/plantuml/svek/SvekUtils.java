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
package net.sourceforge.plantuml.svek;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.security.SFile;
import net.sourceforge.plantuml.utils.Log;

public class SvekUtils {

	// ::comment when __CORE__
	static public void traceString(final SFile f, String text) throws IOException {
		Log.info("Creating intermediate file " + f.getPrintablePath());
		try (PrintWriter pw = f.createPrintWriter()) {
			pw.print(text);
		}
	}
	// ::done

	static public double getValue(String svg, int starting, String varName) {
		final String varNameString = varName + "=\"";
		int p1 = svg.indexOf(varNameString, starting);
		if (p1 == -1) {
			throw new IllegalStateException();
		}
		p1 += varNameString.length();
		final int p2 = svg.indexOf('\"', p1);
		return Double.parseDouble(svg.substring(p1, p2));

	}

	public static XPoint2D getMinXY(List<XPoint2D> points) {
		double minx = points.get(0).x;
		double miny = points.get(0).y;
		for (int i = 1; i < points.size(); i++) {
			if (points.get(i).x < minx)
				minx = points.get(i).x;
			if (points.get(i).y < miny)
				miny = points.get(i).y;
		}
		return new XPoint2D(minx, miny);
	}

	public static XPoint2D getMaxXY(List<XPoint2D> points) {
		double maxx = points.get(0).x;
		double maxy = points.get(0).y;
		for (int i = 1; i < points.size(); i++) {
			if (points.get(i).x > maxx)
				maxx = points.get(i).x;
			if (points.get(i).y > maxy)
				maxy = points.get(i).y;
		}

		return new XPoint2D(maxx, maxy);
	}

	public static void println(StringBuilder sb) {
		sb.append('\n');
	}

	public static String pixelToInches(double pixel) {
		final double v = pixel / 72.0;
		return String.format(Locale.US, "%6.6f", v);
	}

}
