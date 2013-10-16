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
package org.envirocar.geojson;

import java.util.HashMap;
import java.util.Map;

public class FeatureProperties {

	private Map<String, Object> phenomenons = new HashMap<>();
	private String sensor;
	private String time;

	public FeatureProperties() {
	}

	public Map<String, Object> getPhenomenons() {
		return phenomenons;
	}

	public void setPhenomenons(Map<String, Object> phenomenons) {
		this.phenomenons = phenomenons;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
