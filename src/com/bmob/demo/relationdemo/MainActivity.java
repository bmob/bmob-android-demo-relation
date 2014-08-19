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
		 * ��ʼ��BmobSDK ��һ������Ϊ��ǰӦ�ó��������� �ڶ�������Ϊ������Bmob web�˴���Ӧ�õõ���Application ID
		 */
		Bmob.initialize(this, "");
		
		toast("��ǵý����AppId ��д��MainActivity��BmobSDK��ʼ��������");

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
			// ����һ���û�
			createUser();
			break;
		case R.id.btn_addCard:
			// ������п�(Add Relation)
			showCreateBankCardDialog();
			break;
		case R.id.btn_removeUserCards:
			// ���û���Ϣ���Ƴ����п���Ϣ(RemoveRelation)
			removeCardToUser();
			break;
		case R.id.btn_findMyCards:
			// ��ѯ�ҵ��������п���Ϣ(Query Relation)
			findMyCards();
			break;
		case R.id.btn_findCardUser:
			// ��ѯ���п���Ϣ(����������Ϣ)
			findCardUser();
			break;

		default:
			break;
		}
	}
	
	/**
	 * ����һ���û���Ϣ��User����
	 */
	private void createUser() {

		LayoutInflater inflater = getLayoutInflater();
		final View layout = inflater.inflate(R.layout.dialog_user,
				(ViewGroup) findViewById(R.id.dialog));
		new AlertDialog.Builder(this)
				.setTitle("�����û���Ϣ")
				.setView(layout)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

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
							toast("�û��������벻��Ϊ��");
						} else {
							user = new MyUser();
							user.setUsername(etname.getText().toString());
							user.setPassword(etpwd.getText().toString());
							user.signUp(MainActivity.this, new SaveListener() {

								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									toast("�û������ɹ�");
									// �������ɹ����û���Ϣ��ʾ�ڽ�����
									showUserinfo();
								}

								@Override
								public void onFailure(int arg0, String arg1) {
									// TODO Auto-generated method stub
									toast("���ź����û�����ʧ��:" + arg1);
								}
							});
						}
					}
				})
				.setNegativeButton("ȡ��", null).show();

	}

	/**
	 * �򿪴������п������
	 */
	private void showCreateBankCardDialog() {
		
		LayoutInflater inflater = getLayoutInflater();
		final View layout = inflater.inflate(R.layout.dialog_card,
				(ViewGroup) findViewById(R.id.dialog));
		new AlertDialog.Builder(this)
				.setTitle("�������п���Ϣ")
				.setView(layout)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

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
							toast("�������Ϳ��Ų���Ϊ��");
						} else {
							saveBankCardInfo(etname.getText().toString(), etcard.getText().toString());
						}
					}
				})
				.setNegativeButton("ȡ��", null).show();
	}
	
	/**
	 * ����һ�����п���Ϣ��BankCard����,���������û������п���Ϣ��
	 * @param bankName ��������
	 * @param cardNumber ���п���
	 */
	private void saveBankCardInfo(String bankName, String cardNumber){
		
		if(TextUtils.isEmpty(user.getObjectId())){
			toast("��ǰ�û���objectΪ��");
			return;
		}
		
		card = new BankCard();
		card.setBankName(bankName);		// ������������
		card.setCardNumber(cardNumber);	// �������п���
		card.setUser(user);				// �������п�����
		card.save(this, new SaveListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				toast("�ɹ�����һ�����п���Ϣ��BankCard����");
				addCardToUser();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				toast("���ź�������һ�����п���Ϣ��BankCard����ʧ����");
			}
		});
	}
	
	/**
	 * ������п����û������п���Ϣ��
	 */
	private void addCardToUser(){
		if(TextUtils.isEmpty(user.getObjectId()) || 
				TextUtils.isEmpty(card.getObjectId())){
			toast("��ǰ�û����ߵ�ǰCard�����objectΪ��");
			return;
		}
		
		BmobRelation cards = new BmobRelation();
		cards.add(card);
		user.setCards(cards);
		user.update(this, new UpdateListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				toast("�ѳɹ���ӵ��û������п���Ϣ��");
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				toast("���ź����û������п���Ϣ���ʧ��");
			}
		});
	}
	/**
	 * ɾ���û���Ϣ�е�ĳ���п���Ϣ
	 */
	private void removeCardToUser(){
		if(TextUtils.isEmpty(user.getObjectId()) || 
				TextUtils.isEmpty(card.getObjectId())){
			toast("��ǰ�û����ߵ�ǰCard�����objectΪ��");
			return;
		}
		
		BmobRelation cards = new BmobRelation();
		cards.remove(card);
		user.setCards(cards);
		user.update(this, new UpdateListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				toast("���û���Ϣ���ѳɹ��Ƴ������п���Ϣ");
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				toast("���ź����Ƴ�ʧ��");
			}
		});
	}
	
	/**
	 * ��ѯ�ҵ��������п���Ϣ
	 */
	private void findMyCards(){
		BmobQuery<BankCard> cards = new BmobQuery<BankCard>();
		/**
		 * ע������Ĳ�ѯ����
		 * ��һ����������User���е�cards�ֶ���
		 * �ڶ�����������ָ��User���е�ĳ���û���BmobPointer����
		 */
		cards.addWhereRelatedTo("cards", new BmobPointer(user));
		cards.findObjects(this, new FindListener<BankCard>() {
			
			@Override
			public void onSuccess(List<BankCard> arg0) {
				// TODO Auto-generated method stub
				toast("��������"+arg0.size()+"�����п�");
				for (BankCard bankCard : arg0) {
					Log.d("bmob", "objectId:"+bankCard.getObjectId()+",�������ƣ�"+bankCard.getBankName()+",���ţ�"+bankCard.getCardNumber());
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				toast("��ѯ�����п���Ϣʧ��");
			}
		});
	}
	
	/**
	 * ��ѯ���п���Ϣʱͬʱ��ȡ�ÿ��Ļ�����Ϣ
	 */
	private void findCardUser(){
		BmobQuery<BankCard> query = new BmobQuery<BankCard>();
		query.include("user");	// ע�������include���������ڲ�ѯ�������뽫ָ�����͵Ķ�����ϢҲ��ѯ��������ʹ�ô˷�����
		query.getObject(this, card.getObjectId(), new GetListener<BankCard>() {
			
			@Override
			public void onSuccess(BankCard arg0) {
				// TODO Auto-generated method stub
				toast("���ţ�"+arg0.getCardNumber()+"\n"
						+"������"+arg0.getUser().getUsername());
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				toast("��ѯ���п���Ϣʧ��"+arg1);
			}
		});
	}
	
	/**
	 * ��ʾ�û���Ϣ
	 */
	private void showUserinfo(){
		if(user != null){
			tv_userObjectId.setText("objectId:" + user.getObjectId());
			tv_userName.setText("username:" + user.getUsername());
		}
	}

	/**
	 * �Զ���һ��Toast����
	 * 
	 * @param msg
	 *            Ҫ�������ʾ��Ϣ
	 */
	private void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
