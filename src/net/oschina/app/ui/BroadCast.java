package net.oschina.app.ui;

import com.hkzhe.app.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * ֪ͨ��Ϣ�㲥������
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-4-16
 */
public class BroadCast extends BroadcastReceiver {

	private final static int NOTIFICATION_ID = R.layout.main;
	
	private static int lastNoticeCount;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String ACTION_NAME = intent.getAction();
		if("net.oschina.app.action.APPWIDGET_UPDATE".equals(ACTION_NAME))
		{	
			int atmeCount = intent.getIntExtra("atmeCount", 0);//@��
			int msgCount = intent.getIntExtra("msgCount", 0);//����
			int reviewCount = intent.getIntExtra("reviewCount", 0);//����
			int newFansCount = intent.getIntExtra("newFansCount", 0);//�·�˿
			int activeCount = atmeCount + reviewCount + msgCount + newFansCount;//��Ϣ����
			
			//��̬-����
			if(Main.bv_active != null){
				if(activeCount > 0){
					Main.bv_active.setText(activeCount+"");
					Main.bv_active.show();
				}else{
					Main.bv_active.setText("");
					Main.bv_active.hide();
				}
			}
			//@��
			if(Main.bv_atme != null){
				if(atmeCount > 0){
					Main.bv_atme.setText(atmeCount+"");
					Main.bv_atme.show();
				}else{
					Main.bv_atme.setText("");
					Main.bv_atme.hide();
				}
			}
			//����
			if(Main.bv_review != null){
				if(reviewCount > 0){
					Main.bv_review.setText(reviewCount+"");
					Main.bv_review.show();
				}else{
					Main.bv_review.setText("");
					Main.bv_review.hide();
				}
			}
			//����
			if(Main.bv_message != null){
				if(msgCount > 0){
					Main.bv_message.setText(msgCount+"");
					Main.bv_message.show();
				}else{
					Main.bv_message.setText("");
					Main.bv_message.hide();
				}
			}
			
			//֪ͨ����ʾ
			this.notification(context, activeCount);
		}
	}

	private void notification(Context context, int noticeCount){		
		//���� NotificationManager
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		String contentTitle = "��Դ�й�";
		String contentText = "���� " + noticeCount + " ��������Ϣ";
		int _lastNoticeCount;
		
		//�ж��Ƿ񷢳�֪ͨ��Ϣ
		if(noticeCount == 0)
		{
			notificationManager.cancelAll();
			lastNoticeCount = 0;
			return;
		}
		else if(noticeCount == lastNoticeCount)
		{
			return; 
		}
		else
		{
			_lastNoticeCount = lastNoticeCount;
			lastNoticeCount = noticeCount;
		}
		
		//����֪ͨ Notification
		Notification notification = null;
		
		if(noticeCount > _lastNoticeCount) 
		{
			String noticeTitle = "���� " + (noticeCount-_lastNoticeCount) + " ��������Ϣ";
			notification = new Notification(R.drawable.icon, noticeTitle, System.currentTimeMillis());
		}
		else
		{
			notification = new Notification();
		}
		
		//���õ��֪ͨ��ת
		Intent intent = new Intent(context, Main.class);
		intent.putExtra("NOTICE", true);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK); 
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		//����������Ϣ
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		//���õ�����֪ͨ
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		if(noticeCount > _lastNoticeCount) 
		{
			//����֪ͨ��ʽ
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			
			//����֪ͨ��
			notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notificationsound);
			
			//������ <��Ҫ�����û�Ȩ��android.permission.VIBRATE>
			//notification.vibrate = new long[]{100, 250, 100, 500};
		}
		
		//����֪ͨ
		notificationManager.notify(NOTIFICATION_ID, notification);		
	}
	
}
