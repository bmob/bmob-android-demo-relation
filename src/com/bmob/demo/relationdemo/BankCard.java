package com.bmob.demo.relationdemo;

import cn.bmob.v3.BmobObject;
/**
 * ���п�ʵ����
 * @date 2014��8��15�� ����2:46:18
 *
 */
public class BankCard extends BmobObject {
	/**
	 * ���п���
	 */
	private String cardNumber;
	/**
	 * ��������
	 */
	private String bankName;
	/**
	 * ����
	 */
	private MyUser user;
	
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public MyUser getUser() {
		return user;
	}
	public void setUser(MyUser user) {
		this.user = user;
	}
}
