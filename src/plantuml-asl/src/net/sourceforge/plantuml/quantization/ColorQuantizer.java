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
package net.sourceforge.plantuml.quantization;

import java.util.Set;

public interface ColorQuantizer {
	/**
	 * Quantize the given set of colors, returning a set no larger than
	 * {@code maxColors}.
	 *
	 * <p>
	 * The intent is to pick a set of colors which are representative of the
	 * original color set, but no specific guarantees are made.
	 *
	 * @param originalColors the colors in the original image
	 * @param maxColorCount  the maximum number of colors to allow
	 * @return a quantized collection of colors no larger than {@code maxColors}
	 */
	public Set<QColor> quantize(Multiset<QColor> originalColors, int maxColorCount);
}
