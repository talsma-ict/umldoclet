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
package net.sourceforge.plantuml.telnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.SourceStringReader;

class AcceptTelnetClient extends Thread {
	final private Socket clientSocket;
	final private BufferedReader br;
	final private OutputStream os;

	AcceptTelnetClient(Socket socket) throws Exception {
		clientSocket = socket;
		System.out.println("Client Connected ...");
		br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		os = clientSocket.getOutputStream();

		start();
	}

	public String runInternal() throws IOException {
		final StringBuilder sb = new StringBuilder();
		while (true) {
			final String s = br.readLine();
			if (s == null) {
				return sb.toString();
			}
			Log.println("S=" + s);
			sb.append(s);
			sb.append('\n');
			if (s.equalsIgnoreCase("@enduml")) {
				return sb.toString();
			}
		}
	}

	public void run() {
		try {
			final String uml = runInternal();
			Log.println("UML=" + uml);
			final SourceStringReader s = new SourceStringReader(uml);
			s.outputImage(os, new FileFormatOption(FileFormat.ATXT));
			os.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
