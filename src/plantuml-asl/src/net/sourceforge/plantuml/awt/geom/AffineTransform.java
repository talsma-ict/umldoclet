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
package net.sourceforge.plantuml.awt.geom;

import net.sourceforge.plantuml.awt.geom.Point2D.Double;

public class AffineTransform {

	public AffineTransform(AffineTransform at) {
		// TODO Auto-generated constructor stub
	}

	public AffineTransform(double[] ds) {
		// TODO Auto-generated constructor stub
	}

	public AffineTransform(int i, int j, int k, int l, int m, int n) {
		// TODO Auto-generated constructor stub
	}

	public AffineTransform() {
		// TODO Auto-generated constructor stub
	}

	public double getScaleX() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getScaleY() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getTranslateX() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getTranslateY() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void translate(double x, double y) {
		// TODO Auto-generated method stub
		
	}

	public void concatenate(AffineTransform affineTransform) {
		// TODO Auto-generated method stub
		
	}

	public static AffineTransform getScaleInstance(double scale, double scale2) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setToShear(double coef, double coef2) {
		// TODO Auto-generated method stub
		
	}

	public static AffineTransform getTranslateInstance(double tx, double ty) {
		// TODO Auto-generated method stub
		return null;
	}

	public static AffineTransform getShearInstance(double shx, double shy) {
		// TODO Auto-generated method stub
		return null;
	}

	public static AffineTransform getRotateInstance(double d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Point2D transform(Point2D src, Point2D dest) {
		// TODO Auto-generated method stub
		return null;
	}

	public void scale(double changex, double changey) {
		// TODO Auto-generated method stub
		
	}

}
