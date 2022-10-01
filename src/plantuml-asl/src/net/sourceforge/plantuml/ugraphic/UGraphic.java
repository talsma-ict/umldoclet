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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public interface UGraphic {

	public StringBounder getStringBounder();

	public UParam getParam();

	public <SHAPE extends UShape> void draw(SHAPE shape);

	public UGraphic apply(UChange change);

	public ColorMapper getColorMapper();

	public void startUrl(Url url);

	public void closeUrl();

	public void startGroup(Map<UGroupType, String> typeIdents);

	public void closeGroup();

	public void flushUg();

	public boolean matchesProperty(String propertyName);

	public HColor getDefaultBackground();

	public void writeToStream(OutputStream os, String metadata, int dpi) throws IOException;
}
