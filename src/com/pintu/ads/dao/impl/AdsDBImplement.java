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
import com.pintu.ads.beans.Vender;
import com.pintu.ads.dao.AdsDBInterface;


public class AdsDBImplement  implements AdsDBInterface{
	
	private JdbcTemplate jdbcTemplate;

	// Inject by Spring
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Ads> getTodayAds(String today,String userId) {
		List<Ads> resList = new ArrayList<Ads>();
		String sql="";
		if(userId != null){
			sql = "select * from t_ads where ad_publisher='"+userId+"' and ad_startTime <='" + today
					+ "' and ad_endTime >='"+today+"' and ad_enable=1 order by ad_priority, ad_createTime desc" ;
		}else{
			sql = "select * from t_ads where ad_startTime <='" + today
					+ "' and ad_endTime >='"+today+"' and ad_enable=1 order by ad_priority, ad_createTime desc" ;
		}
	
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Ads ad = new Ads();
				ad.setId(map.get("ad_id").toString());
				ad.setPublisher(map.get("ad_publisher").toString());
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
				ad.setEnable(Integer.parseInt(map.get("ad_enable").toString()));
				resList.add(ad);
			}
		}
		return resList;
	}

	@Override
	public List<Ads> serarchAds(String keys, String time ,String userId) {
		List<Ads> resList = new ArrayList<Ads>();
		String sql = generateSql(keys,time,userId);
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Ads ad = new Ads();
				ad.setId(map.get("ad_id").toString());
				ad.setPublisher(map.get("ad_publisher").toString());
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
				ad.setEnable(Integer.parseInt(map.get("ad_enable").toString()));
				resList.add(ad);
			}
		}
		return resList;
	}
	
	private String generateSql(String keys, String time ,String userId){
		String str = "%"+keys+"%";
		String sql = "";
		if(userId !=null && !"".equals(userId)){
			if("".equals(keys) && "".equals(time)){
				sql = "select a.ad_id, a.ad_publisher, a.ad_type, a.ad_content, a.ad_createTime, a.ad_startTime, a.ad_endTime, a.ad_link, a.ad_imgPath, a.ad_priority, a.ad_enable , v.v_name as ad_vender" +
						" from t_ads a,t_vender v where a.ad_publisher = v.v_id and ad_publisher ='"+userId+"' and ad_enable=1 order by ad_priority, ad_createTime desc" ;
			}else if(!"".equals(keys) && "".equals(time)){
				sql = "select a.ad_id, a.ad_publisher, a.ad_type, a.ad_content, a.ad_createTime, a.ad_startTime, a.ad_endTime, a.ad_link, a.ad_imgPath, a.ad_priority, a.ad_enable , v.v_name as ad_vender" +
						" from t_ads a,t_vender v where a.ad_publisher = v.v_id and ad_publisher ='"+userId+"' and ad_content like '"+str+"' and ad_enable=1 order by ad_priority, ad_createTime desc" ;
			}else if(!"".equals(time) && "".equals(keys)){
				sql = "select a.ad_id, a.ad_publisher, a.ad_type, a.ad_content, a.ad_createTime, a.ad_startTime, a.ad_endTime, a.ad_link, a.ad_imgPath, a.ad_priority, a.ad_enable , v.v_name as ad_vender" +
						" from t_ads a,t_vender v where a.ad_publisher = v.v_id and ad_publisher ='"+userId+"' and (ad_startTime <='" + time+ "' and ad_endTime >='"+time+"') and ad_enable=1 order by ad_priority, ad_createTime desc" ;
			}else if(!"".equals(keys) && !"".equals(time)){ 
				sql = "select a.ad_id, a.ad_publisher, a.ad_type, a.ad_content, a.ad_createTime, a.ad_startTime, a.ad_endTime, a.ad_link, a.ad_imgPath, a.ad_priority, a.ad_enable , v.v_name as ad_vender" +
						"from t_ads a,t_vender v where a.ad_publisher = v.v_id and t_ads where ad_publisher ='"+userId+"' and ad_content like '"+str+"'"+
						" and (ad_startTime <='" + time+ "' and ad_endTime >='"+time+"')) and ad_enable=1 order by ad_priority, ad_createTime desc" ;
			}
		}else{
			if("".equals(keys) && "".equals(time)){
				sql = "select a.ad_id, a.ad_publisher, a.ad_type, a.ad_content, a.ad_createTime, a.ad_startTime, a.ad_endTime, a.ad_link, a.ad_imgPath, a.ad_priority, a.ad_enable , v.v_name as ad_vender" +
						" from t_ads a,t_vender v where a.ad_publisher = v.v_id and ad_enable=1 order by ad_createTime desc, ad_priority" ;
			}else if(!"".equals(keys) && "".equals(time)){
				sql = "select a.ad_id, a.ad_publisher, a.ad_type, a.ad_content, a.ad_createTime, a.ad_startTime, a.ad_endTime, a.ad_link, a.ad_imgPath, a.ad_priority, a.ad_enable , v.v_name as ad_vender" +
						" from t_ads a,t_vender v where a.ad_publisher = v.v_id and ad_content like '"+str+"' and ad_enable=1 order by ad_createTime desc, ad_priority" ;
			}else if(!"".equals(time) && "".equals(keys)){
				sql = "select a.ad_id, a.ad_publisher, a.ad_type, a.ad_content, a.ad_createTime, a.ad_startTime, a.ad_endTime, a.ad_link, a.ad_imgPath, a.ad_priority, a.ad_enable , v.v_name as ad_vender" +
						" from t_ads a,t_vender v where a.ad_publisher = v.v_id and (ad_startTime <='" + time+ "' and ad_endTime >='"+time+"') and ad_enable=1 order by ad_createTime desc, ad_priority" ;
			}else if(!"".equals(keys) && !"".equals(time)){ 
				sql ="select a.ad_id, a.ad_publisher, a.ad_type, a.ad_content, a.ad_createTime, a.ad_startTime, a.ad_endTime, a.ad_link, a.ad_imgPath, a.ad_priority, a.ad_enable , v.v_name as ad_vender" +
						" from t_ads a,t_vender v where a.ad_publisher = v.v_id and ad_content like '"+str+"'"+
						" and (ad_startTime <='" + time+ "' and ad_endTime >='"+time+"')) and ad_enable=1 order by ad_createTime desc, ad_priority" ;
			}
		}
		return sql;
	}

	@Override
	public int deleteAdsById(String adId) {
		String sql = "update t_ads set ad_enable = 0 where ad_id='"+adId+"'"; 
		int rows = jdbcTemplate.update(sql);
		return rows;
	}

	@Override
	public int createAds(final Ads ad) {
		String sql = "insert into t_ads"
			+ "(ad_id, ad_publisher, ad_content, ad_type, ad_imgPath, ad_createTime, ad_startTime, ad_endTime, ad_link, ad_priority, ad_enable, ad_memo) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ? ,? , ?, ?, ?)";

			int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) {
					try {
						ps.setString(1, ad.getId());
						ps.setString(2, ad.getPublisher());
						ps.setString(3, ad.getContent());
						ps.setString(4, ad.getType());
						ps.setString(5, ad.getImgPath());
						ps.setString(6, ad.getCreateTime());
						ps.setString(7, ad.getStartTime());
						ps.setString(8, ad.getEndTime());
						ps.setString(9, ad.getLink());
						ps.setInt(10, ad.getPriority());
						ps.setInt(11, ad.getEnable());
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
				ad.setPublisher(map.get("ad_publisher").toString());
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
				ad.setEnable(Integer.parseInt(map.get("ad_enable").toString()));
		}
		return ad;
	}

	@Override
	public int updateAdsById(String adId, final Ads ad) {
		String sql = "update t_ads  set ad_publisher=?, ad_content=?, ad_type=?, ad_imgPath=?, ad_startTime=?, ad_endTime=?, ad_link=?, ad_priority=?  where ad_id ='"+adId+"'";
		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ps.setString(1, ad.getPublisher());
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

	@Override
	public Vender getVenderById(String venderId) {
			Vender vender = new Vender();
			String sql = "select * from t_vender where v_id = '"+venderId+"'";
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
			if(rows!=null && rows.size()>0){
				Map<String, Object> map = (Map<String, Object>) rows.get(0);
				vender.setId(map.get("v_id").toString());
				vender.setEmail(map.get("v_email").toString());
				vender.setName(map.get("v_name").toString());
				vender.setPwd(map.get("v_pwd").toString());
				vender.setCreateTime(map.get("v_createTime").toString());
				vender.setDeployDNS(map.get("v_deployDNS").toString());
				if(map.get("v_effectiveTime") != null){
					vender.setEffectiveTime(map.get("v_effectiveTime").toString());
				}
				if(map.get("v_deadTime") != null){
					vender.setDeadTime(map.get("v_deadTime").toString());
				}
				vender.setServiceLevel(map.get("v_serviceLevel").toString());
				vender.setEnable(Integer.parseInt(map.get("v_enable").toString()));
				vender.setRole(map.get("v_role").toString());
			}
			return vender;
	}

	@Override
	public Vender getExistVender(String email) {
		Vender vender = new Vender();
		String sql = "select * from t_vender where v_email = '"+email+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if(rows!=null && rows.size()>0){
			Map<String, Object> map = (Map<String, Object>) rows.get(0);
			vender.setId(map.get("v_id").toString());
			vender.setEmail(map.get("v_email").toString());
			vender.setName(map.get("v_name").toString());
			vender.setPwd(map.get("v_pwd").toString());
			vender.setCreateTime(map.get("v_createTime").toString());
			vender.setDeployDNS(map.get("v_deployDNS").toString());
			if(map.get("v_effectiveTime") != null){
				vender.setEffectiveTime(map.get("v_effectiveTime").toString());
			}
			if(map.get("v_deadTime") != null){
				vender.setDeadTime(map.get("v_deadTime").toString());
			}
			vender.setServiceLevel(map.get("v_serviceLevel").toString());
			vender.setEnable(Integer.parseInt(map.get("v_enable").toString()));
			vender.setRole(map.get("v_role").toString());
		}
		return vender;
	}

	@Override
	public int changePwd(String newPwd, String venderId) {
		String sql = "update t_vender set v_pwd = '"+newPwd+"' where v_id='"+venderId+"'";
		int rows = jdbcTemplate.update(sql);
		return rows;
	}

	@Override
	public List<Vender> serarchVenders(String keys) {
		List<Vender> resList = new ArrayList<Vender>();
		String str = "%"+keys+"%";
		String sql="select * from t_vender where v_name like'"+str+"' order by v_enable desc,v_createTime desc";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Vender vender = new Vender();
				vender.setId(map.get("v_id").toString());
				vender.setEmail(map.get("v_email").toString());
				vender.setName(map.get("v_name").toString());
				vender.setPwd(map.get("v_pwd").toString());
				vender.setCreateTime(map.get("v_createTime").toString());
				vender.setDeployDNS(map.get("v_deployDNS").toString());
				if(map.get("v_effectiveTime") != null){
					vender.setEffectiveTime(map.get("v_effectiveTime").toString());
				}
				if(map.get("v_deadTime") != null){
					vender.setDeadTime(map.get("v_deadTime").toString());
				}
				vender.setServiceLevel(map.get("v_serviceLevel").toString());
				vender.setEnable(Integer.parseInt(map.get("v_enable").toString()));
				vender.setRole(map.get("v_role").toString());
				resList.add(vender);
			}
		}
		return resList;
	}

	@Override
	public Vender getVendersById(String venderId) {
		Vender vender = new Vender();
		String sql = "select * from t_vender where v_id = '"+venderId+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if(rows!=null && rows.size()>0){
			Map<String, Object> map = (Map<String, Object>) rows.get(0);
			vender.setId(map.get("v_id").toString());
			vender.setEmail(map.get("v_email").toString());
			vender.setName(map.get("v_name").toString());
			vender.setPwd(map.get("v_pwd").toString());
			vender.setCreateTime(map.get("v_createTime").toString());
			vender.setDeployDNS(map.get("v_deployDNS").toString());
			if(map.get("v_effectiveTime") != null){
				vender.setEffectiveTime(map.get("v_effectiveTime").toString());
			}
			if(map.get("v_deadTime") != null){
				vender.setDeadTime(map.get("v_deadTime").toString());
			}
			vender.setServiceLevel(map.get("v_serviceLevel").toString());
			vender.setEnable(Integer.parseInt(map.get("v_enable").toString()));
			vender.setRole(map.get("v_role").toString());
		}
		return vender;
	}

	@Override
	public int updateVendersById(String venderId, final Vender vender) {
				String sql = "update t_vender  set v_name=?, v_email=?, v_effectiveTime=?, v_deadTime=?, v_deployDNS=?, v_serviceLevel=?, v_enable=? where v_id ='"+venderId+"'";
				int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) {
						try {
							ps.setString(1, vender.getName());
							ps.setString(2, vender.getEmail());
							ps.setString(3, vender.getEffectiveTime());
							ps.setString(4, vender.getDeadTime()); 
							ps.setString(5, vender.getDeployDNS());
							ps.setString(6, vender.getServiceLevel());
							ps.setInt(7, vender.getEnable());

						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				});

			return res;
	}

	@Override
	public int createVender(final Vender vender) {
		String sql = "insert into t_vender"
				+ "(v_id, v_name, v_pwd, v_email, v_createTime, v_effectiveTime, v_deadTime, v_deployDNS, v_serviceLevel, v_enable,v_role, v_memo) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ? ,? , ?, ?, ?)";

				int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) {
						try {
							ps.setString(1, vender.getId());
							ps.setString(2, vender.getName());
							ps.setString(3, vender.getPwd());
							ps.setString(4, vender.getEmail());
							ps.setString(5, vender.getCreateTime());
							ps.setString(6, vender.getEffectiveTime());
							ps.setString(7, vender.getDeadTime());
							ps.setString(8, vender.getDeployDNS());
							ps.setString(9, vender.getServiceLevel());
							ps.setInt(10, vender.getEnable());
							ps.setString(11, vender.getRole());
							ps.setString(12, "");

						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				});

			return res;
	}

	
}
