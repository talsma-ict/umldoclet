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
package net.sourceforge.plantuml.ugraphic;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class UImage implements UShape {

	private final MutableImage image;
	private final String formula;
	private final String rawFileName;

	public UImage(MutableImage image) {
		this(image, null, null);
	}

	private UImage(MutableImage image, String rawFileName, String formula) {
		this.image = image;
		this.formula = formula;
		this.rawFileName = rawFileName;
	}

	public final UImage withRawFileName(String rawFileName) {
		return new UImage(image, rawFileName, formula);
	}

	public final UImage withFormula(String formula) {
		return new UImage(image, rawFileName, formula);
	}

	public final String getRawFileName() {
		return rawFileName;
	}

	public final String getFormula() {
		return formula;
	}

	public UImage scale(double scale) {
		return new UImage(image.withScale(scale), rawFileName, formula);
	}

	public final BufferedImage getImage(double withScale) {
		return image.withScale(withScale).getImage();
		// return bufferedImage.getImage();
	}

	public int getWidth() {
		return image.getImage().getWidth() - 1;
	}

	public int getHeight() {
		return image.getImage().getHeight() - 1;
	}

	public UImage muteColor(Color newColor) {
		return new UImage(image.muteColor(newColor), rawFileName, formula);
	}

	public UImage muteTransparentColor(Color newColor) {
		return new UImage(image.muteTransparentColor(newColor), rawFileName, formula);
	}

	public UImage monochrome() {
		return new UImage(image.monochrome(), rawFileName, formula);
	}

	public double getScale() {
		return image.getScale();
	}

}
