package com.jalc.example.toggleled2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.app.LauncherActivity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity{

	Runnable run = new Runnable() {
	 	 
	 	@Override
	 	public void run() {
	 		//Loop until find BT
	 		if(mmSocket.isConnected())	{
	 			
	 				beginListenForData();
	 				handler.postDelayed(run, 1000*3);
	 			}
	 	
	 		else	{
	 	
	 			//Listening Data
	 			handler.removeCallbacks(run);
	 		}
	 	
	 		handler.post(run);
	 	}
	 };
	
	int readBufferPosition;
	private BluetoothSocket mmSocket;
	InputStream mmInputStream;
 	Handler handler = new Handler();

	private Button bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, aOn, aOff, exit;
	private ArrayAdapter<String> mArrayAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket btSocket;
	private ArrayList<BluetoothDevice> btDeviceArray = new ArrayList<BluetoothDevice>();
	private ConnectAsyncTask connectAsyncTask;
		
	MediaPlayer alarm, alarm2;
	
	TextView text, text1, text2;
	String txt, txtfinale1, txtfinale2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		
		alarm = MediaPlayer.create(this, R.raw.disconnect_x);
		alarm2 = MediaPlayer.create(this, R.raw.disconnect_x);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	 	StrictMode.setThreadPolicy(policy);
	 	
	 	text = (TextView)findViewById(R.id.textView5);
	 	
		text1 = (TextView)findViewById(R.id.textView1);
		text2 = (TextView)findViewById(R.id.textView2);		
		
		/**
		 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		 * IT´S NECESSARY INITIALIZATE THIS IN 0 BECAUSE
		 * THE PIC DOESN´T RECONIZE A "NIL" VALUE, AND 
		 * AND REMEMBER THAT IF DON'T HAVE VALUE, IT CARRIES
		 * GARBAGE NUMBERS. THIS DON´T AFFECTS THE READING STRING
		 * I'LL SHOW YOU BENEATH.
		 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++}+
		 */
		txtfinale1 = "0";
		
		mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		setListAdapter(mArrayAdapter);
		
		// Instance AsyncTask
		connectAsyncTask = new ConnectAsyncTask();
		
		//Get Bluettoth Adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// Check smartphone support Bluetooth
		if(mBluetoothAdapter == null){
			//Device does not support Bluetooth
			Toast.makeText(getApplicationContext(), "Not support bluetooth", 5).show();
			finish();
		}
		
		// Check Bluetooth enabled
		if(!mBluetoothAdapter.isEnabled()){
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1);
		}
		
		// Queryng paried devices
		Set<BluetoothDevice> pariedDevices = mBluetoothAdapter.getBondedDevices();
		if(pariedDevices.size() > 0){
			for(BluetoothDevice device : pariedDevices){
				mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				btDeviceArray.add(device);
			}
		}
		
		bt1 = (Button) findViewById(R.id.num1);
		bt1.setOnClickListener(send1);
		
		bt2 = (Button) findViewById(R.id.num2);
		bt2.setOnClickListener(send2);
		
		bt3 = (Button) findViewById(R.id.num3);
		bt3.setOnClickListener(send3);

		bt4 = (Button) findViewById(R.id.num4);
		bt4.setOnClickListener(send4);
		
		bt5 = (Button) findViewById(R.id.num5);
		bt5.setOnClickListener(send5);
		
		bt6 = (Button) findViewById(R.id.num6);
		bt6.setOnClickListener(send6);
		
		bt7 = (Button) findViewById(R.id.num7);
		bt7.setOnClickListener(send7);
		
		bt8 = (Button) findViewById(R.id.num8);
		bt8.setOnClickListener(send8);
		
		
		aOn = (Button) findViewById(R.id.button1);
		aOn.setOnClickListener(actAlarm);
		
		aOff = (Button) findViewById(R.id.button2);
		aOff.setOnClickListener(dactAlarm);
		
		exit = (Button) findViewById(R.id.button3);
		exit.setOnClickListener(quit);
		
	}

	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		BluetoothDevice device = btDeviceArray.get(position);
		connectAsyncTask.execute(device);
		
		final Handler handler = new Handler();

		handler.post(new Runnable() {
			
			int cont1, cont2, acum1, acum2, launch1, launch2;
			

		 	@Override
		 	public void run()	{
		 		if(mmSocket!=null)	{
		 		
		 			if(mmSocket.isConnected())	{	
		 				byte com = beginListenForData();
		 				txt = String.valueOf(Character.toChars(com));
		 				
		 				if(com!=1)	{
		 					txtfinale1+=txt;
		 					text.setText(txtfinale1);
		 							 					
		 				if(txtfinale1.compareTo("01020") == 0)	{
		 						
		 						acum1++;
		 						acum2++;
		 						
		 						if(acum1>=3)	{ 							
		 							txtfinale1 = "0";
		 							text1.setText("Child 2: LOST");
		 							text1.setTextColor(Color.argb(255, 255, 0, 0));
		 							alarm.seekTo(0);
		 							alarm.start();
		 							alarm.setLooping(true);		
		 						}
		 						
		 						if(acum2>=3)	{ 							
		 							txtfinale1 = "0";
		 							text2.setText("Child 1: LOST");
		 							text2.setTextColor(Color.argb(255, 255, 0, 0));
		 							alarm2.seekTo(0);
		 							alarm2.start();
		 							alarm2.setLooping(true);		
		 						}
		 						
		 						txtfinale1 = "0";
		 					}
		 				

 						/**
 						 *	AS YOU CAN SEE, THIS '0' IS CARRIED TO THE STRING AND THE APP READ IT.
 						 *  THIS ARE THE COMBINATIONS WITH A 4 CHAR STRIN
 						 */
		 				
		 				if(txtfinale1.compareTo("01021") == 0)	{
	 						
	 						acum1++;
	 						acum2=0;
	 						
	 						if(acum1>=3)	{ 							
	 							txtfinale1 = "0";
	 							text2.setText("Child 1: LOST");
	 							text2.setTextColor(Color.argb(255, 255, 0, 0));
	 							text1.setText("Child 2: OK");
	 							text1.setTextColor(Color.argb(255, 0, 255, 0));
	 							alarm.seekTo(0);
	 							alarm.start();
	 							alarm.setLooping(true);
	 							
	 							if (alarm2.isPlaying())	{
	 								alarm2.seekTo(0);
	 								alarm2.pause();
	 								text1.setText("Child 2: OK");
	 								text1.setTextColor(Color.argb(255, 0, 255, 0));
	 							}
	 						}
	 						
	 						txtfinale1 = "0";
	 					}
		 				
		 				if(txtfinale1.compareTo("01120") == 0)	{
	 						
	 						acum1=0;
	 						acum2++;
	 						
	 						if(acum2>=3)	{ 							
	 							txtfinale1 = "0";
	 							text1.setText("Child 2: LOST");
	 							text1.setTextColor(Color.argb(255, 255, 0, 0));
	 							alarm2.seekTo(0);
	 							alarm2.start();
	 							alarm2.setLooping(true);	
	 							
	 							if (alarm.isPlaying())	{
	 								alarm.pause();
	 								text1.setTextColor(Color.argb(255, 0, 255, 0));
	 							}
	 						}
	 						
	 						txtfinale1 = "0";
	 					}
		 				
		 				if(txtfinale1.compareTo("01121") == 0)	{
	 						
	 						acum1=0;
	 						acum2=0;
	 						txtfinale1 = "0";
	 						
	 						text1.setText("Child 2: OK");
	 						text2.setText("Child 1: OK");
	 						text1.setTextColor(Color.argb(255, 0, 255, 0));
	 						text2.setTextColor(Color.argb(255, 0, 255, 0));
 							
 							if (alarm2.isPlaying())
 								alarm2.pause(); 							
 							
 							if (alarm.isPlaying())
 								alarm.pause();
 								
	 					}
		 				
		 			}	
		 		
		 			else	{		 		
		 				handler.removeCallbacks(this);	 		
		 			}
		 		}
		 		
		 	handler.postDelayed(this, 0);		 	
		 		}
		 	}
		 	});
 	}

	//// Click event on Button
	private OnClickListener send1 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			OutputStream mmOutStream = null;
			
			try {
				
				if(btSocket.isConnected()){
					for(int i=0; i<= 3; i++){
						mmOutStream = btSocket.getOutputStream();
						mmOutStream.write(new String("1").getBytes());
					}
				}
				
			} catch (IOException e) { }			
		}
	};
	
	// Click event on Button
		private OnClickListener send2 = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				OutputStream mmOutStream = null;
				
				try {
					
					if(btSocket.isConnected()){
						for(int i=0; i<= 3; i++){
							mmOutStream = btSocket.getOutputStream();
							mmOutStream.write(new String("2").getBytes());
						}
					}
					
				} catch (IOException e) { }			
			}
		};
		
		// Click event on Button
		private OnClickListener send3 = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				OutputStream mmOutStream = null;
				
				try {
					
					if(btSocket.isConnected()){
						for(int i=0; i<= 3; i++){
							mmOutStream = btSocket.getOutputStream();
							mmOutStream.write(new String("3").getBytes());
						}
					}
					
				} catch (IOException e) { }			
			}
		};
	
		// Click event on Button
		private OnClickListener send4 = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				OutputStream mmOutStream = null;
				
				try {
					
					if(btSocket.isConnected()){
						for(int i=0; i<= 3; i++){
							mmOutStream = btSocket.getOutputStream();
							mmOutStream.write(new String("4").getBytes());
						}
					}
					
				} catch (IOException e) { }			
			}
		};

		// Click event on Button
		private OnClickListener send5 = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				OutputStream mmOutStream = null;
				
				try {
					
					if(btSocket.isConnected()){
						for(int i=0; i<= 3; i++){
							mmOutStream = btSocket.getOutputStream();
							mmOutStream.write(new String("5").getBytes());
						}
					}
					
				} catch (IOException e) { }			
			}
		};
		
		// Click event on Button
			private OnClickListener send6 = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					OutputStream mmOutStream = null;
					
					try {
						
						if(btSocket.isConnected()){
							for(int i=0; i<= 3; i++){
								mmOutStream = btSocket.getOutputStream();
								mmOutStream.write(new String("6").getBytes());
							}
						}
						
					} catch (IOException e) { }			
				}
			};
			
			// Click event on Button
			private OnClickListener send7 = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					OutputStream mmOutStream = null;
					
					try {
						
						if(btSocket.isConnected()){
							for(int i=0; i<= 3; i++){
								mmOutStream = btSocket.getOutputStream();
								mmOutStream.write(new String("7").getBytes());
							}
						}
						
					} catch (IOException e) { }			
				}
			};
		
			// Click event on Button
			private OnClickListener send8 = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					OutputStream mmOutStream = null;
					
					try {
						
						if(btSocket.isConnected()){
							for(int i=0; i<= 3; i++){
								mmOutStream = btSocket.getOutputStream();
								mmOutStream.write(new String("8").getBytes());
							}
						}
						
					} catch (IOException e) { }			
				}
			};
			
			// Click event on Button
			private OnClickListener actAlarm = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					alarm.start();
					alarm.setLooping(true);
				}
			};
			
			// Click event on Button
			private OnClickListener dactAlarm = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Toast toast = Toast.makeText(getApplicationContext(), "Silence activated until get data", 1);
					toast.show();
					
					text1.setText("SILENCE");
					text2.setText("SILENCE");
					text1.setTextColor(Color.argb(255, 0, 0, 255));
					text2.setTextColor(Color.argb(255, 0, 0, 255));
					txtfinale1 = "0";
														
					if (alarm2.isPlaying())	
							alarm2.pause();							
						
					if (alarm.isPlaying())
							alarm.pause();	
				}
				
			};
			
			// Click event on Button
			private OnClickListener quit = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			};			

	private class ConnectAsyncTask extends AsyncTask<BluetoothDevice, Integer, BluetoothSocket>{

		//private BluetoothSocket mmSocket;
		private BluetoothDevice mmDevice;
		
		@Override
		protected BluetoothSocket doInBackground(BluetoothDevice... device) {
							
			mmDevice = device[0];
			
			try {
				
				String mmUUID = "00001101-0000-1000-8000-00805F9B34FB";
				mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(mmUUID));
				mmSocket.connect();
				
				if(mmSocket.isConnected())
			 	{
			 		//handler.post(run);
					
								
				
			}} catch (Exception e) { Log.v("Muestra", e.getMessage());}
			
			return mmSocket;
		}

		@Override
		protected void onPostExecute(BluetoothSocket result) {
			
			btSocket = result;
			//Enable Button
			bt1.setEnabled(true);
			bt2.setEnabled(true);
			bt3.setEnabled(true);
			bt4.setEnabled(true);
			bt5.setEnabled(true);
			bt6.setEnabled(true);
			bt7.setEnabled(true);
			bt8.setEnabled(true);
			
			text1.setText("Child 2: OK");
			text2.setText("Child 1: OK");
			text1.setTextColor(Color.argb(255, 0, 255, 0));
			text2.setTextColor(Color.argb(255, 0, 255, 0));
			
		}
	}

	
	byte beginListenForData() {
	 	//final Handler handler = new Handler();
	 	final byte delimiter = 80; // This is the ASCII code for a newline
	 	// character

	 	readBufferPosition = 0;
	 	final byte[] readBuffer = new byte[1024];
	 	
	 	/*Thread workerThread = new Thread(new Runnable() {
	 		public void run() {
	 			while (!Thread.currentThread().isInterrupted()) {
	 				*/
	 	byte b = 1;
	 	try {
	 				 	mmInputStream = mmSocket.getInputStream();
	 					int bytesAvailable = mmInputStream.available();
	 					if (bytesAvailable > 0) {
	 	 
	 								byte[] packetBytes = new byte[bytesAvailable];
	 								mmInputStream.read(packetBytes);
	 									for (int i = 0; i < bytesAvailable; i++) {
	 										b = packetBytes[i];
	 										
	 										if (b == delimiter) {
	 												
											 	byte[] encodedBytes = new byte[readBufferPosition];
											 	System.arraycopy(readBuffer, 0,	encodedBytes, 0, encodedBytes.length);
											 	
											 	final String data = new String(
											 	encodedBytes, "US-ASCII");
											 	readBufferPosition = 0;

											 	//handler.post(new Runnable() {
											 		//public void run() {
											 			Log.v("bluetooth ", data);
											 	//	}
											 	//});
											 	
	 										} else {
	 											
	 											readBuffer[readBufferPosition++] = b;
	 										}
	 									}	
	 					}
	 					
	 				} catch (IOException ex) {
	 					Log.v("Prueba", "Prueba" + ex.getMessage());
	 				}return b;
	 			}/*
	 		}
	 	});

	 	workerThread.start();
	 }*/	
	
}
