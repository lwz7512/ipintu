package com.pintu.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.pintu.beans.Applicant;
import com.pintu.beans.Event;
import com.pintu.beans.Favorite;
import com.pintu.beans.Gift;
import com.pintu.beans.Message;
import com.pintu.beans.Story;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicDetails;
import com.pintu.beans.TPicItem;
import com.pintu.beans.Tag;
import com.pintu.beans.User;
import com.pintu.beans.Vote;
import com.pintu.beans.Wealth;
import com.pintu.dao.DBAccessInterface;
import com.pintu.utils.PintuUtils;

public class DBAccessImplement implements DBAccessInterface {

	private JdbcTemplate jdbcTemplate;

	// Inject by Spring
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	// Constructor
	public DBAccessImplement() {

	}

	@Override
	public int insertUser(final User user) {
		String sql = "INSERT INTO t_user "
				+ "(u_id, u_account, u_nickName,u_pwd, u_avatar, u_role, u_level, u_score, u_exchangeScore,u_registerTime,u_memo) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ? ,? ,?,?)";

		int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, user.getId());
					ps.setString(2, user.getAccount());
					ps.setString(3,user.getNickName());
					ps.setString(4, user.getPwd());
					ps.setString(5, user.getAvatar());
					ps.setString(6, user.getRole());
					ps.setInt(7, user.getLevel());
					ps.setInt(8, user.getScore());
					ps.setInt(9, user.getExchangeScore());
					ps.setString(10, user.getRegisterTime());
					ps.setString(11, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

		return res;
	}
	
	@Override
	public int updateUserScore(final List<User> userList) {
		String sql = "update t_user set u_score=u_score+? , u_exchangeScore=u_exchangeScore+? where u_id = ?";

		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						User user = userList.get(i);
						ps.setInt(1, user.getScore());
						ps.setInt(2, user.getExchangeScore());
						ps.setString(3, user.getId());
					}

					public int getBatchSize() {
						return userList.size();
					}
				});
		return res.length;
	}

	@Override
	public int insertPicture(final List<Object> objList) {
		String sql = "INSERT INTO t_picture "
				+ "(p_id,p_name,p_owner,p_publishTime,p_source,p_description,p_isOriginal,p_mobImgId,p_mobImgSize,p_mobImgPath,p_rawImgId,p_rawImgSize,p_rawImgPath,p_browseCount,p_pass,p_memo) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						TPicItem picture = (TPicItem) objList.get(i);
						ps.setString(1, picture.getId());
						ps.setString(2, picture.getName());
						ps.setString(3, picture.getOwner());
						ps.setString(4, picture.getPublishTime());
						ps.setString(5, picture.getSource());
						ps.setString(6, picture.getDescription());
						ps.setInt(7, picture.getIsOriginal());
						ps.setString(8, picture.getMobImgId());
						ps.setString(9, picture.getMobImgSize());
						ps.setString(10, picture.getMobImgPath());
						ps.setString(11, picture.getRawImgId());
						ps.setString(12, picture.getRawImgSize());
						ps.setString(13, picture.getRawImgPath());
						ps.setInt(14, picture.getBrowseCount());
						ps.setInt(15, picture.getPass());
						ps.setString(16, "");
					}

					public int getBatchSize() {
						return objList.size();
					}
				});
		return res.length;
	}

	@Override
	public List<TPicItem> getPictureForCache(String startTime,String endTime) {
		List<TPicItem> resList = new ArrayList<TPicItem>();
		String sql = "select * from t_picture where p_publishTime >='" + startTime
				+ "' and p_publishTime <='"+endTime+"' and p_pass=1";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				TPicItem tpicItem = new TPicItem();
				tpicItem.setId(map.get("p_id").toString());
				tpicItem.setName(map.get("p_name").toString());
				tpicItem.setOwner(map.get("p_owner").toString());
				tpicItem.setPublishTime(map.get("p_publishTime").toString());
				tpicItem.setDescription(map.get("p_description").toString());
				tpicItem.setSource(map.get("p_source").toString());
				tpicItem.setIsOriginal(Integer.parseInt(map.get("p_isOriginal")
						.toString()));
				tpicItem.setMobImgId(map.get("p_mobImgId").toString());
				tpicItem.setMobImgSize(map.get("p_mobImgSize").toString());
				tpicItem.setMobImgPath(map.get("p_mobImgPath").toString());
				tpicItem.setRawImgId(map.get("p_rawImgId").toString());
				tpicItem.setRawImgSize(map.get("p_rawImgSize").toString());
				tpicItem.setRawImgPath(map.get("p_rawImgPath").toString());
				tpicItem.setBrowseCount(Integer.parseInt(map.get("p_browseCount")
						.toString()));
				resList.add(tpicItem);
			}
		}
		return resList;
	}

	@Override
	public List<Story> getStoryForCache(String picIds) {
		List<Story> resList = new ArrayList<Story>();
//		String sql = "select * from t_story where s_publishTime >='" + today + "'";
		String sql = "select * from t_story where s_follow in (" + picIds + ")";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Story story = new Story();
				story.setId(map.get("s_id").toString());
				story.setFollow(map.get("s_follow").toString());
				story.setOwner(map.get("s_owner").toString());
				story.setPublishTime(map.get("s_publishTime").toString());
				story.setContent(map.get("s_content").toString());
				story.setClassical(Integer.parseInt(map.get("s_classical").toString()));
				resList.add(story);
			}
		}
		
		return resList;
	}


	@Override
	public List<Vote> getVoteForCache(String picIds) {
		List<Vote> resList = new ArrayList<Vote>();
		String sql = "select * from t_vote where v_follow in (" + picIds + ")";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Vote vote = new Vote();
				vote.setId(map.get("v_id").toString());
				vote.setFollow(map.get("v_follow").toString());
				vote.setType(map.get("v_type").toString());
				vote.setAmount(Integer.parseInt(map.get("v_amount").toString()));
				resList.add(vote);
			}
		}
		return resList;
	}

	@Override
	public User getUserById(String id) {
		User user = new User();
		String sql = "select * from t_user where u_id = '"+id+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if(rows!=null && rows.size()>0){
			Map<String, Object> map = (Map<String, Object>) rows.get(0);
			user.setId(map.get("u_id").toString());
			user.setAccount(map.get("u_account").toString());
			user.setAvatar(map.get("u_avatar").toString());
			user.setNickName(map.get("u_nickName").toString());
			user.setRegisterTime(map.get("u_registerTime").toString());
			user.setRole(map.get("u_role").toString());
			user.setLevel(Integer.parseInt(map.get("u_level").toString()));
			user.setScore(Integer.parseInt(map.get("u_score").toString()));
			user.setExchangeScore(Integer.parseInt(map.get("u_exchangeScore").toString()));
		}
		return user;
	}


	@Override
	public int insertStory(final List<Object> objList) {
		String sql = "INSERT INTO t_story "
				 + "(s_id,s_follow,s_owner,s_publishTime,s_content,s_classical,s_memo) "
				 + "VALUES (?, ?, ?, ?, ?, ?, ?)";
				
		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						Story story = (Story) objList.get(i);
						ps.setString(1, story.getId());
						ps.setString(2, story.getFollow());
						ps.setString(3, story.getOwner());
						ps.setString(4, story.getPublishTime());
						ps.setString(5, story.getContent());
						ps.setInt(6, story.getClassical());
						ps.setString(7, "");
					}

					public int getBatchSize() {
						return objList.size();
					}
				});
		return res.length;
	}


	@Override
	public List<Story> getStoriesOfPic(String tpId) {
		List<Story> storyList = new ArrayList<Story>();
		String sql = "select * from t_story where s_follow = '"+tpId+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Story story = new Story();
				story.setId(map.get("s_id").toString());
				story.setFollow(map.get("s_follow").toString());
				story.setOwner(map.get("s_owner").toString());
				story.setPublishTime(map.get("s_publishTime").toString());
				story.setContent(map.get("s_content").toString());
				story.setClassical(Integer.parseInt(map.get("s_classical").toString()));
				storyList.add(story);
			}
		}
		return storyList;
	}

	@Override
	public int insertVote(final Vote vote) {
		String sql = "INSERT INTO t_vote "
				 + "(v_id,v_follow,v_type,v_amount,v_memo) "
				 + "VALUES (?, ?, ?, ?, ?)";

		int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, vote.getId());
					ps.setString(2, vote.getFollow());
					ps.setString(3, vote.getType());
					ps.setInt(4, vote.getAmount());
					ps.setString(5, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

		return res;
	}
	
	
	@Override
	public int updateVote(final Vote vote) {
		String sql = "update t_vote  set v_amount = v_amount +? where v_type = ? and v_follow = ?";

		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ps.setInt(1, vote.getAmount());
						ps.setString(2, vote.getType());
						ps.setString(3, vote.getFollow());
						
					}

					public int getBatchSize() {
						return 1;
					}
				});
		return res.length;
	}


	@Override
	public List<Vote> getVoteOfPic(String picId) {
		List<Vote> voteList = new ArrayList<Vote>();
		String sql = "select * from t_vote where v_follow = '"+picId+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Vote vote = new Vote();
				vote.setId(map.get("v_id").toString());
				vote.setFollow(map.get("v_follow").toString());
				vote.setType(map.get("v_type").toString());
				vote.setAmount(Integer.parseInt(map.get("v_amount").toString()));
				voteList.add(vote);
			}
		}
		return voteList;
	}

	@Override
	public List<Vote> getVoteByFollowAndType(String picId, String type) {
		List<Vote> voteList = new ArrayList<Vote>();
		String sql = "select * from t_vote where v_follow = '"+picId+"' and v_type = '"+type+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Vote vote = new Vote();
				vote.setId(map.get("v_id").toString());
				vote.setFollow(map.get("v_follow").toString());
				vote.setType(map.get("v_type").toString());
				vote.setAmount(Integer.parseInt(map.get("v_amount").toString()));
				voteList.add(vote);
			}
		}
		return voteList;
	}

	@Override
	public TPicItem getPictureById(String tpId) {
		TPicItem pic = new TPicItem();
		String sql = "select * from t_picture where p_id = '"+tpId+"' and p_pass=1";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if(rows!=null && rows.size()>0){
			Map<String, Object> map = (Map<String, Object>) rows.get(0);
			pic.setId(map.get("p_id").toString());
			pic.setName(map.get("p_name").toString());
			pic.setOwner(map.get("p_owner").toString());
			pic.setPublishTime(map.get("p_publishTime").toString());
			pic.setSource(map.get("p_source").toString());
			pic.setDescription(map.get("p_description").toString());
			pic.setIsOriginal(Integer.parseInt(map.get("p_isOriginal").toString()));
			pic.setMobImgId(map.get("p_mobImgId").toString());
			pic.setMobImgSize(map.get("p_mobImgSize").toString());
			pic.setMobImgPath(map.get("p_mobImgPath").toString());
			pic.setRawImgId(map.get("p_rawImgId").toString());
			pic.setRawImgSize(map.get("p_rawImgSize").toString());
			pic.setRawImgPath(map.get("p_rawImgPath").toString());
			pic.setPass(Integer.parseInt(map.get("p_pass").toString()));
			pic.setBrowseCount(Integer.parseInt(map.get("p_browseCount").toString()));
		}
		return pic;
	}

	@Override
	public int insertMessage(final Message msg) {
		String sql = "INSERT INTO t_message "
				 + "(m_id,m_sender,m_receiver,m_content,m_writeTime,m_read,m_reference,m_msgType,m_memo) "
				 + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, msg.getId());
					ps.setString(2, msg.getSender());
					ps.setString(3, msg.getReceiver());
					ps.setString(4, msg.getContent());
					ps.setString(5, msg.getWriteTime());
					ps.setInt(6, msg.getRead());
					ps.setString(7, msg.getReference());
					ps.setString(8, msg.getMsgType());
					ps.setString(9, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});
		return res;

	}

	@Override
	public List<Message> getUserMessages(String userId) {
		List<Message> msgList = new ArrayList<Message>();
		String sql = "select * from t_message where  m_receiver = '"+userId+"' and m_read=0";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Message msg = new Message();
				msg.setId(map.get("m_id").toString());
				msg.setReceiver(map.get("m_receiver").toString());
				msg.setSender(map.get("m_sender").toString());
				msg.setReference(map.get("m_reference").toString());
				msg.setMsgType(map.get("m_msgType").toString());
				msg.setWriteTime(map.get("m_writeTime").toString());
				msg.setRead(Integer.parseInt(map.get("m_read").toString()));
				msg.setContent(map.get("m_content").toString());
				msgList.add(msg);
			}
		}
		return msgList;
	}

	@Override
	public int updateMsg(final List<String> msgIdList) {
		String sql = "update t_message  set m_read = 1 where m_id =?";
		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ps.setString(1, msgIdList.get(i));
					}

					public int getBatchSize() {
						return msgIdList.size();
					}
				});
		return res.length;
	}

	@Override
	public List<Story> getClassicalPintu() {
		List<Story> list = new ArrayList<Story>();
		String sql = "select * from t_story where s_classical = 1 ";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Story story = new Story();
				story.setId(map.get("s_id").toString());
				story.setFollow(map.get("s_follow").toString());
				story.setOwner(map.get("s_owner").toString());
				story.setContent(map.get("s_content").toString());
				story.setClassical(Integer.parseInt(map.get("s_classical").toString()));
				story.setPublishTime(map.get("s_publishTime").toString());
				list.add(story);
			}
		}
		return list;
	}

	@Override
	public int updateStoryClassical(final List<String> storyIds) {
		String sql = "update t_story  set s_classical = 1 where s_id =?";
		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ps.setString(1, storyIds.get(i));
					}

					public int getBatchSize() {
						return storyIds.size();
					}
				});
		return res.length;
	}

	@Override
	public int updateUserExchageScore(final String userId,final int remainScore) {
		String sql = "update t_user  set u_exchangeScore = ? where u_id =?";
		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ps.setInt(1, remainScore);
						ps.setString(2, userId);
					}

					public int getBatchSize() {
						return 1;
					}
				});
		return res.length;
	}

	@Override
	public int updateUserLevel(final String userId,final int level) {
		String sql = "update t_user  set u_level ="+level+" where u_id ='"+userId+"'";
		int rows = jdbcTemplate.update(sql); 
		return rows;
	}

	@Override
	public int updateUserLevel(final List<Map<String, Integer>> idLevelList) {
		String sql = "update t_user  set u_level = ? where u_id =?";
		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						Map<String,Integer> map = idLevelList.get(i);
						for(String userId:map.keySet()){
						ps.setInt(1, map.get(userId));
						ps.setString(2, userId);
						}
					}

					public int getBatchSize() {
						return idLevelList.size();
					}
				});
		return res.length;
	}
	
	@Override
	public int insertOnesWealth(final List<Wealth> wList) {
		String sql = "INSERT INTO t_wealth (w_id,w_owner,w_type,w_amount,w_memo) values (?,?,?,?,?)";
		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						Wealth wealth=wList.get(i);
						ps.setString(1, wealth.getId());
						ps.setString(2, wealth.getOwner());
						ps.setString(3, wealth.getType());
						ps.setInt(4, wealth.getAmount());
						ps.setString(5, "");
					}
					public int getBatchSize() {
						return wList.size();
					}
				});
		return res.length;
	}

	@Override
	public int updateOnesWealth(final List<Wealth> wList) {
		String sql = "update t_wealth set w_amount=? where w_type=? and w_owner=? ";
		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						Wealth w = wList.get(i);
						ps.setInt(1, w.getAmount());
						ps.setString(2, w.getType());
						ps.setString(3, w.getOwner());
					}

					public int getBatchSize() {
						return wList.size();
					}
				});
		return res.length;
	}

	@Override
	public int deleteOnesWealth(String type, String userId) {
		String sql = "delete from t_wealth where w_type='"+type+"' and w_owner='"+userId+"'"; 
		int rows = jdbcTemplate.update(sql);
		return rows;
	}
	
	@Override
	public Map<String,Integer> getOnesPicCountByTime(String startTime, String endTime) {
		String sql = "select p_owner,count(p_id) from t_picture where p_publishTime >='"
				+ startTime + "' and p_publishTime <='" + endTime + "' and p_pass=1 group by p_owner";
		Map<String,Integer> resMap = new HashMap<String,Integer>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if(rows!=null && rows.size()>0){
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				resMap.put(map.get("p_owner").toString(),Integer.parseInt(map.get("count(p_id)").toString()));
			}
		}
		return resMap;
	}

	@Override
	public Map<String,Integer> getOnesStoryCountByTime(String startTime, String endTime) {
		String sql = "select s_owner,count(s_id) from t_story where s_publishTime >='"
				+ startTime + "' and s_publishTime <='" + endTime + "' group by s_owner";
		Map<String,Integer> resMap = new HashMap<String,Integer>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if(rows!=null && rows.size()>0){
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				resMap.put(map.get("s_owner").toString(),Integer.parseInt(map.get("count(s_id)").toString()));
			}
		}
		return resMap;
	}

	@Override
	public Map<String, Integer> getUserExchangeInfo(String userIds) {
		String sql = "select u_id,u_exchangeScore from t_user where u_id in ("+userIds+")";
		Map<String,Integer> resMap = new HashMap<String,Integer>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if(rows!=null && rows.size()>0){
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				String id = map.get("u_id").toString();
				int exchangeScore = Integer.parseInt(map.get("u_exchangeScore").toString());
				resMap.put(id,exchangeScore);
			}
		}
		return resMap;
	}

	@Override
	public Map<String, Integer> getUserScoreInfo(String userIds) {
		String sql = "select u_id,u_score from t_user where u_id in ("+userIds+")";
		Map<String,Integer> resMap = new HashMap<String,Integer>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if(rows!=null && rows.size()>0){
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				String id = map.get("u_id").toString();
				int score = Integer.parseInt(map.get("u_score").toString());
				resMap.put(id,score);
			}
		}
		return resMap;
	}

	@Override
	public List<Wealth> getUsersWealthInfo(String userId) {
		String sql = "select * from t_wealth where w_owner = '"+userId+"'";
		List<Wealth> wealthList = new ArrayList<Wealth>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if(rows!=null && rows.size()>0){
			for (int i = 0; i < rows.size(); i++) {
				Wealth wealth = new Wealth();
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				wealth.setId(map.get("w_id").toString());
				wealth.setOwner(map.get("w_owner").toString());
				wealth.setType(map.get("w_type").toString());
				wealth.setAmount(Integer.parseInt(map.get("w_amount").toString()));
				wealthList.add(wealth);
			}
		}
		return wealthList;
	}

	@Override
	public List<Vote> getAllVote() {
		List<Vote> resList = new ArrayList<Vote>();
		String sql = "select * from t_vote";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Vote vote = new Vote();
				vote.setId(map.get("v_id").toString());
				vote.setFollow(map.get("v_follow").toString());
				vote.setType(map.get("v_type").toString());
				vote.setAmount(Integer.parseInt(map.get("v_amount").toString()));
				resList.add(vote);
			}
		}
		return resList;
	}

	@Override
	public List<Story> getClassicalPintuByIds(String ids) {
		String sql = "select * from t_story where s_id in (" + ids + ")";
		List<Story> storyList = new ArrayList<Story>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Story story = new Story();
				story.setId(map.get("s_id").toString());
				story.setFollow(map.get("s_follow").toString());
				story.setOwner(map.get("s_owner").toString());
				story.setPublishTime(map.get("s_publishTime").toString());
				story.setContent(map.get("s_content").toString());
				story.setClassical(Integer.parseInt(map.get("s_classical").toString()));
				storyList.add(story);
			}
		}
		return storyList;
	}

	@Override
	public List<Wealth> getOnesWealth(String userId) {
		List<Wealth> resList = new ArrayList<Wealth>();
		String sql = "select * from t_wealth where  w_owner = '"+userId+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Wealth wealth = new Wealth();
				wealth.setId(map.get("w_id").toString());
				wealth.setOwner(map.get("w_owner").toString());
				wealth.setType(map.get("w_type").toString());
				wealth.setAmount(Integer.parseInt(map.get("w_amount").toString()));
				resList.add(wealth);
			}
		}
		return resList;
	}

	@Override
	public int insertFavorite(final Favorite fav) {
		String sql = "INSERT INTO t_favorite "
				 + "(f_id,f_owner,f_picture,f_collectTime,f_memo) "
				 + "VALUES (?, ?, ?, ?, ?)";

		int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, fav.getId());
					ps.setString(2, fav.getOwner());
					ps.setString(3, fav.getPicture());
					ps.setString(4, fav.getCollectTime());
					ps.setString(5, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

		return res;
	}

	@Override
	public int deleteFavoriteById(String fId) {
		String sql = "delete from t_favorite where f_id ='"+fId+"'"; 
		int rows = jdbcTemplate.update(sql);
		return rows;
	}

	@Override
	public int checkExistFavorite(String userId, String picId) {
		String sql = "select count(f_id) from t_favorite where f_owner = '"+userId+"' and f_picture = '"+picId+"'";
		int counts = jdbcTemplate.queryForInt(sql);
		return counts;
	}

	@Override
	public List<TPicItem> getFavoriteTpics(String userId, int pageNum, int pageSize) {
		List<TPicItem> resList = new ArrayList<TPicItem>();
		int startLine = (pageNum -1)*pageSize;
		String sql = "select p.p_id,p.p_name,p.p_owner,p.p_publishTime,p.p_description,p.p_source,p.p_isOriginal,p.p_browseCount," +
				"p.p_mobImgId,p.p_mobImgSize,p.p_mobImgPath,p.p_rawImgId,p.p_rawImgSize,p.p_rawImgPath" +
				" from t_picture p, t_favorite f where p.p_id = f.f_picture and f.f_owner = '"+userId+"' and p. p_pass=1 limit "+startLine+","+pageSize;
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				TPicItem tpicItem = new TPicItem();
				tpicItem.setId(map.get("p_id").toString());
				tpicItem.setName(map.get("p_name").toString());
				tpicItem.setOwner(map.get("p_owner").toString());
				tpicItem.setPublishTime(map.get("p_publishTime").toString());
				tpicItem.setDescription(map.get("p_description").toString());
				tpicItem.setSource(map.get("p_source").toString());
				tpicItem.setIsOriginal(Integer.parseInt(map.get("p_isOriginal")
						.toString()));
				tpicItem.setMobImgId(map.get("p_mobImgId").toString());
				tpicItem.setMobImgSize(map.get("p_mobImgSize").toString());
				tpicItem.setMobImgPath(map.get("p_mobImgPath").toString());
				tpicItem.setRawImgId(map.get("p_rawImgId").toString());
				tpicItem.setRawImgSize(map.get("p_rawImgSize").toString());
				tpicItem.setRawImgPath(map.get("p_rawImgPath").toString());
				tpicItem.setBrowseCount(Integer.parseInt(map.get("p_browseCount").toString()));
				resList.add(tpicItem);
			}
		}
		return resList;
	}

	@Override
	public List<Story> getStoriesByUser(String userId, int pageNum,int pageSize) {
		int startLine = (pageNum -1)*pageSize;
		String sql = "select * from t_story  where s_owner ='"+userId+"' order by s_publishTime desc limit "+startLine+","+pageSize;
		List<Story> storyList = new ArrayList<Story>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Story story = new Story();
				story.setId(map.get("s_id").toString());
				story.setFollow(map.get("s_follow").toString());
				story.setOwner(map.get("s_owner").toString());
				story.setPublishTime(map.get("s_publishTime").toString());
				story.setContent(map.get("s_content").toString());
				story.setClassical(Integer.parseInt(map.get("s_classical").toString()));
				storyList.add(story);
			}
		}
		return storyList;
	}

	@Override
	public List<TPicItem> getTpicsByUser(String userId, int pageNum,int pageSize) {
		int startLine = (pageNum -1)*pageSize;
		List<TPicItem> resList = new ArrayList<TPicItem>();
		String sql = "select * from t_picture where p_owner = '"+userId+"' and p_pass=1 order by p_publishTime desc limit "+startLine+","+pageSize;
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				TPicItem tpicItem = new TPicItem();
				tpicItem.setId(map.get("p_id").toString());
				tpicItem.setName(map.get("p_name").toString());
				tpicItem.setOwner(map.get("p_owner").toString());
				tpicItem.setPublishTime(map.get("p_publishTime").toString());
				tpicItem.setDescription(map.get("p_description").toString());
				tpicItem.setSource(map.get("p_source").toString());
				tpicItem.setIsOriginal(Integer.parseInt(map.get("p_isOriginal")
						.toString()));
				tpicItem.setMobImgId(map.get("p_mobImgId").toString());
				tpicItem.setMobImgSize(map.get("p_mobImgSize").toString());
				tpicItem.setMobImgPath(map.get("p_mobImgPath").toString());
				tpicItem.setRawImgId(map.get("p_rawImgId").toString());
				tpicItem.setRawImgSize(map.get("p_rawImgSize").toString());
				tpicItem.setRawImgPath(map.get("p_rawImgPath").toString());
				tpicItem.setBrowseCount(Integer.parseInt(map.get("p_browseCount")
						.toString()));
				resList.add(tpicItem);
			}
		}
		return resList;
	}

	@Override
	public List<Gift> getExchangeableGifts() {
		List<Gift> resList = new ArrayList<Gift>();
		String sql = "select * from t_gift where  g_amount > 0";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Gift gift = new Gift();
				gift.setId(map.get("g_id").toString());
				gift.setName(map.get("g_name").toString());
				gift.setType(map.get("g_type").toString());
				gift.setValue(Integer.parseInt(map.get("g_value").toString()));
				gift.setAmount(Integer.parseInt(map.get("g_amount").toString()));
				gift.setImgPath(map.get("g_imgPath").toString());
				resList.add(gift);
			}
		}
		return resList;
	}

	@Override
	public List<Event> getCommunityEvents(String today) {
		List<Event> resList = new ArrayList<Event>();
		String sql = "select * from t_event where  e_eventTime >='"+today+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Event eve = new Event();
				eve.setId(map.get("e_id").toString());
				eve.setDetail(map.get("e_detail").toString());
				eve.setTitle(map.get("e_title").toString());
				eve.setEventTime(map.get("e_eventTime").toString());
				resList.add(eve);
			}
		}
		return resList;
	}

	@Override
	public int insertGift(final Gift gift) {
		String sql = "INSERT INTO t_gift "
				 + "(g_id,g_name,g_type,g_value,g_imgPath,g_amount,g_memo) "
				 + "VALUES (?, ?, ?, ?, ?, ?, ?)";

		int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, gift.getId());
					ps.setString(2, gift.getName());
					ps.setString(3, gift.getType());
					ps.setInt(4, gift.getValue());
					ps.setString(5, gift.getImgPath());
					ps.setInt(5, gift.getAmount());
					ps.setString(6, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

		return res;
	}

	@Override
	public int insertEvent(final Event event) {
		String sql = "INSERT INTO t_event "
				 + "(e_id,e_title,e_detail,e_eventTime,e_memo) "
				 + "VALUES (?, ?, ?, ?, ?)";
		int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, event.getId());
					ps.setString(2, event.getTitle());
					ps.setString(3, event.getDetail());
					ps.setString(4, event.getEventTime());
					ps.setString(5, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

		return res;
	}

	@Override
	public int getTPicCountByUser(String userId) {
		String sql = "select count(p_id) from t_picture where p_owner = '"+userId+"' and p_pass=1";
		int counts = jdbcTemplate.queryForInt(sql);
		return counts;
	}

	@Override
	public int getStoryCountByUser(String userId) {
		String sql = "select count(s_id) from t_story where s_owner = '"+userId+"'";
		int counts = jdbcTemplate.queryForInt(sql);
		return counts;
	}

	@Override
	public User getExistUser(String account) {
		String sql = "select * from t_user where u_account = '"+account+"'"; 
		User user = new User();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				user.setId(map.get("u_id").toString());
				user.setAccount(map.get("u_account").toString());
				user.setNickName(map.get("u_nickName").toString());
			    user.setPwd(map.get("u_pwd").toString());
			    user.setAvatar(map.get("u_avatar").toString());
			    user.setRegisterTime(map.get("u_registerTime").toString());
			    user.setRole(map.get("u_role").toString());
			    user.setLevel(Integer.parseInt(map.get("u_level").toString()));
			    user.setScore(Integer.parseInt(map.get("u_score").toString()));
			    user.setExchangeScore(Integer.parseInt(map.get("u_exchangeScore").toString()));
			}
		}
		return user;
	}


	@Override
	public int insertApplicant(final Applicant tempUser) {
		String sql = "INSERT INTO t_applicant "
				+ "(a_id, a_account, a_applyReason,a_inviteCode,a_passed,a_memo) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, tempUser.getId());
					ps.setString(2, tempUser.getAccount());
					ps.setString(3, tempUser.getApplyReason());
					ps.setString(4, tempUser.getInviteCode());
					ps.setInt(5, tempUser.getPassed());
					ps.setString(6, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

		return res;
	}

	@Override
	public List<Applicant> getApplicant() {
		List<Applicant> resList = new ArrayList<Applicant>();
		String sql = "select * from t_applicant where  a_passed=0 and (isNull(a_inviteCode) or a_inviteCode='')";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Applicant user = new Applicant();
				user.setId(map.get("a_id").toString());
				user.setAccount(map.get("a_account").toString());
				user.setApplyReason(map.get("a_applyReason").toString());
				resList.add(user);
			}
		}
		return resList;
	}

	@Override
	public int deleteTempUser(String userId) {
		String sql = "delete from t_applicant where a_id ='"+userId+"'"; 
		int rows = jdbcTemplate.update(sql);
		return rows;
	}

	@Override
	public int updateApplicant(final String inviteCode,final String id) {
		String sql = "update t_applicant  set a_inviteCode ='"+inviteCode +"' , a_passed=1 where a_id ='"+id+"'";
		int rows = jdbcTemplate.update(sql);
		return rows;
	}
	
	@Override
	public String getExistApplicant(String account,String inviteCode) {
		String sql = "select a_id from t_applicant where a_account = '"+account+"' and a_inviteCode ='"+inviteCode+"' and a_passed=1"; 
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		String id="";
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				id =map.get("a_id").toString();
			}
		}
		return id;
	}

	@Override
	public int updatePicBrowseCount(final List<Map<String, Integer>> browseCountList) {
		String sql = "update t_picture  set p_browseCount = p_browseCount +? where p_id=?";
		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
							Map<String,Integer> map = browseCountList.get(i);
							for(String picId:map.keySet()){
								int count = map.get(picId);
								ps.setInt(1, count);
								ps.setString(2, picId);
						}
					}

					public int getBatchSize() {
						return browseCountList.size();
					}
				});
		return res.length;
	}

	@Override
	public List<TPicDetails> classicalStatistics(int classicalNum) {
		List<TPicDetails> resList = new ArrayList<TPicDetails>();
		String sql = "select p.p_id,p.p_name,p.p_owner,p.p_publishTime,p.p_description,p.p_source,p.p_isOriginal,p.p_browseCount," +
				"p.p_mobImgId,p.p_mobImgSize,p.p_mobImgPath,p.p_rawImgId,p.p_rawImgSize,p.p_rawImgPath," +
				"u.u_nickName,u.u_avatar,u.u_score,u.u_level" +
				" from t_picture p,t_user u where u.u_id=p.p_owner and p.p_pass=1 and p.p_browseCount >"+ classicalNum+" order by p.p_browseCount desc limit 12";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				TPicDetails tpic = new TPicDetails();
				tpic.setId(map.get("p_id").toString());
				tpic.setName(map.get("p_name").toString());
				tpic.setOwner(map.get("p_owner").toString());
				tpic.setPublishTime(map.get("p_publishTime").toString());
				tpic.setDescription(map.get("p_description").toString());
				tpic.setSource(map.get("p_source").toString());
				tpic.setIsOriginal(Integer.parseInt(map.get("p_isOriginal")
						.toString()));
				tpic.setMobImgId(map.get("p_mobImgId").toString());
				tpic.setMobImgSize(map.get("p_mobImgSize").toString());
				tpic.setMobImgPath(map.get("p_mobImgPath").toString());
				tpic.setRawImgId(map.get("p_rawImgId").toString());
				tpic.setRawImgSize(map.get("p_rawImgSize").toString());
				tpic.setRawImgPath(map.get("p_rawImgPath").toString());
				tpic.setBrowseCount(Integer.parseInt(map.get("p_browseCount").toString()));
				tpic.setAuthor(map.get("u_nickName").toString());
				tpic.setLevel(Integer.parseInt(map.get("u_level").toString()));
				tpic.setScore(Integer.parseInt(map.get("u_score").toString()));
				tpic.setAvatarImgPath(map.get("u_avatar").toString());
				resList.add(tpic);
			}
		}
		return resList;
	}

	@Override
	public List<TPicDetails> collectStatistics() {
		List<TPicDetails> resList = new ArrayList<TPicDetails>();
		String sql = "select p.p_id,p.p_name,p.p_owner,p.p_publishTime,p.p_description,p.p_source,p.p_isOriginal,p.p_browseCount," +
				"p.p_mobImgId,p.p_mobImgSize,p.p_mobImgPath,p.p_rawImgId,p.p_rawImgSize,p.p_rawImgPath," +
				"u.u_nickName,u.u_avatar,u.u_score,u.u_level " +
				"from t_picture p,t_user u,t_favorite f where u.u_id=p.p_owner and f.f_picture=p.p_id and p.p_pass=1 " +
				"order by f.f_collectTime desc limit 12";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				TPicDetails tpic = new TPicDetails();
				tpic.setId(map.get("p_id").toString());
				tpic.setName(map.get("p_name").toString());
				tpic.setOwner(map.get("p_owner").toString());
				tpic.setPublishTime(map.get("p_publishTime").toString());
				tpic.setDescription(map.get("p_description").toString());
				tpic.setSource(map.get("p_source").toString());
				tpic.setIsOriginal(Integer.parseInt(map.get("p_isOriginal")
						.toString()));
				tpic.setMobImgId(map.get("p_mobImgId").toString());
				tpic.setMobImgSize(map.get("p_mobImgSize").toString());
				tpic.setMobImgPath(map.get("p_mobImgPath").toString());
				tpic.setRawImgId(map.get("p_rawImgId").toString());
				tpic.setRawImgSize(map.get("p_rawImgSize").toString());
				tpic.setRawImgPath(map.get("p_rawImgPath").toString());
				tpic.setBrowseCount(Integer.parseInt(map.get("p_browseCount").toString()));
				tpic.setAuthor(map.get("u_nickName").toString());
				tpic.setLevel(Integer.parseInt(map.get("u_level").toString()));
				tpic.setScore(Integer.parseInt(map.get("u_score").toString()));
				tpic.setAvatarImgPath(map.get("u_avatar").toString());
				resList.add(tpic);
			}
		}
		
		return resList;
	}

	@Override
	public List<TPicDetails> getGalleryForWeb(int pageNum,int pageSize) {
		List<TPicDetails> resList = new ArrayList<TPicDetails>();
		int startLine = (pageNum -1)*pageSize;
		String sql = "select p.p_id,p.p_name,p.p_owner,p.p_publishTime,p.p_description,p.p_source,p.p_isOriginal,p.p_browseCount," +
				"p.p_mobImgId,p.p_mobImgSize,p.p_mobImgPath,p.p_rawImgId,p.p_rawImgSize,p.p_rawImgPath," +
				"u.u_nickName,u.u_avatar,u.u_score,u.u_level " +
				"from t_picture p,t_user u where u.u_id=p.p_owner and p.p_pass=1 " +
				"order by p.p_publishTime desc limit "+startLine+","+pageSize;
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				TPicDetails tpic = new TPicDetails();
				tpic.setId(map.get("p_id").toString());
				tpic.setName(map.get("p_name").toString());
				tpic.setOwner(map.get("p_owner").toString());
				tpic.setPublishTime(map.get("p_publishTime").toString());
				tpic.setDescription(map.get("p_description").toString());
				tpic.setSource(map.get("p_source").toString());
				tpic.setIsOriginal(Integer.parseInt(map.get("p_isOriginal")
						.toString()));
				tpic.setMobImgId(map.get("p_mobImgId").toString());
				tpic.setMobImgSize(map.get("p_mobImgSize").toString());
				tpic.setMobImgPath(map.get("p_mobImgPath").toString());
				tpic.setRawImgId(map.get("p_rawImgId").toString());
				tpic.setRawImgSize(map.get("p_rawImgSize").toString());
				tpic.setRawImgPath(map.get("p_rawImgPath").toString());
				tpic.setBrowseCount(Integer.parseInt(map.get("p_browseCount").toString()));
				tpic.setAuthor(map.get("u_nickName").toString());
				tpic.setLevel(Integer.parseInt(map.get("u_level").toString()));
				tpic.setScore(Integer.parseInt(map.get("u_score").toString()));
				tpic.setAvatarImgPath(map.get("u_avatar").toString());
				resList.add(tpic);
			}
		}
		return resList;
	}

	@Override
	public String searchTags(String tag) {
		String sql ="select t_id from t_tag where t_name='"+tag+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		String id="";
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				id =map.get("t_id").toString();
			}
		}
		return id;
	}

	@Override
	public String insertTag(final Tag tag) {
		String sql = "insert into t_tag(t_id,t_name,t_type,t_browseCount,t_memo) values (?,?,?,?,?)";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1,tag.getId());
					ps.setString(2, tag.getName());
					ps.setString(3, tag.getType());
					ps.setInt(4, tag.getBrowseCount());
					ps.setString(5, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		
		return tag.getId();
	}
	
	@Override
	public int updateTagBrowse(String tagId) {
		String sql="update t_tag set t_browseCount=t_browseCount+1 where t_id ='"+tagId+"'";
		int res = jdbcTemplate.update(sql);
		return res;
	}

	@Override
	public int insertCategory(final String id,final String picId, final String tagId) {
		String sql = "insert into t_category (c_id,c_picture,c_tag,c_memo) values (?,?,?,?)";
		int res=jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, id);
					ps.setString(2, picId);
					ps.setString(3, tagId);
					ps.setString(4, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		
		return res;
	}

	@Override
	public List<Tag> getHotTags(int topNum) {
		List<Tag> resList = new ArrayList<Tag>();
		String sql = "select * from t_tag order by t_browseCount desc  limit "+topNum;
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Tag tag = new Tag();
				tag.setId(map.get("t_id").toString());
				tag.setName(map.get("t_name").toString());
				tag.setType(map.get("t_type").toString());
				tag.setBrowseCount(Integer.parseInt(map.get("t_browseCount").toString()));
				resList.add(tag);
			}
		}
		return resList;
	}
	
	@Override
	public List<Tag> geSystemTags() {
		List<Tag> resList = new ArrayList<Tag>();
		String sql = "select * from t_tag where t_type='system'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Tag tag = new Tag();
				tag.setId(map.get("t_id").toString());
				tag.setName(map.get("t_name").toString());
				tag.setType(map.get("t_type").toString());
				tag.setBrowseCount(Integer.parseInt(map.get("t_browseCount").toString()));
				resList.add(tag);
			}
		}
		return resList;
	}

	@Override
	public int deleteCmtById(String sId) {
		String sql = "delete from t_story where s_id ='"+sId+"'"; 
		int rows = jdbcTemplate.update(sql);
		return rows;
	}

	@Override
	public int deletePictureById(String pId) {
		String sql = "delete from t_picture where p_id ='"+pId+"'"; 
		int rows = jdbcTemplate.update(sql);
		return rows;
	}

	@Override
	public List<TPicDesc> getThumbnailByTag(String tagId, int pageNum,
			int pageSize) {
		List<TPicDesc> resList = new ArrayList<TPicDesc>();
		String sql = "select p.p_id,p.p_publishTime "+
				"from t_picture p,t_user u,t_tag t,t_category c where u.u_id=p.p_owner and p.p_id=c.c_picture and c.c_tag=t.t_id "+
				"and t.t_id ='" + tagId + "' and p.p_pass=1";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				TPicDesc thumbnail = new TPicDesc();
				thumbnail.setTpId(map.get("p_id").toString());
				thumbnail.setThumbnailId(map.get("p_id").toString()+TPicDesc.THUMBNIAL);
				String creationTime =String.valueOf(PintuUtils.parseToDate( map.get("p_publishTime").toString()).getTime());
				thumbnail.setCreationTime(creationTime);
				thumbnail.setStatus("0");
				resList.add(thumbnail);
			}
		}
		return resList;
	}

	@Override
	public List<TPicDetails> searchByTagAnd(String[] tagArr) {
		List<TPicDetails> resList = new ArrayList<TPicDetails>();
		StringBuffer tagAnd = new StringBuffer();
		
		for(int i=0;i<tagArr.length;i++){
			if(tagAnd.length()>0){
				tagAnd.append(" and ");
			}
			tagAnd.append("p.p_id in (select c.c_picture from t_category c, t_tag t where c.c_tag = t.t_id and t.t_name ='"+tagArr[i].trim()+"')");
		}
		
		String sql = "select p.p_id,p.p_name,p.p_owner,p.p_publishTime,p.p_description,p.p_source,p.p_isOriginal,p.p_browseCount," +
				"p.p_mobImgId,p.p_mobImgSize,p.p_mobImgPath,p.p_rawImgId,p.p_rawImgSize,p.p_rawImgPath," +
				"u.u_nickName,u.u_avatar,u.u_score,u.u_level " +
				"from t_picture p left join t_user u on p.p_owner=u.u_id where p.p_pass=1 and "+tagAnd.toString();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				TPicDetails tpic = new TPicDetails();
				tpic.setId(map.get("p_id").toString());
				tpic.setName(map.get("p_name").toString());
				tpic.setOwner(map.get("p_owner").toString());
				tpic.setPublishTime(map.get("p_publishTime").toString());
				tpic.setDescription(map.get("p_description").toString());
				tpic.setSource(map.get("p_source").toString());
				tpic.setIsOriginal(Integer.parseInt(map.get("p_isOriginal")
						.toString()));
				tpic.setMobImgId(map.get("p_mobImgId").toString());
				tpic.setMobImgSize(map.get("p_mobImgSize").toString());
				tpic.setMobImgPath(map.get("p_mobImgPath").toString());
				tpic.setRawImgId(map.get("p_rawImgId").toString());
				tpic.setRawImgSize(map.get("p_rawImgSize").toString());
				tpic.setRawImgPath(map.get("p_rawImgPath").toString());
				tpic.setBrowseCount(Integer.parseInt(map.get("p_browseCount").toString()));
				tpic.setAuthor(map.get("u_nickName").toString());
				tpic.setLevel(Integer.parseInt(map.get("u_level").toString()));
				tpic.setScore(Integer.parseInt(map.get("u_score").toString()));
				tpic.setAvatarImgPath(map.get("u_avatar").toString());
				resList.add(tpic);
			}
		}
		return resList;
	}
	
	@Override
	public List<TPicDetails> searchByTagOr(String tagOr) {
		List<TPicDetails> resList = new ArrayList<TPicDetails>();
		String sql = "select distinct p.p_id,p.p_name,p.p_owner,p.p_publishTime,p.p_description,p.p_source,p.p_isOriginal,p.p_browseCount," +
				"p.p_mobImgId,p.p_mobImgSize,p.p_mobImgPath,p.p_rawImgId,p.p_rawImgSize,p.p_rawImgPath," +
				"u.u_nickName,u.u_avatar,u.u_score,u.u_level " +
				"from t_picture p,t_user u,t_tag t,t_category c where u.u_id=p.p_owner and p.p_id=c.c_picture and c.c_tag=t.t_id "+
				"and t.t_name in (" + tagOr + ") and p.p_pass=1";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				TPicDetails tpic = new TPicDetails();
				tpic.setId(map.get("p_id").toString());
				tpic.setName(map.get("p_name").toString());
				tpic.setOwner(map.get("p_owner").toString());
				tpic.setPublishTime(map.get("p_publishTime").toString());
				tpic.setDescription(map.get("p_description").toString());
				tpic.setSource(map.get("p_source").toString());
				tpic.setIsOriginal(Integer.parseInt(map.get("p_isOriginal")
						.toString()));
				tpic.setMobImgId(map.get("p_mobImgId").toString());
				tpic.setMobImgSize(map.get("p_mobImgSize").toString());
				tpic.setMobImgPath(map.get("p_mobImgPath").toString());
				tpic.setRawImgId(map.get("p_rawImgId").toString());
				tpic.setRawImgSize(map.get("p_rawImgSize").toString());
				tpic.setRawImgPath(map.get("p_rawImgPath").toString());
				tpic.setBrowseCount(Integer.parseInt(map.get("p_browseCount").toString()));
				tpic.setAuthor(map.get("u_nickName").toString());
				tpic.setLevel(Integer.parseInt(map.get("u_level").toString()));
				tpic.setScore(Integer.parseInt(map.get("u_score").toString()));
				tpic.setAvatarImgPath(map.get("u_avatar").toString());
				resList.add(tpic);
			}
		}
		return resList;
	}

	@Override
	public List<User> getPicDaren() {
		List<User> userList = new ArrayList<User>();
		String sql="select u.u_id,u.u_account,u.u_nickName,u.u_registerTime,u.u_role,u.u_level,u.u_score,u.u_exchangeScore,u.u_avatar," +
				"count(distinct p.p_id) as picNum from t_user u ,t_picture p "+
				"where u.u_id=p.p_owner and p.p_pass=1 group by u.u_id order by picNum desc limit 10";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				User user = new User();
				user.setId(map.get("u_id").toString());
				user.setAccount(map.get("u_account").toString());
				user.setNickName(map.get("u_nickName").toString());
			    user.setAvatar(map.get("u_avatar").toString());
			    user.setRegisterTime(map.get("u_registerTime").toString());
			    user.setRole(map.get("u_role").toString());
			    user.setLevel(Integer.parseInt(map.get("u_level").toString()));
			    user.setScore(Integer.parseInt(map.get("u_score").toString()));
			    user.setExchangeScore(Integer.parseInt(map.get("u_exchangeScore").toString()));
			    user.setTpicNum(Integer.parseInt(map.get("picNum").toString()));
			    userList.add(user);
			}
		}
		return userList;
	}

	@Override
	public List<User> getCmtDaren() {
		List<User> userList = new ArrayList<User>();
		String sql="select u.u_id,u.u_account,u.u_nickName,u.u_registerTime,u.u_level,u.u_role,u.u_role,u.u_score,u.u_exchangeScore,u.u_avatar," +
				"count(distinct s.s_id) as storyNum from t_user u,t_story s "+
				"where u.u_id = s.s_owner group by u.u_id order by storyNum desc limit 10";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				User user = new User();
				user.setId(map.get("u_id").toString());
				user.setAccount(map.get("u_account").toString());
				user.setNickName(map.get("u_nickName").toString());
			    user.setAvatar(map.get("u_avatar").toString());
			    user.setRegisterTime(map.get("u_registerTime").toString());
			    user.setRole(map.get("u_role").toString());
			    user.setLevel(Integer.parseInt(map.get("u_level").toString()));
			    user.setScore(Integer.parseInt(map.get("u_score").toString()));
			    user.setExchangeScore(Integer.parseInt(map.get("u_exchangeScore").toString()));
			    user.setStoryNum(Integer.parseInt(map.get("storyNum").toString()));
			    userList.add(user);
			}
		}
		return userList;
	}



	@Override
	public int getPicCoolCount(String picId) {
		String sql = "select v_amount from t_vote where v_follow ='" + picId + "'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if(rows.size() > 0){
			return Integer.parseInt(rows.get(0).get("v_amount").toString());
		}else{
			return 0;
		}
	}

	@Override
	public int getExistNickname(String nickName) {
		String sql = "select count(*) from t_user where u_nickName = '"+nickName+"'"; 
		int result = jdbcTemplate.queryForInt(sql);
		return result;
	}

	@Override
	public List<Tag> getPicTagsById(String picId) {
		List<Tag> resList = new ArrayList<Tag>();
		String sql = "select t.t_id,t.t_name,t.t_type,t.t_browseCount from t_tag t, t_category c where c.c_tag=t.t_id and c.c_picture='"+picId+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Tag tag = new Tag();
				tag.setId(map.get("t_id").toString());
				tag.setName(map.get("t_name").toString());
				tag.setType(map.get("t_type").toString());
				tag.setBrowseCount(Integer.parseInt(map.get("t_browseCount").toString()));
				resList.add(tag);
			}
		}
		return resList;
	}


	@Override
	public int updatePassword(String password, String userId) {
		String sql = "update t_user  set u_pwd ='"+password+"' where u_id='"+userId+"'";
		int rows = jdbcTemplate.update(sql); 
		return rows;
	}

	@Override
	public int updateAvatarAndNickname(final String avatarPath, final String nickName, final String userId) {
		String sql = "update t_user set u_avatar =?, u_nickName =?  where u_id =?";
		int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, avatarPath);
					ps.setString(2, nickName);
					ps.setString(3, userId);

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

		return res;
	}

	@Override
	public int confirmPassword(String userId, String md5Pwd) {
		String sql = "select count(*) from t_user where u_id='"+userId+"' and u_pwd='"+md5Pwd+"'";
		int result = jdbcTemplate.queryForInt(sql);
		return result;
	}

	@Override
	public List<TPicDesc> getRandGallery(int size) {
		List<TPicDesc> resList = new ArrayList<TPicDesc>();
		String sql ="select p_id,p_publishTime from t_picture where p_pass=1 order by rand() limit "+size;
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				TPicDesc thumbnail = new TPicDesc();
				thumbnail.setTpId(map.get("p_id").toString());
				thumbnail.setThumbnailId(map.get("p_id").toString()+TPicDesc.THUMBNIAL);
				String creationTime =String.valueOf(PintuUtils.parseToDate( map.get("p_publishTime").toString()).getTime());
				thumbnail.setCreationTime(creationTime);
				thumbnail.setStatus("0");
				resList.add(thumbnail);
			}
		}
		return resList;
	}

	@Override
	public int appendUserscoreByVote(String picId, int score) {
		String sql = "update t_user set u_score=u_score+"+score+" ,u_exchangeScore=u_exchangeScore+"+score+" where u_id = (select p_owner from t_picture where p_id='"+picId+"')";
		int result = jdbcTemplate.update(sql);
		return result;
	}

	@Override
	public List<User> getActiveUserRandking(int size) {
		List<User> userList = new ArrayList<User>();
		String sql="select * from t_user order by u_score desc limit "+size;
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				User user = new User();
				user.setId(map.get("u_id").toString());
				user.setAccount(map.get("u_account").toString());
				user.setNickName(map.get("u_nickName").toString());
			    user.setAvatar(map.get("u_avatar").toString());
			    user.setRegisterTime(map.get("u_registerTime").toString());
			    user.setRole(map.get("u_role").toString());
			    user.setLevel(Integer.parseInt(map.get("u_level").toString()));
			    user.setScore(Integer.parseInt(map.get("u_score").toString()));
			    user.setExchangeScore(Integer.parseInt(map.get("u_exchangeScore").toString()));
			    userList.add(user);
			}
		}
		return userList;
	}

	@Override
	public int reviewPictureById(String picId) {
		String sql = "update t_picture set p_pass=0 where p_id='"+picId+"'";
		int rows = jdbcTemplate.update(sql); 
		return rows;
	}

}
