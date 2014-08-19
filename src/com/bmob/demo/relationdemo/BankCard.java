package com.bmob.demo.relationdemo;

import cn.bmob.v3.BmobObject;
/**
 * 银行卡实体类
 * @date 2014年8月15日 下午2:46:18
 *
 */
public class BankCard extends BmobObject {
	/**
	 * 银行卡号
	 */
	private String cardNumber;
	/**
	 * 银行名称
	 */
	private String bankName;
	/**
	 * 户主
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
