package com.bmob.demo.relationdemo;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
/**
 * �û���Ϣʵ����
 * @date 2014��8��15�� ����2:48:45
 *
 */
public class MyUser extends BmobUser{
	/**
	 * �û�����
	 */
	private String nickname;
	/**
	 * �û������п�
	 * һ���˿����ж��Ų�ͬ�����п�������ѡ��ʹ����BmobRelation����
	 */
	private BmobRelation cards;
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public BmobRelation getCards() {
		return cards;
	}
	public void setCards(BmobRelation cards) {
		this.cards = cards;
	}
	
	
}
