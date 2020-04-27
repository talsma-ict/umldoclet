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
package net.sourceforge.plantuml.error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.plantuml.ErrorUml;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.UmlSource;

public class PSystemErrorUtils {

	// public static AbstractPSystemError buildV1(UmlSource source, ErrorUml singleError, List<String> debugLines) {
	// return new PSystemErrorV1(source, singleError, debugLines);
	// }

	public static PSystemError buildV2(UmlSource source, ErrorUml singleError, List<String> debugLines,
			List<StringLocated> list) {
		// return new PSystemErrorV1(source, singleError, debugLines);
		return new PSystemErrorV2(source, list, singleError);
	}

	public static PSystemError merge(Collection<PSystemError> ps) {
		UmlSource source = null;
		final List<ErrorUml> errors = new ArrayList<ErrorUml>();
		// final List<String> debugs = new ArrayList<String>();
		final List<PSystemErrorV2> errorsV2 = new ArrayList<PSystemErrorV2>();
		for (PSystemError system : ps) {
			if (system == null) {
				continue;
			}
			if (system.getSource() != null && source == null) {
				source = system.getSource();
			}
			errors.addAll(system.getErrorsUml());
			// if (system instanceof PSystemErrorV1) {
			// debugs.addAll(((PSystemErrorV1) system).debugLines);
			// if (((PSystemErrorV1) system).debugLines.size() > 0) {
			// debugs.add("-");
			// }
			// }
			if (system instanceof PSystemErrorV2) {
				errorsV2.add((PSystemErrorV2) system);
			}
		}
		if (source == null) {
			throw new IllegalStateException();
		}
		if (errorsV2.size() > 0) {
			return mergeV2(errorsV2);
		}
		throw new IllegalStateException();
		// return new PSystemErrorV1(source, errors, debugs);
	}

	private static PSystemErrorV2 mergeV2(List<PSystemErrorV2> errorsV2) {
		PSystemErrorV2 result = null;
		for (PSystemErrorV2 err : errorsV2) {
			if (result == null || result.size() < err.size()) {
				result = err;
			}
		}
		return result;
	}

	public static boolean isDiagramError(Class<? extends Diagram> type) {
		return PSystemError.class.isAssignableFrom(type);
		// return type == PSystemErrorV1.class;
	}

}
