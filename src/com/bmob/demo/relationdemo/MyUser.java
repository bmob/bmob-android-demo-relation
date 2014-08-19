package com.bmob.demo.relationdemo;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
/**
 * 用户信息实体类
 * @date 2014年8月15日 下午2:48:45
 *
 */
public class MyUser extends BmobUser{
	/**
	 * 用户名称
	 */
	private String nickname;
	/**
	 * 用户的银行卡
	 * 一个人可能有多张不同的银行卡，这里选择使用了BmobRelation类型
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
