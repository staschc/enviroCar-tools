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
package org.envirocar.harvest;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackPublisher {
	
	private static final Logger logger = LoggerFactory.getLogger(TrackPublisher.class);
	
	protected String targetConsumer;
	
	public TrackPublisher(String consumer) {
		this.targetConsumer = consumer;
	}
	
	protected void pushToConsumer(String content)
			throws ClientProtocolException, IOException {
		if (content == null || content.isEmpty()) {
			/*
			 * we did not get contents, ignore
			 */
			return;
		}
		
		HttpClient client = createClient();
		HttpPost post = new HttpPost(targetConsumer);
		post.setEntity(new StringEntity(content, ContentType.APPLICATION_JSON));
		HttpResponse resp = client.execute(post);
		EntityUtils.consume(resp.getEntity());
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

			}, new LoggingHostnameVerifier());
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
	
	protected String readContent(InputStream content) throws IOException {
		Scanner sc = new Scanner(content);
		StringBuilder sb = new StringBuilder(content.available());
		while (sc.hasNext()) {
			sb.append(sc.nextLine());
		}
		sc.close();
		return sb.toString();
	}
	
	private static class LoggingHostnameVerifier implements X509HostnameVerifier {

		private StrictHostnameVerifier delegate;

		public LoggingHostnameVerifier() {
			this.delegate = new StrictHostnameVerifier();
		}
		
		@Override
		public boolean verify(String hostname, SSLSession session) {
			boolean result = delegate.verify(hostname, session);
			logger.info("verified {}?: {}", hostname, result);
			return result;
		}

		@Override
		public void verify(String host, SSLSocket ssl) throws IOException {
			try {
				delegate.verify(host, ssl);
			} catch (IOException e) {
				logger.warn(e.getMessage(), e);
				throw e;
			}
		}

		@Override
		public void verify(String host, X509Certificate cert)
				throws SSLException {
			try {
				delegate.verify(host, cert);
			} catch (SSLException e) {
				logger.warn(e.getMessage(), e);
				throw e;
			}			
		}

		@Override
		public void verify(String host, String[] cns, String[] subjectAlts)
				throws SSLException {
			try {
				delegate.verify(host, cns, subjectAlts);
			} catch (SSLException e) {
				logger.warn(e.getMessage(), e);
				throw e;
			}				
		}

	}

}
