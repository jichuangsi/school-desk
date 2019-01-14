package cn.netin.launcher.service.aispeech;

import org.json.JSONException;
import org.json.JSONObject;

import com.aispeech.AIEngine;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import cn.netin.launcher.service.ServiceContract;

public class AIScorer {
	private static final String TAG = "EL AIScorer" ;
	private static final int WORD = 1;
	private static final int SENTENCE = 2;

	private Context mContext ;
	private AIRecorder mRecorder ;
	private long mEngine = 0;

	boolean mIsStopped = true;
	private AIScorerListener mListener = null ;
	private String mFilesDir = null ;
	private int mCount = 0 ;


	private AIRecorder.Callback recordCallback = new AIRecorder.Callback() {
		public void run(byte[] data, int size) {
			AIEngine.aiengine_feed(mEngine, data, size);
			mCount++ ;
			if (mCount > 500 ) {
				Log.e(TAG, "AIRecorder lasts too long!") ;
				stopScore() ;						
				mListener.onCompletion(10, null);			
			}
		}
	} ;


	/**
	 * 监听接口
	 */
	public interface AIScorerListener{
		public void onCompletion(int result, String json);
	}


	public void setListener(AIScorerListener listener) {
		mListener = listener ;
	}

	private AIEngine.aiengine_callback callback = new AIEngine.aiengine_callback() {
		@Override
		public int run(byte[] id, int type, byte[] data, int size) {
			if (type == AIEngine.AIENGINE_MESSAGE_TYPE_JSON) {

				final String json = new String(data, 0, size).trim(); /* must trim the end '\0' */

				Log.d(TAG, "AIENGINE_MESSAGE_TYPE_JSON json=" + json);

				JSONObject jo = null ;
				int vad_status = -1 ;
				int errId = 0 ;

				//check vad_status
				try{
					jo = new JSONObject(json) ;			
				}catch(JSONException e) {
					Log.e(TAG, "JSONObject fail") ;
					return 0 ;
				}

				try {
					errId = jo.getInt("errId");
					if (errId != 0) {
						stopScore() ;
						Log.e(TAG, "####### aiengine errId=" + errId);	
						//没有注册
						if (errId == 41007) {
							resetEngine() ;
						}
						//41007 not register
						mListener.onCompletion(errId, null);

						return 0 ;
					}
				} catch (JSONException e) {

				}					
				try {
					vad_status = jo.getInt("vad_status");
				} catch (JSONException e) {
					//出错说明不带vad_status
				}

				if (vad_status == 0) {
					//说明还没有录到声音
				}
				else if (vad_status == 1) {
					//说明已经录到声音
				}
				//自动停止
				else if(vad_status == 2)
				{
					//说明已经测评完毕
					if (!mIsStopped) {
						stopScore() ;
					}
				}else{
					//如果不带vad_status，说明结果出来了

					if (mListener != null) {
						//String wavepath = mFilesDir + "/record/" + new String(id).trim() + ".wav";
						String wavepath = mFilesDir + "/record.wav";
						StringBuffer sb = new StringBuffer() ;
						sb.append("{\"wavepath\": \"" + wavepath + "\", ") ;
						sb.append(json.substring(1)) ;

						mListener.onCompletion(0, sb.toString());
					}else{
						Log.e(TAG, "aiengine_callback mListener == null") ;
					}
				}//if vad_status

			}//if type

			return 0;
		}// run()

	}; //callback()


	public AIScorer(Context context) {
		mIsStopped = true;
		mContext = context ;
		mRecorder = new AIRecorder();
		AIEngineHelper.init(context) ;
		new InitEngineTask().execute(0) ;
		mFilesDir =  AIEngineHelper.getFilesDir(mContext).getPath() ;
	}

	private class InitEngineTask extends AsyncTask<Integer, Integer,  Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			Log.d(TAG, "InitEngineTask getEngine ... ") ;
			mEngine = AIEngineHelper.getEngine() ;
			return null;
		}

	}

	public int score(String refText) {

		if (refText == null || refText.trim().length() == 0) {
			Log.e(TAG, "MSG_AISCORE_ERROR_INVALID") ; 
			return ServiceContract.MSG_AISCORE_ERROR_INVALID ;
		}
		if (mEngine == 0) {
			if (!AIEngineHelper.isBusy()) {
				new InitEngineTask().execute(0) ;
			}
			Log.e(TAG, "MSG_AISCORE_ERROR_NOT_READY") ; 
			return ServiceContract.MSG_AISCORE_ERROR_NOT_READY ;
		}
		if (!mIsStopped) {
			Log.e(TAG, "Start a new scoring before stop the runing scorer!") ; 
			stopScore() ;
		}
		int rv = 0;
		byte[] id = new byte[64];
		int rank = 100;

		int refTextType ;
		int wordsCount = refText.split("\\s+").length;
		//if (wordsCount <= 1) {
		if (wordsCount == 0) {

			refTextType = WORD;
			rv = AIEngine.aiengine_start(mEngine, "{\"vadEnable\": 1, \"volumeEnable\": 0, \"coreProvideType\": \"native\", \"app\": {\"userId\": \"" + AIConstants.USERID + "\"}, \"audio\": {\"audioType\": \"wav\", \"channel\": 1, \"sampleBytes\": 2, \"sampleRate\": 16000}, \"request\": {\"coreType\": \"en.word.score\", \"refText\": \"" + refText + "\", \"rank\": " + rank + "}}", id, callback);
		}
		else{
			refTextType = SENTENCE;
			rv = AIEngine.aiengine_start(mEngine, "{\"vadEnable\": 1, \"volumeEnable\": 0, \"coreProvideType\": \"native\", \"app\": {\"userId\": \"" +  AIConstants.USERID + "\"}, \"audio\": {\"audioType\": \"wav\", \"channel\": 1, \"sampleBytes\": 2, \"sampleRate\": 16000}, \"request\": {\"coreType\": \"en.sent.score\", \"refText\": \"" + refText + "\", \"rank\": " + rank + "}}", id, callback);						
		}

		if (rv == -1) {
			AIEngine.aiengine_stop(mEngine);
			Log.e(TAG, "MSG_AISCORE_ERROR_NOT_READY rv= -1") ; 
			return ServiceContract.MSG_AISCORE_ERROR_NOT_READY ;
		}

		Log.d(TAG, "### aiengine_start rv= " + rv);
		//String wavPath = mFilesDir + "/record/" + new String(id).trim() + ".wav";
		String wavPath = mFilesDir + "/record.wav";
		Log.d(TAG, "wavPath: " + wavPath);
		mCount = 0 ;
		mRecorder.start(wavPath, recordCallback, mContext);
		mIsStopped = false;       

		return 0 ;

	}


	public void release() {

		if (mEngine != 0) {
			AIEngine.aiengine_delete(mEngine);
			Log.d(TAG, "engine deleted: " + mEngine);
			mEngine = 0 ;
		}
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder = null ;
		}        
	}

	public void stopScore() {
		if (mRecorder != null) {
			mRecorder.stop();
		}
		if (mEngine != 0) {
			AIEngine.aiengine_stop(mEngine);
		}		            
		mIsStopped = true;
	}

	private void resetEngine() {
		Log.d(TAG, "resetEngine ... ") ;
		if (mEngine != 0) {
			Log.d(TAG, "aiengine_delete ... ") ;
			//AIEngine.aiengine_delete(mEngine);
			Log.d(TAG, "engine deleted: " + mEngine);
			mEngine = 0 ;
		}
		Log.d(TAG, "new InitEngineTask() ... ") ;
		new InitEngineTask().execute(0) ;

	}


}
