package com.pintu.facade;

import java.util.List;

import com.pintu.beans.Comment;
import com.pintu.beans.GTStatics;
import com.pintu.beans.Gift;
import com.pintu.beans.Message;
import com.pintu.beans.News;
import com.pintu.beans.Note;
import com.pintu.beans.ShellDetails;
import com.pintu.beans.Story;
import com.pintu.beans.TPEvent;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicDetails;
import com.pintu.beans.TastePic;
import com.pintu.beans.UsrEstate;
import com.pintu.beans.Vote;
import com.pintu.beans.WeiboUsr;

public interface PintuServiceInterface {
	
	
	/**
	 * �÷��������û�����ע�ᣬϵͳ���������뷢�͵����������ע�᣻
	 * ��������Ҫ���⽨��һ�������û���t_applyuser
	 * 
	 * @param realname ��ʵ����
	 * @param email ����
	 * @param intro ���ҽ��ܼ�������
	 * @return
	 */
	public Boolean applyForUser(String realname,String email, String intro);
	
	/**
	 * �÷�����webע��ҳ���е���
	 * @param user �û�����Ϊ�����ַ
	 * @param pswd ����
	 * @param inviteCode ������
	 * @return
	 */
	//�û�ע��
	public Boolean registerUser(String user,String pswd,String inviteCode);
	
	//ʹ��Ʒͼ�˺ŵ�¼ϵͳ
	public Boolean loginSys(String user,String pswd);
	
	//ʹ��΢���˺ŵ�¼ϵͳ
	public Boolean loginByWeibo(String user,String pswd);
	
	//����һ����ͼ��ϵͳ��
	public Boolean createTastePic(TastePic pic,String user);
	
	//�鿴�Լ�����ͼ�б�
	public List<TPicDesc> getTpicsByUser(String user,String pageNum);
	
	//�鿴�Լ��ı������������
	public ShellDetails getShellDetails(String user);
	
	//�����������
	public List<TPicDesc> getCommunityTpics();
	
	//���һ��Ʒͼ����
	public TPicDetails getTPicDetailsByID(String tpID);
	
	//�鿴һ��Ʒͼ�Ĺ���
	public List<Story> getStoriesOfPic(String tpID);
	
	//�鿴һ��Ʒͼ������
	public List<Comment> getCommentsOfPic(String tpID);
	
	//�鿴������ͼ�������������������������
	//ϵͳ��ʱͳ�Ƴ��������֣�Ȼ�󷵻�����ͼ����10���ڵ�30��ͼ��
	public List<TPicDesc> getHotTpics();
	
	//�鿴�����������
	//FIXME, ������ܽ�����û����ƣ���ʱ�Ȳ�ʵ�֣�
	public List<TPicDesc> getInviteTpicsToday();
	
	//�鿴����ͼ�Ĺ���
	public List<TPicDesc> getClassicTpics();
	
	//�鿴�û�������Ϣ��΢���˺ţ�
	public WeiboUsr getUsrBasInfo(String user);
	
	//�鿴�û��ȼ���ӵ�б�����
	public UsrEstate getUsrEstate(String user);
	
	//չʾ���յĿɶһ�������Ϣ
	public List<Gift> getGiftsToday();
	
	//�������û���ͼ��������
	public Boolean commentPintu(Comment cmt);
	
	//Ϊ�����е�Ʒͼ�����������Ʒ��
	public Boolean addStoryToTpic(Story story);
	
	//Ϊ�����е�Ʒ��ͶƱ�����������ʶ��
	public Boolean addPollToTpic(Vote vote);
	
	//�鿴���������¼�
	public List<TPEvent> getCommunityEvents();
	
	//�鿴�Լ����ղ�ͼƬ
	public List<TPicDesc> getFavoriteTpics(String user,String pageNum);
	
	//��ȡһ������ͼ
	public Byte[] getTPicThumbnail(String thumbnailId);
	
	//��ȡԭʼ��ͼ�����ͼ����web�����
	public Byte[] getTPicBig(String tpID);
	
	//��ȡһ��С�ߴ�ͼ�������ֻ����
	public Byte[] getTPicMoile(String tpID);
	
	//����һ����Ϣ
	public Boolean sendMessage(Message msg);
	
	//�鿴�Լ�����Ϣ
	public List<Message> getUserMessages(String user);
	
	
	
	//ANYMORE NECESSARY???
	
	
	//************************  ����Ϊ�ͻ���2.0�汾����  *********************************
	
	//�����������(���ˣ�Get Talent)
	public GTStatics getCommunityGTs();
	
	//��ҹ�жһ�����
	public Boolean exchangeGifts(String user,String giftIds);
	
	//�����ھ�����
	public Boolean giveGifts(String user,String giftIds);
	
	//��ҹ��������
	public Boolean pasteNote(String user,String content);
	
	//��ҹ������
	public List<Note> getMarketNotes();
	
	//��ͼƬ�ؼ�������
	public List<TPicDesc> searchTpicByTags(String tags);
	
	//�鿴��ҵ��̬
	public List<News> getIndustryNews();
	

	//********************  ��̨�����÷���  **************************************
	
	//�鿴���һ��ʱ�����ͼ���Զ�ˢ�£�
	public List<TPicDesc> getLatestTpics(String timeLength);
	
	//ɾ��������ͼ
	public Boolean deleteTasetPic(String tpID);
	
	//ɾ����ˮ����
	public Boolean deleteStoryOfPic(String storyId,String tpicID);
	
	//������ѡ������Ϣ
	public Boolean publishAvailableGift(Gift gift);
	
	
	//********************* ��̨����2.0���� *************************************
	
	//���������¼�
	public Boolean publishTpEvent(TPEvent tpEvent);
	
	//������ҵ��̬
	public Boolean publishIndustryEvent(TPEvent tpEvent);
	
	
	
	//TO BE CONTINUED...
	
}
