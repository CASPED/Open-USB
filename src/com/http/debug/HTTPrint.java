package com.http.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HTTPrint {
	
	PrintStream old_out;
	PrintStream old_err;
	AsyncHttpClient client;
	
	String url;

	public HTTPrint(String url) {
		this.url = url;

		client = new AsyncHttpClient();

		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				http_print(String.valueOf((char) b), false);
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				http_print(new String(b, off, len), false);
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};
		
		OutputStream err = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				http_print(String.valueOf((char) b), true);
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				http_print(new String(b, off, len), true);
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};
		
		old_out = System.out;
		old_err = System.err;
		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(err, true));
	}
	
	private void http_print(final String text, Boolean error) {
		old_out.print(text);
		
		RequestParams params = new RequestParams();
		
		if(error) params.put("action", "out");
		else params.put("action", "error");

		params.put("msg", text);

		client.post(url+"/trace", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
					}
				});
	}

}
