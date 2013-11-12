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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = Coordinate.CoordinateDeserializer.class)
@JsonSerialize(using = Coordinate.CoordinateSerializer.class)
public class Coordinate {

	private double longitude;
	private double latitude;
	private double altitude = Double.NaN;

	public Coordinate() {
	}

	public Coordinate(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public Coordinate(double longitude, double latitude, double altitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public static class CoordinateDeserializer extends
			JsonDeserializer<Coordinate> {

		@Override
		public Coordinate deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			if (jp.isExpectedStartArrayToken()) {
				return deserializeArray(jp, ctxt);
			}
			throw ctxt.mappingException(Coordinate.class);
		}

		protected Coordinate deserializeArray(JsonParser jp,
				DeserializationContext ctxt) throws IOException,
				JsonProcessingException {
			Coordinate node = new Coordinate();
			node.setLongitude(extractDouble(jp, ctxt, false));
			node.setLatitude(extractDouble(jp, ctxt, false));
			node.setAltitude(extractDouble(jp, ctxt, true));
			if (jp.hasCurrentToken()
					&& jp.getCurrentToken() != JsonToken.END_ARRAY)
				jp.nextToken();
			return node;
		}

		private double extractDouble(JsonParser jp,
				DeserializationContext context, boolean optional)
				throws JsonParseException, IOException {
			JsonToken token = jp.nextToken();
			if (token == null) {
				if (optional) {
					return Double.NaN;
				} else {
					throw context
							.mappingException("Mandatory mapping not found!");
				}
			} else {
				switch (token) {
				case END_ARRAY:
					if (optional) {
						return Double.NaN;
					} else {
						throw context
								.mappingException("Mandatory mapping not found!");
					}
				case VALUE_NUMBER_FLOAT:
					return jp.getDoubleValue();
				case VALUE_NUMBER_INT:
					return jp.getLongValue();
				default:
					throw context.mappingException("Unrecognized mapping: "
							+ token);
				}
			}
		}
	}

	public static class CoordinateSerializer extends JsonSerializer<Coordinate> {

		@Override
		public void serialize(Coordinate value, JsonGenerator jgen,
				SerializerProvider provider) throws IOException,
				JsonProcessingException {
			jgen.writeStartArray();
			jgen.writeNumber(value.getLongitude());
			jgen.writeNumber(value.getLatitude());
			if (!Double.isNaN(value.getAltitude())) {
				jgen.writeNumber(value.getAltitude());
			}
			jgen.writeEndArray();
		}
	}

}
