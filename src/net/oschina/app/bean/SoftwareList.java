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
 * ����б�ʵ����
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class SoftwareList extends Entity{
	
	public final static String TAG_RECOMMEND = "recommend";//�Ƽ�
	public final static String TAG_LASTEST = "time";//����
	public final static String TAG_HOT = "view";//����
	public final static String TAG_CHINA = "list_cn";//����
	
	private int softwarecount;
	private int pagesize;
	private List<Software> softwarelist = new ArrayList<Software>();
	
	public static class Software implements Serializable {
		public String name;
		public String description;
		public String url;
	}

	public int getSoftwarecount() {
		return softwarecount;
	}
	public void setSoftwarecount(int softwarecount) {
		this.softwarecount = softwarecount;
	}
	public int getPageSize() {
		return pagesize;
	}
	public void setPageSize(int pagesize) {
		this.pagesize = pagesize;
	}
	public List<Software> getSoftwarelist() {
		return softwarelist;
	}
	public void setSoftwarelist(List<Software> softwarelist) {
		this.softwarelist = softwarelist;
	}
	
	public static SoftwareList parse(InputStream inputStream) throws IOException, AppException {
		SoftwareList softwarelist = new SoftwareList();
		Software software = null;
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
			    		if(tag.equalsIgnoreCase("softwarecount")) 
			    		{
			    			softwarelist.setSoftwarecount(StringUtils.toInt(xmlParser.nextText(),0));
			    		}
			    		else if(tag.equalsIgnoreCase("pagesize")) 
			    		{
			    			softwarelist.setPageSize(StringUtils.toInt(xmlParser.nextText(),0));
			    		}
			    		else if (tag.equalsIgnoreCase("software")) 
			    		{ 
			    			software = new Software();
			    		}
			    		else if(software != null)
			    		{	
				            if(tag.equalsIgnoreCase("name"))
				            {			      
				            	software.name = xmlParser.nextText();
				            }
				            else if(tag.equalsIgnoreCase("description"))
				            {			            	
				            	software.description = xmlParser.nextText();
				            }
				            else if(tag.equalsIgnoreCase("url"))
				            {			            	
				            	software.url = xmlParser.nextText();
				            }
				            
			    		}
			            //֪ͨ��Ϣ
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	softwarelist.setNotice(new Notice());
			    		}
			            else if(softwarelist.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				softwarelist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	softwarelist.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	softwarelist.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	softwarelist.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//���������ǩ��������Ѷ�����ӽ�������
				       	if (tag.equalsIgnoreCase("software") && software != null) { 
				       		softwarelist.getSoftwarelist().add(software); 
				       		software = null; 
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
        return softwarelist;       
	}
}
