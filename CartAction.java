package com.internousdev.radish.action;

import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.radish.dao.CartInfoDAO;
import com.internousdev.radish.dto.CartInfoDTO;
import com.opensymphony.xwork2.ActionSupport;

public class CartAction extends ActionSupport implements SessionAware{
	private long totalPrice;
	private List<CartInfoDTO> cartInfoDTOList;
	private Map<String, Object> session;

	public String execute(){
		if(!session.containsKey("tempUserId") && !session.containsKey("userId")){
			return "sessionTimeout";
		}

		String userId = null;
		String tempLogined = String.valueOf(session.get("logined"));//Map<String, Object>のObjectの部分をString型に設定する。
		CartInfoDAO cartInfoDAO = new CartInfoDAO();

		/*元の文 int logined = Integer.parseInt(String.valueOf(session.get("logined"))); を変えた理由は
		session.get("logined")で取り出した値がnullだった場合、Integer.parseIntで整数型に変換することが出来ないため、三項演算子、int logined = "null".equals(tempLogined) ? 0 : Integer.parseInt(tempLogined)を使ってnullだった
		場合に0をint loginedに入れるようにすることでNumberFormatExceptionを回避するため。*/
		int logined = "null".equals(tempLogined) ? 0 : Integer.parseInt(tempLogined);//三項演算子 (条件式)? (true):(false)
		if(logined == 1){//loginedのvalueを上の三項演算子で持ってきてif文で条件分岐に使う。
			userId = session.get("userId").toString();
		}else{
			userId = String.valueOf(session.get("tempUserId"));
		}
		cartInfoDTOList = cartInfoDAO.getCartInfoDTOList(userId);
		totalPrice = cartInfoDAO.getTotalPrice(userId);

		return SUCCESS;
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

	public Map<String, Object> getSession(){
		return session;
	}

	public void setSession(Map<String, Object> session){
		this.session=session;
	}
}
