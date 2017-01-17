package com.nickbullet.messageitmobile;

import com.nickbullet.messageitmobile.other.Message;
import com.nickbullet.messageitmobile.other.Utils;
import com.nickbullet.messageitmobile.other.WsConfig;

import java.net.URI;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.codebutler.android_websockets.WebSocketClient;
import com.example.messageitmobile.R;

public class MainActivity extends Activity {

	// LogCat tag
	private static final String TAG = MainActivity.class.getSimpleName();

	private Button btnSend;
	private EditText inputMsg;

	private WebSocketClient client;

	// ������� ������ ���������
	private MessagesListAdapter adapter;
	private List<Message> listMessages;
	private ListView listViewMessages;

	private Utils utils;

	// ��� �������
	private String name = null;
	
	public String timeCurrent = null;

	// JSON ����� ��� ������������� �������
	private static final String TAG_SELF = "self", TAG_NEW = "new",
			TAG_MESSAGE = "message", TAG_EXIT = "exit";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setTitle("MessageIT");
		btnSend = (Button) findViewById(R.id.btnSend);
		inputMsg = (EditText) findViewById(R.id.inputMsg);
		listViewMessages = (ListView) findViewById(R.id.list_view_messages);

		utils = new Utils(getApplicationContext());

		// ��������� ����� ������� � ����������� ������
		Intent i = getIntent();
		name = i.getStringExtra("name");

		btnSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// �������� ��������� �������
				sendMessageToServer(utils.getSendMessageJSON(inputMsg.getText()
						.toString(),getSysTime(timeCurrent)));

				// ������� ���������� ����
				inputMsg.setText("");
			}
		});

		listMessages = new ArrayList<Message>();

		adapter = new MessagesListAdapter(this, listMessages);
		listViewMessages.setAdapter(adapter);

		/**
		 * �������� ������ �������
		 * */
		client = new WebSocketClient(URI.create(WsConfig.URL_WEBSOCKET
				+ URLEncoder.encode(name)), new WebSocketClient.Listener() {
			@Override
			public void onConnect() {

			}

			/**
			 * ��������� ��������� � �������
			 * */
			@Override
			public void onMessage(String message) {
				Log.d(TAG, String.format("Got string message! %s", message));

				parseMessage(message);

			}

			@Override
			public void onMessage(byte[] data) {
				Log.d(TAG, String.format("Got binary message! %s",
						bytesToHex(data)));

				// ��������� � JSON �������
				parseMessage(bytesToHex(data));
			}

			/**
			 * ���������� ����� ���������� �������
			 * */
			@Override
			public void onDisconnect(int code, String reason) {

				String message = String.format(Locale.US,
						"Disconnected! Code: %d Reason: %s", code, reason);

				showToast(message);

				// ������� id ������
				utils.storeSessionId(null);
			}

			@Override
			public void onError(Exception error) {
				Log.e(TAG, "Error! : " + error);

				//showToast("������! : " + error);
			}

		}, null);

		client.connect();
	}

	/**
	 * ����� ��� �������� ��������� �� ������
	 * */
	private void sendMessageToServer(String message) {
		if (client != null && client.isConnected()) {
			client.send(message);
		}
	}

	/**
	 * ������� ��������� ���������� � �������
	 * */
	private void parseMessage(final String msg) {

		try {
			JSONObject jObj = new JSONObject(msg);

			// ����
			String flag = jObj.getString("flag");

			// ���� ���� ��� 'self', ���� ������ �������� id ������
			if (flag.equalsIgnoreCase(TAG_SELF)) {

				String sessionId = jObj.getString("sessionId");

				// ���������� id ������
				utils.storeSessionId(sessionId);

				Log.e(TAG, "Your session id: " + utils.getSessionId());

			} else if (flag.equalsIgnoreCase(TAG_NEW)) {
				// ���� ���� ��� 'new', �� � ��� ����� ����� ������������
				String name = jObj.getString("name");
				String message = jObj.getString("message");

				// ���������� ������� ������
				String onlineCount = jObj.getString("onlineCount");

				showToast(name + message + ". ������ " + onlineCount
						+ " ������� ������!");

			} else if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
				// ���� ���� ��� 'message', �� �������� ����� ���������
				String fromName = name;
				String message = jObj.getString("message");
				String sessionId = jObj.getString("sessionId");
				String time = jObj.getString("time");
				boolean isSelf = true;

				// �������� ���� ��������� ���� ���������� ����
				if (!sessionId.equals(utils.getSessionId())) {
					fromName = jObj.getString("name");
					isSelf = false;
				}

				Message m = new Message(fromName, message, time, isSelf);

				// ���������� ��������� � ������
				appendMessage(m);

			} else if (flag.equalsIgnoreCase(TAG_EXIT)) {
				// ����� ���� ��� 'exit', ���-�� ������� ���
				String name = jObj.getString("name");
				String message = jObj.getString("message");

				showToast(name + message);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(client != null & client.isConnected()){
			client.disconnect();
		}
	}

	/**
	 * ���������� ��������� � ������
	 * */
	private void appendMessage(final Message m) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				listMessages.add(m);

				adapter.notifyDataSetChanged();

				// ������������ ������� �����������
				playBeep();
			}
		});
	}

	private void showToast(final String message) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_LONG).show();
			}
		});

	}

	/**
	 * ������������ ������������ ����� �����������
	 * */
	public void playBeep() {

		try {
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
					notification);
			r.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	
    public String getSysTime(String time)
    {
    	Date now = new Date();
    	DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    	timeCurrent = formatter.format(now);
        return timeCurrent;
    }
}
