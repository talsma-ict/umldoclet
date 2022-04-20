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
package net.sourceforge.plantuml.core;

// Remove CmapData and Dimension2D
// Merge CucaDiagramFileMakerResult
/**
 * Information about a generated image for a diagram.
 * For some diagrams, there are some position information about elements
 * from the diagram. In that case, the method <code>containsCMapData()</code> returns
 * <code>true</code> and you can retrieve those information using
 * <code>getCMapData()</code> method.
 * 
 * @author Arnaud Roques
 * 
 */
public interface ImageData {

	/**
	 * Width in pixel of the image.
	 */
	public int getWidth();

	/**
	 * Height in pixel of the image.
	 */
	public int getHeight();

	/**
	 * Indicates if the image has some position information.
	 * @return <code>true</code> if the image has position information.
	 */
	public boolean containsCMapData();

	/**
	 * Return position information as a CMap formated string.
	 * For example, if you call this method with <code>nameId</code>
	 * set to "foo_map", you will get something like:
	 * 
	 * <pre>
	 * &lt;map id="foo_map" name="foo_map"&gt;
	 * &lt;area shape="rect" id="..." href="..." title="..." alt="" coords="64,68,93,148"/&gt;
	 * &lt;/map&gt;
	 * </pre>
	 * 
	 * @param nameId the id to be used in the cmap data string.
	 */
	public String getCMapData(String nameId);
	
	public String getWarningOrError();
	
	public int getStatus();


}
