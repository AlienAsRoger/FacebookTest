package com.developer4droid.FacebookTest;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
	private TextView welcome;
	private TestWait testWait;
	private TestSessionCallBack testSessionCallBack;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button buttonLoginFragment = (Button) findViewById(R.id.buttonLoginFragment);
		buttonLoginFragment.setOnClickListener(this);
		welcome = (TextView) findViewById(R.id.welcome);
		printHashKey();

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.container_frame, new SessionLoginFragment()).commit();

		// start Facebook Login
		testWait = new TestWait();
		testSessionCallBack = new TestSessionCallBack();
		Session.openActiveSession(this, true, testSessionCallBack);
		findViewById(R.id.lin).setOnClickListener(this);
	}

	private class TestWait implements Request.GraphUserCallback {

		@Override
		public void onCompleted(GraphUser user, Response response) {
			Log.d("TEST", "user = " + user);
			if (user != null) {
				TextView welcome = (TextView) findViewById(R.id.welcome);
				welcome.setText("Hello " + user.getName() + "!");
			}
		}
	}

	private class TestSessionCallBack implements Session.StatusCallback {

		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (session.isOpened()) {

				// make request to the /me API
				Request.executeMeRequestAsync(session, testWait);
			}
		}
	}

	public void printHashKey() {

		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("TEMPTAGHASH KEY:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (PackageManager.NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session session = Session.getActiveSession();
		session.onActivityResult(this, requestCode, resultCode, data);
		Request.executeMeRequestAsync(session, testWait);
	}
private static final String NAME = "name";
	private static final String ID = "id";
	private static final String PICTURE = "picture";
	private static final String FIELDS = "fields";
	private static final String REQUEST_FIELDS = TextUtils.join(",", new String[]{ID, NAME, PICTURE});

	@Override
	public void onClick(View view) {

		if (view.getId() == R.id.loginFb) {
//		startActivity(new Intent(this, LoginUsingLoginFragmentActivity.class));
		// start Facebook Login
		final Session activeSession = Session.getActiveSession();
		if (activeSession != null && activeSession.isOpened()) {
			welcome.setText("session closed");
			activeSession.closeAndClearTokenInformation();
		} else {
			welcome.setText("opening session");
			Session.openActiveSession(this, true, new Session.StatusCallback() {

				// callback when session changes state
				@Override
				public void call(final Session session, SessionState state, Exception exception) {
					if (exception != null) {
						Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_SHORT).show();
					}
					if (session.isOpened()) {
						welcome.setText("session is open");
						Log.d("TEST", "session opened");

						Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
							@Override
							public void onCompleted(GraphUser me, Response response) {
								if (session == activeSession) {
									Log.d("TEST", " user = " + me);
								}
//								if (response.getError() != null) {
//									loginButton.handleError(response.getError().getException());
//								}
							}
						});
						Bundle parameters = new Bundle();
						parameters.putString(FIELDS, REQUEST_FIELDS);
						request.setParameters(parameters);
						Request.executeBatchAsync(request);

						// make request to the /me API
//						Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
//
//							// callback after Graph API response with user object
//							@Override
//							public void onCompleted(GraphUser user, Response response) {
//								Log.d("TEST", "onCompleted user = " + user);
//								if (user != null) {
//
//									welcome.setText("Hello " + user.getName() + "!");
//								}
//							}
//						});
					} else {
						Log.d("TEST", "session closed");
					}
				}
			});
		}
		}
	}
}
