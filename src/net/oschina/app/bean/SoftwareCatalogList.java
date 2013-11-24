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
 * ��������б�ʵ����
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class SoftwareCatalogList extends Entity{
	
	private int softwarecount;
	private List<SoftwareType> softwaretypelist = new ArrayList<SoftwareType>();
	
	public static class SoftwareType implements Serializable {
		public String name;
		public int tag;
	}

	public int getSoftwarecount() {
		return softwarecount;
	}
	public void setSoftwarecount(int softwarecount) {
		this.softwarecount = softwarecount;
	}
	public List<SoftwareType> getSoftwareTypelist() {
		return softwaretypelist;
	}
	public void setSoftwareTypelist(List<SoftwareType> softwaretypelist) {
		this.softwaretypelist = softwaretypelist;
	}
	
	public static SoftwareCatalogList parse(InputStream inputStream) throws IOException, AppException {
		SoftwareCatalogList softwarecatalogList = new SoftwareCatalogList();
		SoftwareType softwaretype = null;
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
			    			softwarecatalogList.setSoftwarecount(StringUtils.toInt(xmlParser.nextText(),0));
			    		}
			    		else if (tag.equalsIgnoreCase("softwareType")) 
			    		{ 
			    			softwaretype = new SoftwareType();
			    		}
			    		else if(softwaretype != null)
			    		{	
				            if(tag.equalsIgnoreCase("name"))
				            {			      
				            	softwaretype.name = xmlParser.nextText();
				            }
				            else if(tag.equalsIgnoreCase("tag"))
				            {			            	
				            	softwaretype.tag = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            
			    		}
			            //֪ͨ��Ϣ
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	softwarecatalogList.setNotice(new Notice());
			    		}
			            else if(softwarecatalogList.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				softwarecatalogList.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	softwarecatalogList.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	softwarecatalogList.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	softwarecatalogList.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//���������ǩ��������Ѷ�����ӽ�������
				       	if (tag.equalsIgnoreCase("softwareType") && softwaretype != null) { 
				       		softwarecatalogList.getSoftwareTypelist().add(softwaretype); 
				       		softwaretype = null; 
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
        return softwarecatalogList;       
	}
}
