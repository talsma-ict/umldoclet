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
package com.plantuml.api.cheerpj.v1;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import com.plantuml.api.cheerpj.JsonResult;
import com.plantuml.api.cheerpj.SvgOutputStream;
import com.plantuml.api.cheerpj.Utils;
import com.plantuml.api.cheerpj.WasmLog;

import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.BlockUmlBuilder;
import net.sourceforge.plantuml.ErrorUml;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.error.PSystemError;
import net.sourceforge.plantuml.klimt.color.ColorMapper;
import net.sourceforge.plantuml.preproc.Defines;

//::revert when __CORE__
//import com.leaningtech.client.Global;
//::done

public class Svg {
    // ::remove folder when __HAXE__

	public static Object convert(String mode, String text) {
		final long start = System.currentTimeMillis();
		WasmLog.start = start;
		WasmLog.log("Starting processing");

		try {
			FileFormatOption format = new FileFormatOption(FileFormat.SVG);
			if ("dark".equalsIgnoreCase(mode))
				format = format.withColorMapper(ColorMapper.DARK_MODE);
			text = Utils.cleanText(text);
			final BlockUmlBuilder builder = new BlockUmlBuilder(Collections.<String>emptyList(), UTF_8,
					Defines.createEmpty(), new StringReader(text), null, "string");
			List<BlockUml> blocks = builder.getBlockUmls();

			if (blocks.size() == 0)
				return JsonResult.noDataFound(start);

			final Diagram system = blocks.get(0).getDiagram();
			if (system instanceof PSystemError) {
				final ErrorUml error = ((PSystemError) system).getFirstError();
				WasmLog.log("[" + error.getPosition() + "] " + error.getError());
				return JsonResult.fromError(start, (PSystemError) system);
			}

			WasmLog.log("...processing...");

			final SvgOutputStream svgos = new SvgOutputStream();
			WasmLog.log("...loading data...");

			final ImageData imageData = system.exportDiagram(svgos, 0, format);

			WasmLog.log("Done!");
			svgos.close();
			final String svg = svgos.toString();
			// ::revert when __CORE__
			return svg;
			// return Global.JSString(svg);
			// ::done

		} catch (Throwable t) {
			WasmLog.log("Fatal error " + t);
			return JsonResult.fromCrash(start, t);
		}

	}

}
