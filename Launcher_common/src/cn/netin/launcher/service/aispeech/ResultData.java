package cn.netin.launcher.service.aispeech;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 评分结果的数据类
 * @author Bear_yang
 */
public class ResultData {
	/** 输入WAV的长度，单位:毫秒 */
	public int wavetime = 0;
	/** 识别时间，单位:毫秒 */
	public int systime = 0;
	/** 评分等级，可选值有2,4,100,默认值为4 */
	public int rank = 4;
	/** 发音得分 */
	public int pron = 0;
	/** 准确度评分 */
	public int accuracy = 0;
	/** 完整度评分 */
	public int integrity = 0;
	/** 总体得分，不推荐使用 */
	public int overall = 0;
	
	/** 流利度信息 */
	public Fluency fluency;
	/** 句子中每个单词的评分细节 */
	public List<Details> details = new ArrayList<Details>();
	/** 音素级别发音统计。统计每个音素出现的次数和平均评分 */
	public List<Statics> statics = new ArrayList<Statics>();
	/** 音频信息反馈 */
	public Info info;

	public ResultData(){
		this.fluency = new Fluency();
		this.info = new Info();
	}
	/**
	 * 构造函数
	 * @param data
	 * @throws JSONException
	 */
	public ResultData(String data) throws JSONException{
		
		this(new JSONObject(data));
	}
	
	public ResultData(JSONObject json) {
		try {
			this.wavetime = (Integer) json.get("wavetime");

			this.systime = (Integer) json.get("systime");
			this.rank = (Integer) json.get("rank");
			this.pron = (Integer) json.get("pron");
			this.accuracy = (Integer) json.get("accuracy");
			this.integrity = (Integer) json.get("integrity");
			this.overall = (Integer) json.get("overall");

			this.fluency = new Fluency((JSONObject) json.get("fluency"));

			JSONArray _details = (JSONArray) json.get("details");
			for (int i = 0; i < _details.length(); i++) {
				details.add(new Details((JSONObject) _details.get(i)));
			}

			JSONArray _statics = (JSONArray) json.get("statics");
			for (int i = 0; i < _statics.length(); i++) {
				statics.add(new Statics((JSONObject) _statics.get(i)));
			}

			this.info = new Info((JSONObject) json.get("info"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public ResultData(String data,int i) {
		JSONObject json = null;
		try {
			json = new JSONObject(data);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			this.pron = (Integer) json.get("pron");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 流利度信息 */
	public class Fluency {
		/** 流利度总分 */
		public int overrall = 0;
		/** 停顿，单位：停顿次数 */
		public int pause = 0;
		/** 语速，单位：单词数/分钟 */
		public int speed = 0;
		public Fluency(){}
		public Fluency(JSONObject json) throws JSONException {
			this.overrall = json.getInt("overall");
			this.pause = json.getInt("pause");
			this.speed = json.getInt("speed");
		}
	}
	
	/** 句子中每个单词的评分细节 */
	public class Details {
		/** 单词的评分 */
		public int score = 0;
		/** 单词语音帧长度 */
		public int dur = 0;
		/** 单词的流利度评分 */
		public int fluency = 0;
		/** 单词文本 */
		public String _char = "";
		public Details(){}
		public Details(JSONObject json) throws JSONException {
			score = (Integer) json.getInt("score");
			dur = (Integer) json.getInt("dur");
			fluency = (Integer) json.getInt("fluency");
			_char = json.getString("char");
		}
	}
	
	/** 音素级别发音统计。统计每个音素出现的次数和平均评分 */
	public class Statics {
		/** 音素平均得分 */
		public int score = 0;
		/** 音素出现的次数 */
		public int count = 0;
		/** 音素字符，与国际音标具有一一对应的关系 */
		public String _char = "";
		public Statics(){}
		public Statics(JSONObject json) throws JSONException {
			score = (Integer) json.getInt("score");
			count = (Integer) json.getInt("count");
			_char = json.getString("char");
		}
	}

	/** 音频信息反馈 */
	public class Info {
		/** 音量大小,取值范围[0,32767],小于500可以认为音量太小 */
		public int volume = 0;
		/** 截幅的原始计算值，此值>=0.04则为截幅,否则为非截幅 */
		public double clip = 0.0;
		/** 信噪比，如果此值<3，则音频的信号质量较差，噪声较多 */
		public double snr = 0.0;
		/** 
		 * <b>提示信息</b></p> 
		 * 音频提示信息:</br> 10000，音频数据为0，可提示未录音；</br>
		 * 10001，用户发音不完整，如"i want an apple"，可能只发了"i want an"，可提示发音不完整；</br>
		 * 10002，10003，识别不完整，出现这种情况大部分是静音，及音频偏短，可提示发音不完整；</br>
		 * 10004，音量偏低，可提示用户矫正麦克风，可能位置太远；</br> 10005，音频截幅，可提示用户矫正麦克风，可能位置太近；</br>
		 * 10006，音频质量偏差，噪声较多，可提示用户录音环境嘈杂，或麦克风质量偏差；</p>
		 *
		 * 错误:</br> 1, 系统未初始化，此值实际评测中不会出现;</br> 5, 单词、整句参考文本非法; </br>6, 识别ebnf文本非法;
		 */
		public long tipId = 0;
		public Info(){}
		public Info(JSONObject json) throws JSONException {
			this.volume = json.getInt("volume");
			this.clip = json.getDouble("clip");
			this.snr = json.getDouble("snr");
			this.tipId = json.getLong("tipId");
		}
	}
	
}
