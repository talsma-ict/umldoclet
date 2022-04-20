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
package net.sourceforge.plantuml.dedication;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.FileUtils;

public class Dedications {

	private static final List<Dedication> all = new ArrayList<>();

	static {
		try {
			all.add(new DedicationSimple(load("dedication"), "Write your own dedication!"));
			all.add(new DedicationSimple(load("linux_china"), "linux_china"));
			all.add(new DedicationSimple(load("arkban"), "arkban"));
			all.add(new DedicationSimple(load("boundaries"), "Boundaries allow discipline to create true strength"));
			all.add(new DedicationSimple(load("dr_chet"), "Thank you, Dr. Chet. I wouldn't be where I am without you"));
			all.add(new DedicationSimple(load("ben"), "Ben and Jen 2020"));
			all.add(secret(5, "835ff5d643b58cd35a20db6480071d05751aa6a0e01da78662ceafd0161f3f5e", new BigInteger(
					"1182423723677118831606503500858825217076578422970565964857326298418401529955036896808663335300684244453386039908536275400945824932191521017102701344437753036730900076162922741167523337650578479960119614237031234925702200473053235777")));
			all.add(secret(3, "514816d583044efbd336882227deb822194ff63e3bdc3cf707a01f17770d5a6a", new BigInteger(
					"538955952645999836068094511687012232127213955837942782605199622268460518023083462090291844640318324475656060087513198129259364840841077651829017347845508167869708224054456044836844382437974410757740941102771969965334031780041648873")));
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static DedicationCrypted secret(int tiny, String sig, BigInteger pq) throws IOException {
		return new DedicationCrypted(load(sig), tiny, sig, pq);
	}

	private static byte[] load(String name) throws IOException {
		final InputStream tmp = PSystemDedication.class.getResourceAsStream(name + ".png");
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileUtils.copyInternal(tmp, baos, true);
		return baos.toByteArray();
	}

	private Dedications() {
	}

	public synchronized static BufferedImage get(String line) {
		final TinyHashableString sentence = new TinyHashableString(line);
		for (Dedication dedication : all) {
			final BufferedImage image = dedication.getImage(sentence);
			if (image != null) {
				return image;
			}
		}
		return null;
	}

}
