package com.pintu.ads.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.pintu.ads.beans.Ads;
import com.pintu.ads.dao.AdsDBInterface;


public class AdsDBImplement  implements AdsDBInterface{
	
	private JdbcTemplate jdbcTemplate;

	// Inject by Spring
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Ads> getTodayAds(String today) {
		List<Ads> resList = new ArrayList<Ads>();
		String sql = "select * from t_ads where ad_startTime <='" + today
				+ "' and ad_endTime >='"+today+"' and ad_disabled=1 order by ad_priority, ad_createTime desc" ;
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Ads ad = new Ads();
				ad.setId(map.get("ad_id").toString());
				ad.setVender(map.get("ad_vender").toString());
				ad.setType(map.get("ad_type").toString());
				ad.setContent(map.get("ad_content").toString());
				ad.setCreateTime(map.get("ad_createTime").toString());
				ad.setStartTime(map.get("ad_startTime").toString());
				ad.setEndTime(map.get("ad_endTime").toString());
				ad.setLink(map.get("ad_link").toString());
				if(map.get("ad_imgPath") != null){
					ad.setImgPath(map.get("ad_imgPath").toString());
				}
				ad.setPriority(Integer.parseInt(map.get("ad_priority").toString()));
				ad.setDisabled(Integer.parseInt(map.get("ad_disabled").toString()));
				resList.add(ad);
			}
		}
		return resList;
	}

	@Override
	public List<Ads> serarchAds(String keys, String time) {
		List<Ads> resList = new ArrayList<Ads>();
		String str = "%"+keys+"%";
		String sql = "";
		if("".equals(keys) && "".equals(time)){
			sql = "select * from t_ads where ad_disabled=1 order by ad_priority, ad_createTime desc" ;
		}else if(!"".equals(keys) && "".equals(time)){
			sql = "select * from t_ads where (ad_vender like '" +str+"' or ad_content like '"+str+"') and ad_disabled=1 order by ad_priority, ad_createTime desc" ;
		}else if(!"".equals(time) && "".equals(keys)){
			sql = "select * from t_ads where (ad_startTime <='" + time+ "' and ad_endTime >='"+time+"') and ad_disabled=1 order by ad_priority, ad_createTime desc" ;
		}else if(!"".equals(keys) && !"".equals(time)){ 
			sql = "select * from t_ads where ((ad_vender like '" +str+"' or ad_content like '"+str+"')"+
					" and (ad_startTime <='" + time+ "' and ad_endTime >='"+time+"')) and ad_disabled=1 order by ad_priority, ad_createTime desc" ;
		}
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Ads ad = new Ads();
				ad.setId(map.get("ad_id").toString());
				ad.setVender(map.get("ad_vender").toString());
				ad.setType(map.get("ad_type").toString());
				ad.setContent(map.get("ad_content").toString());
				ad.setCreateTime(map.get("ad_createTime").toString());
				ad.setStartTime(map.get("ad_startTime").toString());
				ad.setEndTime(map.get("ad_endTime").toString());
				ad.setLink(map.get("ad_link").toString());
				if(map.get("ad_imgPath") != null){
					ad.setImgPath(map.get("ad_imgPath").toString());
				}
				ad.setPriority(Integer.parseInt(map.get("ad_priority").toString()));
				ad.setDisabled(Integer.parseInt(map.get("ad_disabled").toString()));
				resList.add(ad);
			}
		}
		return resList;
	}

	@Override
	public int deleteAdsById(String adId) {
		String sql = "update t_ads set ad_disabled = 0 where ad_id='"+adId+"'"; 
		int rows = jdbcTemplate.update(sql);
		return rows;
	}

	@Override
	public int createAds(final Ads ad) {
		String sql = "insert into t_ads"
			+ "(ad_id, ad_vender, ad_content, ad_type, ad_imgPath, ad_createTime, ad_startTime, ad_endTime, ad_link, ad_priority, ad_disabled, ad_memo) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ? ,? , ?, ?, ?)";

			int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) {
					try {
						ps.setString(1, ad.getId());
						ps.setString(2, ad.getVender());
						ps.setString(3, ad.getContent());
						ps.setString(4, ad.getType());
						ps.setString(5, ad.getImgPath());
						ps.setString(6, ad.getCreateTime());
						ps.setString(7, ad.getStartTime());
						ps.setString(8, ad.getEndTime());
						ps.setString(9, ad.getLink());
						ps.setInt(10, ad.getPriority());
						ps.setInt(11, ad.getDisabled());
						ps.setString(12, "");

					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			});

		return res;
	}

	@Override
	public Ads getAdsById(String adId) {
		Ads ad = new Ads();
		String sql = "select * from t_ads where ad_id = '"+adId+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
				Map<String, Object> map = (Map<String, Object>) rows.get(0);
				ad.setId(map.get("ad_id").toString());
				ad.setVender(map.get("ad_vender").toString());
				ad.setType(map.get("ad_type").toString());
				ad.setContent(map.get("ad_content").toString());
				ad.setCreateTime(map.get("ad_createTime").toString());
				ad.setStartTime(map.get("ad_startTime").toString());
				ad.setEndTime(map.get("ad_endTime").toString());
				ad.setLink(map.get("ad_link").toString());
				if(map.get("ad_imgPath") != null){
					ad.setImgPath(map.get("ad_imgPath").toString());
				}
				ad.setPriority(Integer.parseInt(map.get("ad_priority").toString()));
				ad.setDisabled(Integer.parseInt(map.get("ad_disabled").toString()));
		}
		return ad;
	}

	@Override
	public int updateAdsById(String adId, final Ads ad) {
		String sql = "update t_ads  set ad_vender=?, ad_content=?, ad_type=?, ad_imgPath=?, ad_startTime=?, ad_endTime=?, ad_link=?, ad_priority=?  where ad_id ='"+adId+"'";
		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ps.setString(1, ad.getVender());
						ps.setString(2, ad.getContent());
						ps.setString(3, ad.getType());
						ps.setString(4, ad.getImgPath());
						ps.setString(5, ad.getStartTime());
						ps.setString(6, ad.getEndTime());
						ps.setString(7, ad.getLink());
						ps.setInt(8, ad.getPriority());
					}
					public int getBatchSize() {
						return 1;
					}
				});
		return res.length;
	}

	
}
