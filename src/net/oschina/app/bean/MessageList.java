package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.oschina.app.AppException;
import net.oschina.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * ��Ϣ�б�ʵ����
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class MessageList extends Entity{

	private int pageSize;
	private int messageCount;
	private List<Messages> messagelist = new ArrayList<Messages>();
	
	public int getPageSize() {
		return pageSize;
	}
	public int getMessageCount() {
		return messageCount;
	}
	public List<Messages> getMessagelist() {
		return messagelist;
	}

	public static MessageList parse(InputStream inputStream) throws IOException, AppException {
		MessageList msglist = new MessageList();
		Messages msg = null;
        //���XmlPullParser������
        XmlPullParser xmlParser = Xml.newPullParser();
        try {        	
            xmlParser.setInput(inputStream, UTF8);
            //��ý��������¼���������п�ʼ�ĵ��������ĵ�����ʼ��ǩ��������ǩ���ı��ȵ��¼���
            int evtType=xmlParser.getEventType();
			//һֱѭ����ֱ���ĵ�����    
			while(evtType!=XmlPullParser.END_DOCUMENT){ 
	    		String tag = xmlParser.getName(); 
	    		int depth = xmlParser.getDepth();
			    switch(evtType){ 
			    	case XmlPullParser.START_TAG:
			    		if(depth==2 && tag.equalsIgnoreCase("messageCount")) 
			    		{
			    			msglist.messageCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("pageSize")) 
			    		{
			    			msglist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase("message")) 
			    		{ 
			    			msg = new Messages();
			    		}
			    		else if(msg != null)
			    		{	
				            if(tag.equalsIgnoreCase("id"))
				            {			      
				            	msg.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase("portrait"))
				            {			            	
				            	msg.setFace(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("friendid"))
				            {
				            	msg.setFriendId(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("friendname"))
				            {
				            	msg.setFriendName(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("content"))
				            {			            	
				            	msg.setContent(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase("sender"))
				            {			            	
				            	msg.setSender(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase("senderid"))
				            {			            	
				            	msg.setSenderId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(depth==4 && tag.equalsIgnoreCase("messageCount"))
				            {			            	
				            	msg.setMessageCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("pubDate"))
				            {			            	
				            	msg.setPubDate(xmlParser.nextText());     	
				            }
			    		}
			            //֪ͨ��Ϣ
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	msglist.setNotice(new Notice());
			    		}
			            else if(msglist.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				msglist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	msglist.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	msglist.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	msglist.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//���������ǩ��������Ѷ�����ӽ�������
				       	if (tag.equalsIgnoreCase("message") && msg != null) { 
				       		msglist.getMessagelist().add(msg); 
				       		msg = null; 
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
        return msglist;       
	}
}
