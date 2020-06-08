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
package net.sourceforge.plantuml.pdf;

import java.lang.reflect.Method;

import net.sourceforge.plantuml.security.SFile;

public class PdfConverter {

	public static void convert(SFile svgFile, SFile pdfFile) {

		if (svgFile.exists() == false) {
			throw new IllegalArgumentException();
		}
		pdfFile.delete();
		if (pdfFile.exists()) {
			throw new IllegalStateException();
		}

		try {
			// https://stackoverflow.com/questions/12579468/how-to-set-log4j-property-file
			System.setProperty("log4j.debug", "false");

			final Class<?> clSVGConverter = Class.forName("org.apache.batik.apps.rasterizer.SVGConverter");

			final Object converter = clSVGConverter.newInstance();

			final Class<?> clDestinationType = Class.forName("org.apache.batik.apps.rasterizer.DestinationType");
			final Object pdf = clDestinationType.getField("PDF").get(null);
			final Method setDestinationType = clSVGConverter.getMethod("setDestinationType", clDestinationType);

			setDestinationType.invoke(converter, pdf);

			final String[] path = new String[] { svgFile.getAbsolutePath() };
			final Method setSources = clSVGConverter.getMethod("setSources", path.getClass());
			setSources.invoke(converter, new Object[] { path });
			final Method setDst = clSVGConverter.getMethod("setDst", pdfFile.getClass());
			setDst.invoke(converter, new Object[] { pdfFile });
			final Method execute = clSVGConverter.getMethod("execute");
			execute.invoke(converter);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}
		if (pdfFile.exists() == false) {
			throw new IllegalStateException();
		}
	}
}
