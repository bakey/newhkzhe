package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.oschina.app.AppException;
import net.oschina.app.bean.Active.ObjectReply;
import net.oschina.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * ��̬�б�ʵ����
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ActiveList extends Entity{

	public final static int CATALOG_LASTEST = 1;//����
	public final static int CATALOG_ATME = 2;//@��
	public final static int CATALOG_COMMENT = 3;//����
	public final static int CATALOG_MYSELF = 4;//���Լ�
	
	private int pageSize;
	private int activeCount;
	private List<Active> activelist = new ArrayList<Active>();
	
	public int getPageSize() {
		return pageSize;
	}
	public int getActiveCount() {
		return activeCount;
	}
	public List<Active> getActivelist() {
		return activelist;
	}

	public static ActiveList parse(InputStream inputStream) throws IOException, AppException {
		ActiveList activelist = new ActiveList();
		Active active = null;
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
			    		if(tag.equalsIgnoreCase("activeCount")) 
			    		{
			    			activelist.activeCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("pageSize")) 
			    		{
			    			activelist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase("active")) 
			    		{ 
			    			active = new Active();
			    		}
			    		else if(active != null)
			    		{	
				            if(tag.equalsIgnoreCase("id"))
				            {			      
				            	active.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase("portrait"))
				            {			            	
				            	active.setFace(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("message"))
				            {			            	
				            	active.setMessage(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase("author"))
				            {			            	
				            	active.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase("authorid"))
				            {			            	
				            	active.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase("catalog"))
				            {			            	
				            	active.setActiveType(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("objectID"))
				            {			            	
				            	active.setObjectId(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("objecttype"))
				            {			            	
				            	active.setObjectType(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("objectcatalog"))
				            {			            	
				            	active.setObjectCatalog(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("objecttitle"))
				            {			            	
				            	active.setObjectTitle(xmlParser.nextText());			            	
				            }
				            else if(tag.equalsIgnoreCase("objectreply"))
				            {			            	
				            	active.setObjectReply(new ObjectReply());	            	
				            }
				            else if(active.getObjectReply()!=null && tag.equalsIgnoreCase("objectname"))
				            {			            	
				            	active.getObjectReply().objectName = xmlParser.nextText();		            	
				            }
				            else if(active.getObjectReply()!=null && tag.equalsIgnoreCase("objectbody"))
				            {			            	
				            	active.getObjectReply().objectBody = xmlParser.nextText();		            	
				            }
				            else if(tag.equalsIgnoreCase("commentCount"))
				            {			            	
				            	active.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("pubDate"))
				            {			            	
				            	active.setPubDate(xmlParser.nextText());			            	
				            }
				            else if(tag.equalsIgnoreCase("tweetimage"))
				            {			            	
				            	active.setTweetimage(xmlParser.nextText());			            	
				            }
				            else if(tag.equalsIgnoreCase("appclient"))
				            {			            	
				            	active.setAppClient(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("url"))
				            {			            	
				            	active.setUrl(xmlParser.nextText());			            	
				            }
			    		}
			            //֪ͨ��Ϣ
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	activelist.setNotice(new Notice());
			    		}
			            else if(activelist.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				activelist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	activelist.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	activelist.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	activelist.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//���������ǩ��������Ѷ�����ӽ�������
				       	if (tag.equalsIgnoreCase("active") && active != null) { 
				       		activelist.getActivelist().add(active); 
				       		active = null; 
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
        return activelist;       
	}
}
