package cn.netin.launcher.service.aispeech;

import java.util.Hashtable;

public class EnScoreMap {
	/*
	 * AISpeech API provides AI Speech Ltd's (www.aispeech.com) world leading
	 * pronunciation evaluation,speech recognition technologies, and
	 * text-to-speech technologies.
	 * 
	 * AI Speech Ltd opens this API hoping make every programmer be capable to
	 * implement speech enabled applicaitons.
	 * 
	 * Copyright (c) 2008-2011 by AISpeech. All rights reserved.
	 */
	/**
	 * AISpeech API英文评分反馈结果中音素和国际音标的对应关系,<br>
	 * 第一列是AISpeech API的反馈，第二列为国际音标。<br>
	 * 该映射对应的英文词语内核为AIScoreEn_v1.4.1<br>
	 * $Id,EnScoreMap.js 1995 2011-12-02 05:58:18Z zhiyuan.liang $
	 * 
	 * @namespace AISpeech API英文评分反馈结果中音素和国际音标的映射表
	 */
	final static Hashtable<String, String> EnScoreMap = new Hashtable<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5324233709007349191L;

		{
			/**
			 * @name aispeech.EnScoreMap.ih
			 * @description ɪ
			 */
			put("ih", "ɪ");
			/**
			 * @name aispeech.EnScoreMap.ax
			 * @description ə
			 */
			put("ax", "ə");
			/**
			 * @name aispeech.EnScoreMap.oh
			 * @description ɒ
			 */
			put("oh", "ɒ");
			/**
			 * @name aispeech.EnScoreMap.uh
			 * @description ʊ
			 */
			put("uh", "ʊ");
			/**
			 * @name aispeech.EnScoreMap.ah
			 * @description ʌ
			 */
			put("ah", "ʌ");
			/**
			 * @name aispeech.EnScoreMap.eh
			 * @description e
			 */
			put("eh", "e");
			/**
			 * @name aispeech.EnScoreMap.ae
			 * @description æ
			 */
			put("ae", "æ");
			/**
			 * @name aispeech.EnScoreMap.iy
			 * @description i:
			 */
			put("iy", "i:");
			/**
			 * @name aispeech.EnScoreMap.er
			 * @description ɜ:
			 */
			put("er", "ɜ:");
			/**
			 * @name aispeech.EnScoreMap.axr
			 * @description ɚ
			 */
			put("axr", "ɚ");
			/**
			 * @name aispeech.EnScoreMap.ao
			 * @description ɔ:
			 */
			put("ao", "ɔ:");
			/**
			 * @name aispeech.EnScoreMap.uw
			 * @description u:
			 */
			put("uw", "u:");
			/**
			 * @name aispeech.EnScoreMap.aa
			 * @description ɑ:
			 */
			put("aa", "ɑ:");
			/**
			 * @name aispeech.EnScoreMap.ey
			 * @description eɪ
			 */
			put("ey", "eɪ");
			/**
			 * @name aispeech.EnScoreMap.ay
			 * @description aɪ
			 */
			put("ay", "aɪ");
			/**
			 * @name aispeech.EnScoreMap.oy
			 * @description ɔɪ
			 */
			put("oy", "ɔɪ");
			/**
			 * @name aispeech.EnScoreMap.aw
			 * @description aʊ
			 */
			put("aw", "aʊ");
			/**
			 * @name aispeech.EnScoreMap.ow
			 * @description әʊ
			 */
			put("ow", "әʊ");
			/**
			 * @name aispeech.EnScoreMap.ia
			 * @description ɪə
			 */
			put("ia", "ɪə");
			/**
			 * @name aispeech.EnScoreMap.ea
			 * @description ɛә
			 */
			put("ea", "ɛә");
			/**
			 * @name aispeech.EnScoreMap.ua
			 * @description ʊə
			 */
			put("ua", "ʊə");

			/**
			 * @name aispeech.EnScoreMap.p
			 * @description p
			 */
			put("p", "p");
			/**
			 * @name aispeech.EnScoreMap.b
			 * @description b
			 */
			put("b", "b");
			/**
			 * @name aispeech.EnScoreMap.t
			 * @description t
			 */
			put("t", "t");
			/**
			 * @name aispeech.EnScoreMap.d
			 * @description d
			 */
			put("d", "d");
			/**
			 * @name aispeech.EnScoreMap.k
			 * @description k
			 */
			put("k", "k");
			/**
			 * @name aispeech.EnScoreMap.g
			 * @description g
			 */
			put("g", "g");
			/**
			 * @name aispeech.EnScoreMap.l
			 * @description l
			 */
			put("l", "l");
			/**
			 * @name aispeech.EnScoreMap.r
			 * @description r
			 */
			put("r", "r");
			/**
			 * @name aispeech.EnScoreMap.m
			 * @description m
			 */
			put("m", "m");
			/**
			 * @name aispeech.EnScoreMap.n
			 * @description n
			 */
			put("n", "n");
			/**
			 * @name aispeech.EnScoreMap.ng
			 * @description ŋ
			 */
			put("ng", "ŋ");
			/**
			 * @name aispeech.EnScoreMap.hh
			 * @description h
			 */
			put("hh", "h");
			/**
			 * @name aispeech.EnScoreMap.s
			 * @description s
			 */
			put("s", "s");
			/**
			 * @name aispeech.EnScoreMap.z
			 * @description z
			 */
			put("z", "z");
			/**
			 * @name aispeech.EnScoreMap.th
			 * @description θ
			 */
			put("th", "θ");
			/**
			 * @name aispeech.EnScoreMap.dh
			 * @description ð
			 */
			put("dh", "ð");
			/**
			 * @name aispeech.EnScoreMap.f
			 * @description f
			 */
			put("f", "f");
			/**
			 * @name aispeech.EnScoreMap.v
			 * @description v
			 */
			put("v", "v");
			/**
			 * @name aispeech.EnScoreMap.w
			 * @description w
			 */
			put("w", "w");
			/**
			 * @name aispeech.EnScoreMap.y
			 * @description j
			 */
			put("y", "j");
			/**
			 * @name aispeech.EnScoreMap.sh
			 * @description ʃ
			 */
			put("sh", "ʃ");
			/**
			 * @name aispeech.EnScoreMap.zh
			 * @description ʒ
			 */
			put("zh", "ʒ");
			/**
			 * @name aispeech.EnScoreMap.ch
			 * @description tʃ
			 */
			put("ch", "tʃ");
			/**
			 * @name aispeech.EnScoreMap.jh
			 * @description dʒ
			 */
			put("jh", "dʒ");
		}
	};

	/**
	 * 根据映射字符获得国际音标。如果没找到，则返回原字符串
	 * 
	 * @param {string} key
	 * @return {string}
	 */
	public static String get(String key) {
		if (key != null) {
			return EnScoreMap.get(key);
		}
		return key;
	}
}
