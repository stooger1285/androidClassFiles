package course.labs.notificationslab;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

public class DownloaderTask extends AsyncTask<String, Void, String[]>
{

	private static final int SIM_NETWORK_DELAY = 5000;
	private static final String TAG = "Lab-Notifications";
	private final int MY_NOTIFICATION_ID = 11151990;
	private String mFeeds[] = new String[3];
	private MainActivity mParentActivity;
	private Context mApplicationContext;

	// Change this variable to false if you do not have a stable network
	// connection
	private static final boolean HAS_NETWORK_CONNECTION = true;

	// Raw feed file IDs used if you do not have a stable connection
	public static final int txtFeeds[] = { R.raw.tswift, R.raw.rblack, R.raw.lgaga };

	// Constructor
	public DownloaderTask(MainActivity parentActivity)
	{
		super();

		MainActivity.log("MIKE", "instantiate downloader async");
		mParentActivity = parentActivity;
		mApplicationContext = parentActivity.getApplicationContext();

	}

	@Override
	protected String[] doInBackground(String... urlParameters) {
		log("Entered doInBackground()");

		return download(urlParameters);

	}

	private String[] download(String urlParameters[]) {

		MainActivity.log("MIKE", "download");
		boolean downloadCompleted = false;

		try
		{

			for (int idx = 0; idx < urlParameters.length; idx++)
			{

				URL url = new URL(urlParameters[idx]);
				try
				{
					Thread.sleep(SIM_NETWORK_DELAY);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				InputStream inputStream;
				BufferedReader in;

				// Alternative for students without
				// a network connection
				if (HAS_NETWORK_CONNECTION)
				{
					inputStream = url.openStream();
					in = new BufferedReader(new InputStreamReader(inputStream));
				} else
				{
					inputStream = mApplicationContext.getResources().openRawResource(txtFeeds[idx]);
					in = new BufferedReader(new InputStreamReader(inputStream));
				}

				String readLine;
				StringBuffer buf = new StringBuffer();

				while ((readLine = in.readLine()) != null)
				{
					buf.append(readLine);
				}

				mFeeds[idx] = buf.toString();

				if (null != in)
				{
					in.close();
				}
			}

			downloadCompleted = true;

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		log("Tweet Download Completed:" + downloadCompleted);

		notify(downloadCompleted);

		return mFeeds;

	}

	// Call back to the MainActivity to update the feed display

	@Override
	protected void onPostExecute(String[] result) {
		super.onPostExecute(result);

		MainActivity.log("MIKE", "post execute");
		
		if (mParentActivity != null)
		{
			mParentActivity.setRefreshed(result);
		}

	}

	// If necessary, notifies the user that the tweet downloads are complete.
	// Sends an ordered broadcast back to the BroadcastReceiver in MainActivity
	// to determine whether the notification is necessary.

	private void notify(final boolean success) {
		log("Entered notify()");

		final Intent restartMainActivtyIntent = new Intent(mApplicationContext, MainActivity.class);
		restartMainActivtyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		if (success)
		{
			MainActivity.log("MIKE", "notify with success");
			// Save tweets to a file
			saveTweetsToFile();

		}

		// Sends an ordered broadcast to determine whether MainActivity is
		// active and in the foreground. Creates a new BroadcastReceiver
		// to receive a result indicating the state of MainActivity

		// The Action for this broadcast Intent is
		// MainActivity.DATA_REFRESHED_ACTION
		// The result Activity.RESULT_OK, indicates that MainActivity is active
		// and
		// in the foreground.

		MainActivity.log("MIKE", "send ordered broadcast");
		mApplicationContext.sendOrderedBroadcast(new Intent(MainActivity.DATA_REFRESHED_ACTION), null, new BroadcastReceiver()
		{
			final String failMsg = "Download has failed. Please retry Later.";
			final String successMsg = "Download completed successfully.";

			
			
			@Override
			public void onReceive(Context context, Intent intent) {

				log("Entered result receiver's onReceive() method");

				// TODO: Check whether the result code is RESULT_OK

				if (Activity.RESULT_OK != this.getResultCode())
				{

					MainActivity.log("MIKE", "on receive of downloaderer. result not ok");
					// TODO: If so, create a PendingIntent using the
					// restartMainActivityIntent and set its flags
					// to FLAG_UPDATE_CURRENT

					final Intent restartMainActivtyIntent = new Intent(mApplicationContext, MainActivity.class);
					// restartMainActivtyIntent.setFlags(FLAG_UPDATE_CURRENT);

					// TODO: change flag?
					final PendingIntent pendingIntent = PendingIntent.getActivity(mApplicationContext, 0, restartMainActivtyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

					// Uses R.layout.custom_notification for the
					// layout of the notification View. The xml
					// file is in res/layout/custom_notification.xml

					final RemoteViews mContentView = new RemoteViews(mApplicationContext.getPackageName(), R.layout.custom_notification);
					//mContentView.g

					

					
					//mContentView.find
					
					//TODO: do i have to do this on UI thread?
					
					// TODO: Set the notification View's text to
					// reflect whether or the download completed
					// successfully
					if( success )
					{
						MainActivity.log("MIKE", "on receive of downloaderer. result not ok. success message post");
						mContentView.setTextViewText(R.id.text, successMsg);	
					}
					else
					{
						MainActivity.log("MIKE", "on receive of downloaderer. result not ok. fail message post");
						mContentView.setTextViewText(R.id.text, failMsg);
					}
					
//					new Thread(new Runnable()
//					{
//						@Override
//						public void run() {
//							// do work
//							mContentView.post(new Runnable()
//							{
//								@Override
//								public void run() {
//									tv.setText("Download Complete");
//
//									mContentView.setTextViewText(R.id.text, contentText + " ("
//											+ ++mNotificationCount + ")");
//								}
//							});
//						}
//					}).start();

					// TODO: Use the Notification.Builder class to
					// create the Notification. You will have to set
					// several pieces of information. You can use
					// android.R.drawable.stat_sys_warning
					// for the small icon. You should also setAutoCancel(true).

					//Notification.Builder notificationBuilder = null;
//					// Build the Notification
					Notification.Builder notificationBuilder = new Notification.Builder(context)
						.setTicker("download complete")
						.setSmallIcon(android.R.drawable.stat_sys_warning)
						.setAutoCancel(true) //go away after drawer is clicked
						.setContentIntent(pendingIntent)
						.setContentText(successMsg); //pending intent
//						.setSound(soundURI)
	//					.setVibrate(mVibratePattern)
		//				.setContent(mContentView); //custom view
//
					
					
					// TODO: Send the notification
//					// Pass the Notification to the NotificationManager:
					NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					MainActivity.log("MIKE", "on receive of downloaderer. result not ok. notify ");
					mNotificationManager.notify(MY_NOTIFICATION_ID, notificationBuilder.build());
					log("Notification Area Notification sent");
				}
			}
		}, null, 0, null, null);
	}

	// Saves the tweets to a file
	private void saveTweetsToFile() {
		PrintWriter writer = null;
		try
		{
			FileOutputStream fos = mApplicationContext.openFileOutput(MainActivity.TWEET_FILENAME, Context.MODE_PRIVATE);
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));

			for (String s : mFeeds)
			{
				writer.println(s);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			if (null != writer)
			{
				writer.close();
			}
		}
	}

	// Simplified log output method
	private void log(String msg) {
		try
		{
			Thread.sleep(500);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		Log.i(TAG, msg);
	}

}