/**
 * Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.envirocar.wps;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.envirocar.harvest.ProgressListener;
import org.envirocar.tools.TrackToCSV;
import org.n52.wps.algorithm.annotation.Algorithm;
import org.n52.wps.algorithm.annotation.ComplexDataOutput;
import org.n52.wps.algorithm.annotation.Execute;
import org.n52.wps.algorithm.annotation.LiteralDataInput;
import org.n52.wps.algorithm.descriptor.AlgorithmDescriptor;
import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.n52.wps.server.AbstractAnnotatedAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convert a enviroCar track to CSV
 * 
 * @author matthes rieke
 *
 */
@Algorithm(version = "1.0.0", abstrakt="enviroCar track to CSV converter")
public class TrackToCSVProcess extends AbstractAnnotatedAlgorithm implements ProgressListener {

    private static Logger LOGGER = LoggerFactory.getLogger(TrackToCSVProcess.class);
	private String trackUrl;
	private GenericFileData csv;

    public TrackToCSVProcess() {
        super();
    }
    
    @Override
    protected AlgorithmDescriptor createAlgorithmDescriptor() {
    	return super.createAlgorithmDescriptor();
    }
    
    @LiteralDataInput(identifier = "enviroCar-track-url", binding = LiteralStringBinding.class, minOccurs=1)
    public void setTrackUrl(String u) {
        this.trackUrl = u;
    }
    
    @ComplexDataOutput(identifier = "result", binding = GenericFileDataBinding.class)
    public GenericFileData getCSV() {
    	return this.csv;
    }
    
    @Execute
    public void runAlgorithm() throws IOException {
    	LOGGER.info("Pulling track");

    	HttpResponse resp = createClient().execute(new HttpGet(trackUrl));
    	
    	if (resp != null && resp.getStatusLine() != null &&
    			resp.getStatusLine().getStatusCode() >= HttpStatus.SC_OK) {
    		TrackToCSV toCsv = new TrackToCSV();
    		InputStream data = toCsv.convert(resp.getEntity().getContent());
    		csv = new GenericFileData(data, "text/csv");
    	}
    	else {
    		throw new IOException("Invalid response from server. Check the provided URL.");
    	}
    	
    	LOGGER.info("Finished CSV conversion");
    }

	protected HttpClient createClient() throws IOException {
		SSLSocketFactory sslsf;
		try {
			sslsf = new SSLSocketFactory(new TrustStrategy() {

				public boolean isTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
					// XXX !!!
					return true;
				}

			}, new StrictHostnameVerifier());
		} catch (KeyManagementException e) {
			throw new IOException(e);
		} catch (UnrecoverableKeyException e) {
			throw new IOException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e);
		} catch (KeyStoreException e) {
			throw new IOException(e);
		}
		Scheme httpsScheme2 = new Scheme("https", 443, sslsf);
		
		DefaultHttpClient client = new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(httpsScheme2);
		
		return client;
	}

	@Override
	public void onProgressUpdate(float progressPercent) {
		
	}
	
}