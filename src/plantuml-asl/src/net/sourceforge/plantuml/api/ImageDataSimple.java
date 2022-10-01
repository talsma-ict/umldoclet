/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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
package net.sourceforge.plantuml.api;

import net.sourceforge.plantuml.annotation.HaxeIgnored;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.core.ImageData;

public class ImageDataSimple extends ImageDataAbstract {

	@HaxeIgnored
	public ImageDataSimple(int width, int height) {
		super(width, height);
	}

	@HaxeIgnored
	public ImageDataSimple(XDimension2D dim) {
		super(dim);
	}

	public ImageDataSimple(XDimension2D dim, int status) {
		super(dim);
		setStatus(status);
	}

	@HaxeIgnored
	private ImageDataSimple() {
		this(0, 0);
	}

	public boolean containsCMapData() {
		return false;
	}

	public String getCMapData(String nameId) {
		throw new UnsupportedOperationException();
	}

	public String getWarningOrError() {
		return null;
	}

	public static ImageData error() {
		final ImageDataSimple result = new ImageDataSimple();
		result.setStatus(503);
		return result;
	}

	public static ImageData ok() {
		return new ImageDataSimple();
	}

}
