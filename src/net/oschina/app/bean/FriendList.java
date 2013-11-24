package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.oschina.app.AppException;
import net.oschina.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * �����б�ʵ����
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class FriendList extends Entity{

	public final static int TYPE_FANS = 0x00;
	public final static int TYPE_FOLLOWER = 0x01;
	
	private List<Friend> friendlist = new ArrayList<Friend>();
	
	/**
	 * ����ʵ����
	 */
	public static class Friend implements Serializable {
		private int userid;
		private String name;
		private String face;
		private String expertise;
		private int gender;
		public int getUserid() {return userid;}
		public void setUserid(int userid) {this.userid = userid;}
		public String getName() {return name;}
		public void setName(String name) {this.name = name;}
		public String getFace() {return face;}
		public void setFace(String face) {this.face = face;}
		public String getExpertise() {return expertise;}
		public void setExpertise(String expertise) {this.expertise = expertise;}
		public int getGender() {return gender;}
		public void setGender(int gender) {this.gender = gender;}		
	}

	public List<Friend> getFriendlist() {
		return friendlist;
	}
	public void setFriendlist(List<Friend> resultlist) {
		this.friendlist = resultlist;
	}
	
	public static FriendList parse(InputStream inputStream) throws IOException, AppException {
		FriendList friendlist = new FriendList();
		Friend friend = null;
        //���XmlPullParser������
        XmlPullParser xmlParser = Xml.newPullParser();
        try {        	
            xmlParser.setInput(inputStream, UTF8);
            //��ý��������¼���������п�ʼ�ĵ��������ĵ�����ʼ��ǩ��������ǩ���ı��ȵ��¼���
            int evtType=xmlParser.getEventType();
			//һֱѭ����ֱ���ĵ�����    
			while(evtType!=XmlPullParser.END_DOCUMENT){ 
	    		String tag = xmlParser.getName(); 
			    switch(evtType){ 
			    	case XmlPullParser.START_TAG:
			    		if (tag.equalsIgnoreCase("friend")) 
			    		{ 
			    			friend = new Friend();
			    		}
			    		else if(friend != null)
			    		{	
				            if(tag.equalsIgnoreCase("userid"))
				            {			      
				            	friend.userid = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase("name"))
				            {			            	
				            	friend.name = xmlParser.nextText();
				            }
				            else if(tag.equalsIgnoreCase("portrait"))
				            {			            	
				            	friend.face = xmlParser.nextText();
				            }
				            else if(tag.equalsIgnoreCase("expertise"))
				            {			            	
				            	friend.expertise = xmlParser.nextText();
				            }
				            else if(tag.equalsIgnoreCase("gender"))
				            {			            	
				            	friend.gender = StringUtils.toInt(xmlParser.nextText(),0);
				            }
			    		}
			            //֪ͨ��Ϣ
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	friendlist.setNotice(new Notice());
			    		}
			            else if(friendlist.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				friendlist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	friendlist.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	friendlist.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	friendlist.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//���������ǩ��������Ѷ�����ӽ�������
				       	if (tag.equalsIgnoreCase("friend") && friend != null) { 
				       		friendlist.getFriendlist().add(friend); 
				       		friend = null; 
				       	}
				       	break; 
			    }
			    //���xmlû�н������򵼺�����һ���ڵ�
			    evtType=xmlParser.next();
			}		
        } catch (XmlPullParserException e) {
			throw AppException.xml(e);
        } finally {
        	inputStream.close();	
        }      
        return friendlist;       
	}
}
