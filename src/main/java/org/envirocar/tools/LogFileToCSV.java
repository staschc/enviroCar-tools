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
/* 
 * enviroCar 2013
 * Copyright (C) 2013  
 * Martin Dueren, Jakob Moellers, Gerald Pape, Christopher Stephan
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 * 
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LogFileToCSV  {

	private static final String FILE = "responses.log";
	private Map<String, List<TimeValue>> propertyMap;

	public void testConvertLogToCSV() throws IOException, URISyntaxException {
		propertyMap = new HashMap<String, List<TimeValue>>();
		
		InputStream baseDir = getClass().getResourceAsStream("/"+FILE);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(baseDir));
		
		while (br.ready()) {
			processLine(br.readLine());
		}
		
		br.close();
		
		saveProperties();
	}

	private void saveProperties() throws URISyntaxException, IOException {
		File parent = new File(getClass().getResource(FILE).toURI());
		File target = new File(parent.getParentFile().getParentFile(), FILE.concat(".csv"));
		
		target.createNewFile();
		
		FileWriter fw = new FileWriter(target, false);
		
		StringBuilder header = new StringBuilder();
		for (String key : propertyMap.keySet()) {
			header.append(key);
			header.append("; ");
		}
		String sep = System.getProperty("line.separator");
		header.append(sep);
		fw.write(header.toString());
		
		for (String key : propertyMap.keySet()) {
			List<TimeValue> list = propertyMap.get(key);
			
			for (TimeValue timeValue : list) {
				fw.write(key);
				fw.write("; ");
				fw.write(String.format(Locale.GERMAN, "%f", timeValue.getValue()));
				fw.write("; ");
				fw.write(Long.toString(timeValue.getMillis()));
				fw.write(sep);
			}
		}
		
		fw.close();
	}

	private void processLine(String line) {
		line = line.trim();
		if (line.contains("CommandListener") && line.contains("Processed")
				&& line.contains("Response")) {
			int indexStart = line.indexOf("] Processed ");
			int indexEnd = line.indexOf(" Response:");
			String phen = line.substring(indexStart+"] Processed ".length(), indexEnd).trim();
			
			String time = line.substring(line.lastIndexOf(" ")).trim();
			
			Long cutoff = Long.valueOf(time.substring(0, 7))*1000000;
			Long millis = Long.valueOf(time);
			
			String valueStr = line.substring(indexEnd+" Response:".length(), line.indexOf("time:"));
			double value = Double.valueOf(valueStr.trim());
			
			processProperty(phen, value, millis-cutoff);
		}
	}

	private void processProperty(String phen, double value, Long millis) {
		List<TimeValue> list;
		if (propertyMap.containsKey(phen)) {
			list = propertyMap.get(phen);
		}
		else {
			list = new ArrayList<TimeValue>();
			propertyMap.put(phen, list);
		}
		
		list.add(new TimeValue(millis.longValue(), value));
	}
	
	private class TimeValue {
		
		private long millis;
		private double value;
		
		public TimeValue(long millis, double value) {
			super();
			this.millis = millis;
			this.value = value;
		}

		public long getMillis() {
			return millis;
		}

		public double getValue() {
			return value;
		}
		
	}
	
}
