package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;

import net.oschina.app.AppException;
import net.oschina.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * �ҵĸ�����Ϣʵ����
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class MyInformation extends Entity{
	
	public static int GENDER_MAN = 1;
	public static int GENDER_WOMAN = 2;

	private String name;
	private String face;
	private String jointime;
	private int gender;
	private String from;
	/*private String devplatform;
	private String expertise;*/
	private String m_user_interest_area;
	private String m_user_level;

	private int favoritecount;
	private int fanscount;
	private int followerscount;
	
	public String getJointime() {
		return jointime;
	}
	public void setJointime(String jointime) {
		this.jointime = jointime;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public String getInterestArea() {
		return m_user_interest_area;
	}
	public void setInterestArea( String area ) {
		this.m_user_interest_area = area;		
	}
	public String getUserLevel() {
		return m_user_level;
	}
	public void setUserLevel( String level ) {
		this.m_user_level = level;		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFace() {
		return face;
	}
	public void setFace(String face) {
		this.face = face;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public int getFavoritecount() {
		return favoritecount;
	}
	public void setFavoritecount(int favoritecount) {
		this.favoritecount = favoritecount;
	}
	public int getFanscount() {
		return fanscount;
	}
	public void setFanscount(int fanscount) {
		this.fanscount = fanscount;
	}
	public int getFollowerscount() {
		return followerscount;
	}
	public void setFollowerscount(int followerscount) {
		this.followerscount = followerscount;
	}
	
	public static MyInformation parse(InputStream stream) throws IOException, AppException {
		MyInformation user = null;
		// ���XmlPullParser������
		XmlPullParser xmlParser = Xml.newPullParser();
		try {
			xmlParser.setInput(stream, Base.UTF8);
			// ��ý��������¼���������п�ʼ�ĵ��������ĵ�����ʼ��ǩ��������ǩ���ı��ȵ��¼���
			int evtType = xmlParser.getEventType();
			// һֱѭ����ֱ���ĵ�����
			while (evtType != XmlPullParser.END_DOCUMENT) {
				String tag = xmlParser.getName();
				switch (evtType) {

				case XmlPullParser.START_TAG:
					// ����Ǳ�ǩ��ʼ����˵����Ҫʵ����������
					if(tag.equalsIgnoreCase("user")){
						user = new MyInformation();
					}else if(user != null) {
						if(tag.equalsIgnoreCase("name")){
							user.setName(xmlParser.nextText());
						}else if(tag.equalsIgnoreCase("portrait")){
							user.setFace(xmlParser.nextText());
						}else if(tag.equalsIgnoreCase("jointime")){
							user.setJointime(xmlParser.nextText());
						}else if(tag.equalsIgnoreCase("gender")){
							user.setGender(StringUtils.toInt(xmlParser.nextText(), 0));
						}else if(tag.equalsIgnoreCase("from")){
							user.setFrom(xmlParser.nextText());
						}else if(tag.equalsIgnoreCase("favoritecount")){
							user.setFavoritecount(StringUtils.toInt(xmlParser.nextText(), 0));
						}else if(tag.equalsIgnoreCase("fanscount")){
							user.setFanscount(StringUtils.toInt(xmlParser.nextText(), 0));
						}else if(tag.equalsIgnoreCase("followerscount")){
							user.setFollowerscount(StringUtils.toInt(xmlParser.nextText(), 0));
						}
			            //֪ͨ��Ϣ
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	user.setNotice(new Notice());
			    		}
			            else if(user.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				user.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	user.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	user.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	user.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				// ���xmlû�н������򵼺�����һ���ڵ�
				evtType = xmlParser.next();
			}

		} catch (XmlPullParserException e) {
			throw AppException.xml(e);
		} finally {
			stream.close();
		}
		return user;
	}
}
