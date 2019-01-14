package cn.netin.launcher.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SocketServer implements Runnable {
	private static final String TAG = "EL SocketServer" ;
	private ServerSocket serverSocket ;
	private final ExecutorService pool;
	private final Context mContext;

	public SocketServer(Context context, int port, int poolSize)
			throws IOException {

		mContext = context ;
		serverSocket = new ServerSocket(port);
		pool = Executors.newFixedThreadPool(poolSize);
	}

	public void run() { // run the service
		try {
			for (;;) {
				Socket socket = serverSocket.accept() ;
				Log.d(TAG, "client connected :" + socket.getInetAddress()) ;
				Handler handler = new Handler(socket) ;
				pool.execute(handler);
			}
		} catch (IOException ex) {
			//pool.shutdown();
		}
	}

	public void close() {
		shutdownAndAwaitTermination( pool) ;
	}


	class Handler implements Runnable {
		private final Socket socket;

		public Handler(Socket socket) { 
			this.socket = socket;
		}
		public void run() {
			//定义输入流，来自于socket的输入流
			InputStream ips = null;
			try {
				ips = socket.getInputStream();
				byte[] bytes = new byte[16]; 
				int len = ips.read(bytes);
				if (len != -1) {
					Log.d(TAG, "read len=" + len + " value=" + bytes[0]) ;

					broadcast(bytes[0]) ;
				}
				ips.close();
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/** The following method shuts down an ExecutorService in two phases, 
	 * first by calling shutdown to reject incoming tasks, and then calling shutdownNow, 
	 * if necessary, to cancel any lingering tasks: 
	 * */
	private void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(6, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(6, TimeUnit.SECONDS))
					Log.e(TAG, "Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}


	private void broadcast(byte b) {
		Intent intent = new Intent() ;
		intent.setAction(ServiceContract.ACTION_REMOTE) ;
		intent.putExtra(ServiceContract.EXTRA_VALUE, b) ;
		mContext.sendBroadcast(intent);
	}
}
