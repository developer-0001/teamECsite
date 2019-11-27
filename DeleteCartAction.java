package com.internousdev.radish.action;

import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.radish.dao.CartInfoDAO;
import com.internousdev.radish.dto.CartInfoDTO;
import com.opensymphony.xwork2.ActionSupport;

public class DeleteCartAction extends ActionSupport implements SessionAware {

	private long totalPrice;
	private List<CartInfoDTO> cartInfoDTOList;
	private String[] checkList;
	private Map<String,Object> session;

	public String execute(){

		if(!session.containsKey("tempUserId") && !session.containsKey("userId")){
			return "sessionTimeout";
		}

		String result=ERROR;
		CartInfoDAO cartInfoDAO = new CartInfoDAO();
		int count = 0;
		String userId = null;
		String tempLogined = String.valueOf(session.get("logined"));

		int logined = "null".equals(tempLogined) ? 0 : Integer.parseInt(tempLogined);
		if(logined == 1){
			userId = session.get("userId").toString();
		}else{
			userId = String.valueOf(session.get("tempUserId"));
		}
		for(String productId:checkList){
			count += cartInfoDAO.delete(productId, userId);
		}
		if(count == checkList.length){
			cartInfoDTOList = cartInfoDAO.getCartInfoDTOList(userId);
			totalPrice = cartInfoDAO.getTotalPrice(userId);

			result=SUCCESS;
		}
		return result;
	}

	public long getTotalPrice(){
		return totalPrice;
	}

	public void setTotalPrice(long totalPrice){
		this.totalPrice=totalPrice;
	}

	public List<CartInfoDTO> getCartInfoDTOList(){
		return cartInfoDTOList;
	}

	public void setCartInfoDTOList(List<CartInfoDTO> cartInfoDTOList){
		this.cartInfoDTOList=cartInfoDTOList;
	}

	public String[] getCheckList(){
		return checkList;
	}

	public void setCheckList(String[] checkList){
		this.checkList=checkList;
	}

	public Map<String, Object> getSession(){
		return session;
	}

	public void setSession(Map<String, Object> session){
		this.session=session;
	}

}
