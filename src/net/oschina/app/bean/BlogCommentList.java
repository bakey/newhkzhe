package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.oschina.app.AppException;
import net.oschina.app.bean.Comment.Refer;
import net.oschina.app.bean.Comment.Reply;
import net.oschina.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * ���������б�ʵ����
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class BlogCommentList extends Entity{
	
	private int pageSize;
	private int allCount;
	private List<Comment> commentlist = new ArrayList<Comment>();
	
	public int getPageSize() {
		return pageSize;
	}
	public int getAllCount() {
		return allCount;
	}
	public List<Comment> getCommentlist() {
		return commentlist;
	}

	public static BlogCommentList parse(InputStream inputStream) throws IOException, AppException {
		BlogCommentList commlist = new BlogCommentList();
		Comment comm = null;
		Reply reply = null;
		Refer refer = null;
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
			    		if(tag.equalsIgnoreCase("allCount")) 
			    		{
			    			commlist.allCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("pageSize")) 
			    		{
			    			commlist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase("comment")) 
			    		{ 
			    			comm = new Comment();
			    		}
			    		else if(comm != null)
			    		{	
				            if(tag.equalsIgnoreCase("id"))
				            {			      
				            	comm.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase("portrait"))
				            {			            	
				            	comm.setFace(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("author"))
				            {			            	
				            	comm.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase("authorid"))
				            {			            	
				            	comm.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase("content"))
				            {			            	
				            	comm.setContent(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("pubDate"))
				            {			            	
				            	comm.setPubDate(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase("appclient"))
				            {			            	
				            	comm.setAppClient(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("reply"))
				            {			            	
				            	reply = new Reply();         	
				            }
				            else if(reply!=null && tag.equalsIgnoreCase("rauthor"))
				            {
				            	reply.rauthor = xmlParser.nextText();
				            }
				            else if(reply!=null && tag.equalsIgnoreCase("rpubDate"))
				            {
				            	reply.rpubDate = xmlParser.nextText();
				            }
				            else if(reply!=null && tag.equalsIgnoreCase("rcontent"))
				            {
				            	reply.rcontent = xmlParser.nextText();
				            }
				            else if(tag.equalsIgnoreCase("refer"))
				            {			            	
				            	refer = new Refer();         	
				            }
				            else if(refer!=null && tag.equalsIgnoreCase("refertitle"))
				            {
				            	refer.refertitle = xmlParser.nextText();
				            }
				            else if(refer!=null && tag.equalsIgnoreCase("referbody"))
				            {
				            	refer.referbody = xmlParser.nextText();
				            }
			    		}
			            //֪ͨ��Ϣ
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	commlist.setNotice(new Notice());
			    		}
			            else if(commlist.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				commlist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	commlist.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	commlist.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	commlist.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//���������ǩ��������Ѷ�����ӽ�������
				       	if (tag.equalsIgnoreCase("comment") && comm != null) { 
				       		commlist.getCommentlist().add(comm); 
				       		comm = null; 
				       	}
				       	else if (tag.equalsIgnoreCase("reply") && comm!=null && reply!=null) { 
				       		comm.getReplies().add(reply);
				       		reply = null; 
				       	}
				       	else if(tag.equalsIgnoreCase("refer") && comm!=null && refer!=null) {
				       		comm.getRefers().add(refer);
				       		refer = null;
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
        return commlist;       
	}
}
