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
package net.sourceforge.plantuml.ugraphic;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.EnsureVisible;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class UGraphicNull extends AbstractUGraphic<String> implements EnsureVisible {

	@Override
	protected AbstractCommonUGraphic copyUGraphic() {
		return new UGraphicNull(this);
	}

	private UGraphicNull(UGraphicNull other) {
		super(other);
	}

	public UGraphicNull() {
		super(HColorUtils.BLACK, new ColorMapperIdentity(), FileFormat.PNG.getDefaultStringBounder(), "foo");
	}

	@Override
	public void writeToStream(OutputStream os, String metadata, int dpi) throws IOException {
	}

	public void ensureVisible(double x, double y) {
	}

}
