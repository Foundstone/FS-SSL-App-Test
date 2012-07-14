package fs.android.sslapptest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class FSSSLAppTestActivity extends Activity {

	private final String TAG = "FSFSFSFSFSFSFSFSFS";

	private String result = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.d(TAG, "[+] Starting application...");

		// Thread out the HTTP connection, Android does not allow do networking
		// on the main UI thread
		new Thread() {
			public void run() {
				Log.d(TAG, "[+] Starting HTTPS request...");

				try {
					Log.d(TAG, "[+] Set URL...");
					URL url = new URL("https://www.foundstone.com");

					Log.d(TAG, "[+] Open Connection...");
					HttpsURLConnection urlConnection = (HttpsURLConnection) url
							.openConnection();

					Log.d(TAG, "[+] Get the input stream...");
					InputStream in = urlConnection.getInputStream();

					Log.d(TAG, "[+] Certs...");
					Certificate[] certs = urlConnection.getServerCertificates();

					for (Certificate cert : certs) {
						Log.d(TAG, "[+] Cert Info:");
						Log.d(TAG, cert.toString());
					}

					Log.d(TAG,
							"[+] Create a buffered reader to read the response...");
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));

					String line = null;

					Log.d(TAG, "[+] Read all of the return....");
					while ((line = reader.readLine()) != null) {
						result += line + "\n";
					}

					// Uncomment this to see the result in logcat
					// Log.d(TAG, result);

					// end the blank result to a message handler, this is how
					// thread results are handled
					messageHandler.sendEmptyMessage(0);

					// Catch when the CA doesn't exist
					// Error: javax.net.ssl.SSLHandshakeException:
					// java.security.cert.CertPathValidatorException: Trust
					// anchor for certification path not found
				} catch (SSLHandshakeException e) {
					Log.d(TAG, "[-] SSL HANDSHAKE EXCEPTION: " + e.toString());
					e.printStackTrace();
					// Catch when the hostname does not verify
					// Line 224ish
					// http://source-android.frandroid.com/libcore/luni/src/main/java/libcore/net/http/HttpConnection.java
					// http://docs.oracle.com/javase/1.4.2/docs/api/javax/net/ssl/HostnameVerifier.html#method_detail
				} catch (IOException e) {
					Log.d(TAG, "[-] IO EXCEPTION (HOSTNAME): " + e.toString());
					Log.d(TAG,
							"[-] IO EXCEPTION (HOSTNAME)2: "
									+ e.getLocalizedMessage());
					Log.d(TAG,
							"[-] IO EXCEPTION (HOSTNAME)3: " + e.getMessage());
					e.printStackTrace();

				} catch (Exception e) {
					Log.d(TAG, "[-] EXCEPTION: " + e.toString());
					e.printStackTrace();
				}
			}
		}.start();

	}

	// Success handler, change the textview to the result, print some success
	// messages
	private Handler messageHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			// Get the textview
			TextView text = (TextView) findViewById(R.id.maintext);

			// Set the text
			text.setText(result);

			// Print success everywhere
			Log.d(TAG, "[+] SUCCESS");
			Log.d(TAG, "[+] SUCCESS");
			Log.d(TAG, "[+] SUCCESS");
			Toast.makeText(getApplicationContext(), "SUCCESS",
					Toast.LENGTH_LONG).show();
		}
	};
}