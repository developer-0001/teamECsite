package com.internousdev.radish.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.internousdev.radish.dto.CartInfoDTO;
import com.internousdev.radish.util.DBConnector;

public class CartInfoDAO {

	public List<CartInfoDTO>getCartInfoDTOList(String userId){
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		List<CartInfoDTO> cartInfoDTOList = new ArrayList<CartInfoDTO>();

		String sql="select "
				+ "ci.id as id,"
				+ "ci.user_id as user_id,"
				+ "ci.product_id as product_id,"
				+ "ci.product_count as product_count,"
				+ "pi.price as price,"
				+ "pi.product_name as product_name,"
				+ "pi.product_name_kana as product_name_kana,"
				+ "pi.image_file_path as image_file_path,"
				+ "pi.image_file_name as image_file_name,"
				+ "pi.release_date as release_date,"
				+ "pi.release_company as release_company,"
				+ "pi.status as status,"
				+ "(ci.product_count * pi.price) as subtotal,"
				+ "ci.regist_date as regist_date,"
				+ "ci.update_date as update_date "
				+ "FROM cart_info as ci "
				+ "LEFT JOIN product_info as pi "
				+ "ON ci.product_id = pi.product_id "
				+ "WHERE ci.user_id=? "
				+ "order by update_date desc, regist_date desc";

		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1,userId);
			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				CartInfoDTO cartInfoDTO = new CartInfoDTO();
				cartInfoDTO.setId(rs.getInt("id"));
				cartInfoDTO.setUserId(rs.getString("user_id"));
				cartInfoDTO.setProductId(rs.getInt("product_id"));
				cartInfoDTO.setProductCount(rs.getInt("product_count"));
				cartInfoDTO.setPrice(rs.getInt("price"));
				cartInfoDTO.setProductName(rs.getString("product_name"));
				cartInfoDTO.setProductNameKana(rs.getString("product_name_kana"));
				cartInfoDTO.setImageFilePath(rs.getString("image_file_path"));
				cartInfoDTO.setImageFileName(rs.getString("image_file_name"));
				cartInfoDTO.setReleaseDate(rs.getDate("release_date"));
				cartInfoDTO.setReleaseCompany(rs.getString("release_company"));
				cartInfoDTO.setStatus(rs.getString("status"));
				cartInfoDTO.setSubTotal(rs.getLong("subtotal"));
				cartInfoDTOList.add(cartInfoDTO);

			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
			con.close();
			}catch(SQLException e){
			e.printStackTrace();
		}
	}
	return cartInfoDTOList;
}

	//商品の購入個数に応じての合計金額を出す。
	public long getTotalPrice(String userId){
		long totalPrice = 0;
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		String sql = "select "
				+ "sum(product_count * price) as total_price "
				+ "from cart_info ci "
				+ "join product_info pi "
				+ "on ci.product_id = pi.product_id "
				+ "where user_id=? group by user_id";

		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ResultSet rs  = ps.executeQuery();
			if(rs.next()){
				totalPrice = rs.getLong("total_price");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				con.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return totalPrice;
	}

	//カート情報に商品の購入情報を入れる。
	public int regist(String userId, int productId, int productCount){
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int count = 0;
		String sql = "insert into cart_info(user_id, product_id, product_count, regist_date, update_date)"
				+ "values(?, ?, ?, now(), now()) ";
		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1,userId);
			ps.setInt(2, productId);
			ps.setInt(3, productCount);
			count = ps.executeUpdate();

		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				con.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return count;

	}

	//カートにすでに存在する商品の購入個数情報を更新する(数を加える)。
	public int updateProductCount(String userId, int productId, int productCount){
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		String sql ="UPDATE cart_info SET product_count=(product_count + ?), update_date=now() WHERE user_id=? AND product_id=?";

		int result = 0;

		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, productCount);
			ps.setString(2, userId);
			ps.setInt(3, productId);
			result = ps.executeUpdate();

		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				con.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return result;
	}

	public int delete(String productId, String userId){
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int count = 0;
		String sql ="delete from cart_info WHERE product_id =? and user_id=?";

		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, productId);
			ps.setString(2, userId);
			count = ps.executeUpdate();

		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				con.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return count;
	}

	public int deleteAll(String userId){
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int count = 0;
		String sql = "delete from cart_info WHERE user_id=?";

		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			count = ps.executeUpdate();

		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				con.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return count;
	}

	//引数として受け取ったuserIdのユーザーが、productIdの商品のカートに入れた情報が存在するかどうかを判別する。
	public boolean isExistsCartInfo (String userId, int productId){
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		String sql = "select count(id) as count from cart_info WHERE user_id =? AND product_id=?";
		boolean result = false;

		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setInt(2, productId);
			ResultSet rs = ps.executeQuery();

			if(rs.next()){
				if(rs.getInt("count")>0){
					result = true;
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				con.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return result;
	}

	//仮ユーザーIDに紐づいているカート情報をユーザーIDに紐付け直す。
	public int linkToUserId(String tempUserId, String userId, int productId){
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int count = 0;
		String sql = "update cart_info set user_id=?, update_date = now() WHERE user_id=? and product_id=?";

		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, tempUserId);
			ps.setInt(3, productId);
			count = ps.executeUpdate();

		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
					con.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
			return count;
		}
	}