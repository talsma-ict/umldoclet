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
package net.sourceforge.plantuml.ugraphic.visio;

import java.io.IOException;
import java.io.OutputStream;

public class VisioLine implements VisioShape {

	private final int id;
	private final double x1;
	private final double y1;
	private final double x2;
	private final double y2;

	public static VisioLine createInches(int id, double x1, double y1, double x2, double y2) {
		return new VisioLine(id, toInches(x1), toInches(y1), toInches(x2), toInches(y2));
	}

	private VisioLine(int id, double x1, double y1, double x2, double y2) {
		if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0) {
			throw new IllegalArgumentException();
		}
		this.id = id;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public VisioShape yReverse(double maxY) {
		return new VisioLine(id, x1, maxY - y1, x2, maxY - y2);
	}

	private static double toInches(double val) {
		return val / 72.0;
	}

	public void print(OutputStream os) throws IOException {
		out(os, "<Shape ID='" + id + "' Type='Shape' LineStyle='3' FillStyle='3' TextStyle='3'>");
		out(os, "<XForm>");
		final double ddx = x2 - x1;
		final double ddy = y2 - y1;
		out(os, "<PinX F='(BeginX+EndX)/2'>" + ((x1 + x2) / 2) + "</PinX>");
		out(os, "<PinY F='(BeginY+EndY)/2'>" + ((y1 + y2) / 2) + "</PinY>");
		final double len = Math.sqrt(ddx * ddx + ddy * ddy);
		out(os, "<Width F='Sqrt((EndX-BeginX)^2+(EndY-BeginY)^2)'>" + len + "</Width>");
		out(os, "<Height>0</Height>");
		out(os, "<LocPinX F='Width*0.5'>" + (len / 2) + "</LocPinX>");
		out(os, "<LocPinY F='Height*0.5'>0</LocPinY>");
		final double atan2 = Math.atan2(ddy, ddx);
		out(os, "<Angle F='ATan2(EndY-BeginY,EndX-BeginX)'>" + atan2 + "</Angle>");
		out(os, "<FlipX>0</FlipX>");
		out(os, "<FlipY>0</FlipY>");
		out(os, "<ResizeMode>0</ResizeMode>");
		out(os, "</XForm>");
		out(os, "<XForm1D>");
		out(os, "<BeginX>" + x1 + "</BeginX>");
		out(os, "<BeginY>" + y1 + "</BeginY>");
		out(os, "<EndX>" + x2 + "</EndX>");
		out(os, "<EndY>" + y2 + "</EndY>");
		out(os, "</XForm1D>");
		out(os, "<Geom IX='0'>");
		out(os, "<NoFill>1</NoFill>");
		out(os, "<NoLine>0</NoLine>");
		out(os, "<NoShow>0</NoShow>");
		out(os, "<NoSnap>0</NoSnap>");
		out(os, "<MoveTo IX='1'>");
		out(os, "<X F='Width*0'>0</X>");
		out(os, "<Y>0</Y>");
		out(os, "</MoveTo>");
		out(os, "<LineTo IX='2'>");
		out(os, "<X F='Width*1'>" + len + "</X>");
		out(os, "<Y>0</Y>");
		out(os, "</LineTo>");
		out(os, "</Geom>");
		out(os, "</Shape>");
	}

	private void out(OutputStream os, String s) throws IOException {
		os.write(s.getBytes());
		os.write("\n".getBytes());
	}

}
