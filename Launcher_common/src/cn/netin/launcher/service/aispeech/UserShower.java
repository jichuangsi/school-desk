package cn.netin.launcher.service.aispeech;

import org.json.JSONException;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

/**
 * 专门用来返回展示数据的类
 * @author Bear_yang
 */
public class UserShower {
	
	/**
	 * @param refText
	 *            与录音对比评分的句子
	 * @param result
	 *            存有JSON结构评分结果的result String
	 * @return 对refText根据result评分而着色后的结果
	 */
	public static SpannableString spannableResult(String refText, String result) {
		// 创建一个 SpannableString对象
		SpannableString sp = new SpannableString(refText);
		// 记录mRefText位置,用来对单个单词着色
		int refIndex = 0;
		try {
			// 把Score result转成ResultData
			System.out.println("result = " + result);
			ResultData data = new ResultData(result);
			for (int i = 0; i < data.details.size(); i++) {
				int _score = data.details.get(i).score;
				int _fluency = data.details.get(i).fluency;
				String _char = data.details.get(i)._char;
				// 根据分数不同,给不同的单词不同的颜色
				if (_score + _fluency < 7) {  
					sp.setSpan(new BackgroundColorSpan(Color.RED), refIndex,
							refIndex += _char.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				} else if (_score  + _fluency < 8) {
					sp.setSpan(new ForegroundColorSpan(Color.YELLOW), refIndex,
							refIndex += _char.length(),
							Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				} else {
					sp.setSpan(new ForegroundColorSpan(Color.GREEN), refIndex,
							refIndex += _char.length(),
							Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				}

				if (refIndex < result.length()) {
					refIndex++;// 如果没有走到句子结束,则位置加1表示空格
				}
			}

//			System.out.println("details: " + details.toString(2));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return sp;
	}
}