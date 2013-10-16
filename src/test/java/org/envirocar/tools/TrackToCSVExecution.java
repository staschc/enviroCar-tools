/**
 * Copyright (C) 2013
 * by Matthes Rieke
 *
 * Contact: http://matthesrieke.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.envirocar.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.junit.Test;

public class TrackToCSVExecution {

	@Test
	public void execute() throws IOException, URISyntaxException {
		TrackToCSV ttc = new TrackToCSV();
		String fileName = "envirocar_track48.json";
		InputStream in = ttc.convert(getClass().getResourceAsStream(fileName));
		
		FileOutputStream fos = new FileOutputStream(
				new File(getClass().getResource("/").toURI().getPath(), fileName+".csv"));
		
		ReadableByteChannel inChannel = Channels.newChannel(in);
		ChannelTools.fastChannelCopy(inChannel, fos.getChannel());
		
		fos.flush();
		fos.close();
		in.close();
	}
	
}
