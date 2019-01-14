package com.hanceedu.common.util;

/**
 * 
 * @author wjx
 *存储keyboard-resource.xml文件中数据的类
 */
public class KeyInfo
{
	
	/*key标签的id属性值*/
	private String keyId;
	
	/*key标签的左上角x坐标属性值*/
	private float left;
	
	/*key标签的左上角y坐标属性值*/
	private float top;
	
	/*key标签的右下角x坐标属性值*/
	private float right;
	
	/*key标签的右下角y坐标属性值*/
	private float bottom;
	
	/*key标签的对应的图片id属性值*/
	private String sourceId;

	public String getKeyId()
	{
		return keyId;
	}

	public void setKeyId(String keyId)
	{
		this.keyId = keyId;
	}

	public float getLeft()
	{
		return left;
	}

	public void setLeft(float left)
	{
		this.left = left;
	}

	public float getTop()
	{
		return top;
	}

	public void setTop(float top)
	{
		this.top = top;
	}

	public float getRight()
	{
		return right + left;
	}

	public void setRight(float right)
	{
		this.right = right;
	}

	public float getBottom()
	{
		return bottom + top;
	}

	public void setBottom(float bottom)
	{
		this.bottom = bottom;
	}

	public String getSourceId()
	{
		return sourceId;
	}

	public void setSourceId(String sourceId)
	{
		this.sourceId = sourceId;
	}

	@Override
	public String toString()
	{
		return "KeyInfo [keyId=" + keyId + ", left=" + left + ", top=" + top
				+ ", right=" + right + ", bottom=" + bottom + ", sourceId="
				+ sourceId + "]";
	}

}
