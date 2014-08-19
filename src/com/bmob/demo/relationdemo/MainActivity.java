package com.bmob.demo.relationdemo;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private TextView tv_userObjectId, tv_userName;
	
	private MyUser user = new MyUser();
	
	private BankCard card = new BankCard();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		/**
		 * 初始化BmobSDK 第一个参数为当前应用程序上下文 第二个参数为，你在Bmob web端创建应用得到的Application ID
		 */
		Bmob.initialize(this, "");
		
		toast("请记得将你的AppId 填写在MainActivity的BmobSDK初始化方法中");

		tv_userObjectId = (TextView) findViewById(R.id.tv_userObjectId);
		tv_userName = (TextView) findViewById(R.id.tv_username);
		
		user = BmobUser.getCurrentUser(this, MyUser.class);
		showUserinfo();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_addUser:
			// 创建一个用户
			createUser();
			break;
		case R.id.btn_addCard:
			// 添加银行卡(Add Relation)
			showCreateBankCardDialog();
			break;
		case R.id.btn_removeUserCards:
			// 在用户信息中移除银行卡信息(RemoveRelation)
			removeCardToUser();
			break;
		case R.id.btn_findMyCards:
			// 查询我的所有银行卡信息(Query Relation)
			findMyCards();
			break;
		case R.id.btn_findCardUser:
			// 查询银行卡信息(包含户主信息)
			findCardUser();
			break;

		default:
			break;
		}
	}
	
	/**
	 * 创建一条用户信息到User表中
	 */
	private void createUser() {

		LayoutInflater inflater = getLayoutInflater();
		final View layout = inflater.inflate(R.layout.dialog_user,
				(ViewGroup) findViewById(R.id.dialog));
		new AlertDialog.Builder(this)
				.setTitle("输入用户信息")
				.setView(layout)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EditText etname = (EditText) layout
								.findViewById(R.id.etname);
						EditText etpwd = (EditText) layout
								.findViewById(R.id.etpwd);
						if (TextUtils.isEmpty(etname.getText().toString())
								|| TextUtils
										.isEmpty(etpwd.getText().toString())) {
							toast("用户名和密码不能为空");
						} else {
							user = new MyUser();
							user.setUsername(etname.getText().toString());
							user.setPassword(etpwd.getText().toString());
							user.signUp(MainActivity.this, new SaveListener() {

								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									toast("用户创建成功");
									// 将创建成功的用户信息显示在界面中
									showUserinfo();
								}

								@Override
								public void onFailure(int arg0, String arg1) {
									// TODO Auto-generated method stub
									toast("很遗憾，用户创建失败:" + arg1);
								}
							});
						}
					}
				})
				.setNegativeButton("取消", null).show();

	}

	/**
	 * 打开创建银行卡输入框
	 */
	private void showCreateBankCardDialog() {
		
		LayoutInflater inflater = getLayoutInflater();
		final View layout = inflater.inflate(R.layout.dialog_card,
				(ViewGroup) findViewById(R.id.dialog));
		new AlertDialog.Builder(this)
				.setTitle("输入银行卡信息")
				.setView(layout)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EditText etname = (EditText) layout
								.findViewById(R.id.etbackname);
						EditText etcard = (EditText) layout
								.findViewById(R.id.etcard);
						if (TextUtils.isEmpty(etname.getText().toString())
								|| TextUtils
										.isEmpty(etcard.getText().toString())) {
							toast("银行名和卡号不能为空");
						} else {
							saveBankCardInfo(etname.getText().toString(), etcard.getText().toString());
						}
					}
				})
				.setNegativeButton("取消", null).show();
	}
	
	/**
	 * 创建一条银行卡信息到BankCard表中,并关联到用户的银行卡信息中
	 * @param bankName 银行名称
	 * @param cardNumber 银行卡号
	 */
	private void saveBankCardInfo(String bankName, String cardNumber){
		
		if(TextUtils.isEmpty(user.getObjectId())){
			toast("当前用户的object为空");
			return;
		}
		
		card = new BankCard();
		card.setBankName(bankName);		// 设置银行名称
		card.setCardNumber(cardNumber);	// 设置银行卡号
		card.setUser(user);				// 设置银行卡户主
		card.save(this, new SaveListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				toast("成功保存一条银行卡信息到BankCard表中");
				addCardToUser();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				toast("很遗憾，保存一条银行卡信息到BankCard表中失败了");
			}
		});
	}
	
	/**
	 * 添加银行卡到用户的银行卡信息中
	 */
	private void addCardToUser(){
		if(TextUtils.isEmpty(user.getObjectId()) || 
				TextUtils.isEmpty(card.getObjectId())){
			toast("当前用户或者当前Card对象的object为空");
			return;
		}
		
		BmobRelation cards = new BmobRelation();
		cards.add(card);
		user.setCards(cards);
		user.update(this, new UpdateListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				toast("已成功添加到用户的银行卡信息中");
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				toast("很遗憾，用户的银行卡信息添加失败");
			}
		});
	}
	/**
	 * 删除用户信息中的某银行卡信息
	 */
	private void removeCardToUser(){
		if(TextUtils.isEmpty(user.getObjectId()) || 
				TextUtils.isEmpty(card.getObjectId())){
			toast("当前用户或者当前Card对象的object为空");
			return;
		}
		
		BmobRelation cards = new BmobRelation();
		cards.remove(card);
		user.setCards(cards);
		user.update(this, new UpdateListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				toast("在用户信息中已成功移除该银行卡信息");
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				toast("很遗憾，移除失败");
			}
		});
	}
	
	/**
	 * 查询我的所有银行卡信息
	 */
	private void findMyCards(){
		BmobQuery<BankCard> cards = new BmobQuery<BankCard>();
		/**
		 * 注意这里的查询条件
		 * 第一个参数：是User表中的cards字段名
		 * 第二个参数：是指向User表中的某个用户的BmobPointer对象
		 */
		cards.addWhereRelatedTo("cards", new BmobPointer(user));
		cards.findObjects(this, new FindListener<BankCard>() {
			
			@Override
			public void onSuccess(List<BankCard> arg0) {
				// TODO Auto-generated method stub
				toast("我现在有"+arg0.size()+"张银行卡");
				for (BankCard bankCard : arg0) {
					Log.d("bmob", "objectId:"+bankCard.getObjectId()+",银行名称："+bankCard.getBankName()+",卡号："+bankCard.getCardNumber());
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				toast("查询我银行卡信息失败");
			}
		});
	}
	
	/**
	 * 查询银行卡信息时同时获取该卡的户主信息
	 */
	private void findCardUser(){
		BmobQuery<BankCard> query = new BmobQuery<BankCard>();
		query.include("user");	// 注意这里的include方法，当在查询过程中想将指针类型的对象信息也查询出来是请使用此方法。
		query.getObject(this, card.getObjectId(), new GetListener<BankCard>() {
			
			@Override
			public void onSuccess(BankCard arg0) {
				// TODO Auto-generated method stub
				toast("卡号："+arg0.getCardNumber()+"\n"
						+"户主："+arg0.getUser().getUsername());
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				toast("查询银行卡信息失败"+arg1);
			}
		});
	}
	
	/**
	 * 显示用户信息
	 */
	private void showUserinfo(){
		if(user != null){
			tv_userObjectId.setText("objectId:" + user.getObjectId());
			tv_userName.setText("username:" + user.getUsername());
		}
	}

	/**
	 * 自定义一个Toast方法
	 * 
	 * @param msg
	 *            要输出的提示信息
	 */
	private void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
