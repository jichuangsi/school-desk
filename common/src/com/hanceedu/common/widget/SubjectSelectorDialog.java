package com.hanceedu.common.widget;

import java.lang.reflect.Field;
import java.util.Arrays;


import com.hanceedu.common.R;
import com.hanceedu.common.util.DrawableUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

/** 科目选择对话框 
 * 完全通过xml资源文件，在显示标题和资源ID之间进行适配，具体的做法是，在res/values/arrays.xml 中写两个数组，
 * 一个是显示标题，一个是对应的资源ID字符串，程序通过数组的下标将两者关联。同时，在逻辑实现里，根据资源ID字符串得到
 * 真正的资源ID。这样，就实现了程序中不涉及具体的资源ID，因而，带来更高的复用性。
 * 可广泛用于各种菜单。
 * 
 * 实现的过程：根据构造函数中传入的参数subjectIdStr，到上述两个数组中取相应的文本和图片。用户点击某个项目时，获得position,
 * 根据postion换算到对应的Item的下标，然后通过接口通知调用者选择的Item的下标
 * */
public class SubjectSelectorDialog extends Dialog {
	private GridView mGridview;
	private Context mContext;

	/** 所有科目图片的resource Id数组 */
	private  int[] resouceIds;
	
	/** 所有科目图片的resource Id字符串数组 */
	private  String[] resouceIdsStrings  ;
	
	/** 保存用户所选择的科目ID,默认值为0，表示未知 */
	int selectedSubjectId = 0;
	
	/** 可选的科目数组,由构造函数中subjectIdStr转换而来 */
	int[] selectableSubjectIds;
	
	/** 所有科目名称 */
	private  String[] allSubjects;
	
	/** 用以判断allSubjects[]是否被赋值 */
	private boolean isInitialized = false ;
	
	/**无效的资源ID*/
	private static final int INVALID_RESOURCEID = -1 ;

	private SubjectSelectorListener subjectSelectorListener;
	

	/**
	 * @param context
	 * @param subjectIdStr 形如“1,2,3”的字符串，代表放入菜单中的项目的数组下标
	 */
	public SubjectSelectorDialog(Context context, String subjectIdStr) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏显示
		setContentView(com.hanceedu.common.R.layout.subjectselector);
		
		//点击dialog之外，dialgo就消失
		this.setCanceledOnTouchOutside(true);
		
		mContext = context;
		mGridview = (GridView) findViewById(R.id.subjectselector_gridview);
		if (mGridview == null) {
			System.out.println("mGridview == null");
		}

		
		//将"1,2,3"形式的科目ID字符串转成数组
		selectableSubjectIds = splitStringToIntArray(subjectIdStr); 

		initResources() ;

		mGridview.setOnItemClickListener(new GirdItemClickListener());

		mGridview.setAdapter(new ImageAdapter(context));

		isInitialized = true ;
	}
	
	class GirdItemClickListener implements OnItemClickListener{

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO 用户点击Item的事件实现，暂时只获取了用户选择的科目ID
			selectedSubjectId = selectableSubjectIds[position];
			subjectSelectorListener.subjectChanged(selectedSubjectId);
			//Toast.makeText(mContext, getSubject(selectedSubjectId) + " ID："+selectedSubjectId, Toast.LENGTH_SHORT).show();
			SubjectSelectorDialog.this.dismiss() ;
		}
	}

	
	/**
	 * 取得相关资源
	 *
	 */
	private void initResources() {
//Resources resource = mGridview.getResources();
		
		Resources resource = mContext.getResources();
		
		//取得所有科目图片的resource Id数组
		resouceIdsStrings = resource.getStringArray(R.array.subjectSelector_resourceIds);
		
		//取得所有科目名称的字符串数组
		allSubjects = resource.getStringArray(R.array.subjectSelector_subjects);
		
			
		resouceIds = new int[selectableSubjectIds.length];
		
		//遍历可选择科目数组，获得相应的resource Id
		for (int i = 0; i < selectableSubjectIds.length; i++) {
			int subjectId = selectableSubjectIds[i] ;
			
			//取resource Id 的字符串
			String resourceIdString = resouceIdsStrings[subjectId] ;
			
			
			//根据resource Id 字符串找到实际的 resource Id 值
			resouceIds[i] = getResourceId2(resourceIdString);
			

		}
	}
	
	
	/**
	 * 用以把用户选择的科目ID传出去的接口
	 * @author Bear_yang
	 */
	public interface SubjectSelectorListener {
		/**
		 * @param id 用户选择科目的ID
		 */
		void subjectChanged(int selectedSubjectId);
	}
	
	//private OnSelectSubjectListener onSelectSubjectListener;
	
	public void setSubjectSelectorListener(SubjectSelectorListener listener){
		this.subjectSelectorListener = listener;
	}
	

	
	/**
	 * 将形如“1,2,3”的字符串 拆分成 int[]
	 */
	private int[] splitStringToIntArray(String s) {
		String[] sa = s.split(","); //将useSubjectId以","分离成字符串数组
		int[] ia = new int[sa.length];
		
		int temInt ;
		for (int i = 0; i < sa.length; i++) {
			temInt = Integer.parseInt(sa[i]);
			
			//防止下标temInt越界
			//if (temInt < allSubjects.length) {
				ia[i] = temInt ;
			//}

		}
		return ia;
	}

	/**
	 * 根据id 得到名称
	 * 
	 * @param subjectId
	 * @return
	 */
	public  String getSubject(int subjectId) {
		if (!isInitialized) {
			return null ;
		}
		return allSubjects[subjectId];
	}

	/**
	 * 根据resource Id 字符串找到实际的 resource Id 值
	 * 
	 * @param resourceIdStr
	 * @return SubjectId
	 */
	public  int getResourceId(String resourceIdStr) {
		if (resourceIdStr == null || resourceIdStr.trim().equals("")) {
			return INVALID_RESOURCEID;
		}
		
		//取类，类似于ClassForName
		Class<?> drawableClass = R.drawable.class;
		try {
			
			//在指定的类里面查找某个变量名字
			Field field = drawableClass.getDeclaredField(resourceIdStr);
			return field.getInt(resourceIdStr);
		} catch (SecurityException e) {
			return INVALID_RESOURCEID ;
		} catch (NoSuchFieldException e) {
			return INVALID_RESOURCEID ;
		} catch (IllegalArgumentException e) {
			return INVALID_RESOURCEID ;
		} catch (IllegalAccessException e) {
			return INVALID_RESOURCEID ;
		}
	}
	
	public  int getResourceId2(String resourceIdStr) {
		if (resourceIdStr == null || resourceIdStr.trim().equals("")) {
			return INVALID_RESOURCEID;
		}
		
		if (resourceIdStr.equals("subject_unknown")) {
			return R.drawable.subject_unknown ;
		}
		else if (resourceIdStr.equals("subject_chinese")) {
			return R.drawable.subject_chinese ;
		}
		else if (resourceIdStr.equals("subject_maths")) {
			return R.drawable.subject_maths ;
		}
		else if (resourceIdStr.equals("subject_english")) {
			return R.drawable.subject_english ;
		}
		else if (resourceIdStr.equals("subject_physics")) {
			return R.drawable.subject_physics ;
		}
		else if (resourceIdStr.equals("subject_chemisty")) {
			return R.drawable.subject_chemisty ;
		}
		else if (resourceIdStr.equals("subject_biology")) {
			return R.drawable.subject_biology ;
		}
		else if (resourceIdStr.equals("subject_history")) {
			return R.drawable.subject_history ;
		}
		else if (resourceIdStr.equals("subject_geography")) {
			return R.drawable.subject_geography ;
		}		
		else if (resourceIdStr.equals("subject_political")) {
			return R.drawable.subject_political ;
		}
		else if (resourceIdStr.equals("subject_science")) {
			return R.drawable.subject_science ;
		}
		else if (resourceIdStr.equals("subject_other")) {
			return R.drawable.subject_other ;
		}
		else if (resourceIdStr.equals("subject_all")) {
			return R.drawable.subject_all ;
		}

		return INVALID_RESOURCEID ;
	}
	
	/**
	 * 根据subject Id 找到实际的 resource Id 值
	 * 
	 * @param resourceIdStr
	 * @return SubjectId
	 */
	public int getResourceId(int subjectId) {
		
		String resouceIdString =  resouceIdsStrings[subjectId] ;
		
		return  getResourceId(resouceIdString) ;

	}
	
	

	/*
	 * GridView的适配器，关键是要定义item对应的View
	 */
	public class ImageAdapter extends BaseAdapter {
		private Context context;

		private LayoutInflater inflater;
		
		public ImageAdapter(Context c) {
			context = c;
			this.inflater=LayoutInflater.from(context);
		}

		/**
		 * key:展示item的数目
		 */
		public int getCount() {
			return selectableSubjectIds.length;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return selectableSubjectIds[position];
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView subjectselector_label = null;
			if (convertView == null) { // if it's not recycled, initialize some

				convertView = inflater.inflate(R.layout.subjectselector_item, null);

				subjectselector_label = (TextView)convertView.findViewById(R.id.subjectselector_label);

				
				//判断图片放在哪个方位，然后把图片放到同样的方位.
				//getCompoundDrawables: TextView中图片和文字的位置关系。取得对应于文字LTRB四个方位的图片，无图片的方位为null
				Drawable[] drawables = subjectselector_label.getCompoundDrawables() ;
				for (int i = 0 ; i < 4 ; i++) {
					
					//这个方位有图片，把图片替换。似乎没必要改变图片大小
					if(drawables[i] != null){

						drawables[i] = mContext.getResources().getDrawable(resouceIds[position]) ;
					}
					//System.out.println("drawables["+i+"]" +drawables[i]);
				}
				
				//重新设置图片，对应于getter: getCompoundDrawables
				subjectselector_label.setCompoundDrawablesWithIntrinsicBounds(
						drawables[0],drawables[1],drawables[2],drawables[3]);
				convertView.setTag(subjectselector_label);
			} else {
				subjectselector_label = (TextView)convertView.getTag(); 
			}
			
			//设置标题
			subjectselector_label.setText(getSubject(selectableSubjectIds[position]));
			
			return convertView;
		}
	}
	

}
