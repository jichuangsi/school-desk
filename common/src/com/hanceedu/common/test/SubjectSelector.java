package com.hanceedu.common.test;

import com.hanceedu.common.widget.SubjectSelectorDialog;

import android.app.Activity;
import android.os.Bundle;

public class SubjectSelector extends Activity {
    /** Called when the activity is first created. */

	SubjectSelectorDialog mSubjectSelectorDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubjectSelectorDialog = new SubjectSelectorDialog(this,"1,2,9,6,3,11,12");
        mSubjectSelectorDialog.setSubjectSelectorListener(new SubjectSelectorDialog.SubjectSelectorListener() {
			
			
			public void subjectChanged(int selectedSubjectId) {
				// 用做测试，看SelectSubjectDialog中得到的用户选择
				// 的科目ID是否被传出来以供别的类使�?				
				System.out.println("selectedSubjectId:"+selectedSubjectId);
			}
		});
        mSubjectSelectorDialog.show();
    }
}