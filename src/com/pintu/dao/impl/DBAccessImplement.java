package com.pintu.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.pintu.beans.Comment;
import com.pintu.beans.Favorite;
import com.pintu.beans.Message;
import com.pintu.beans.Story;
import com.pintu.beans.TPicItem;
import com.pintu.beans.User;
import com.pintu.beans.Vote;
import com.pintu.beans.Wealth;
import com.pintu.dao.DBAccessInterface;

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
	public String insertOneUser(User user) {
		final String uid = UUID.randomUUID().toString().replace("-", "")
				.substring(16);
		System.out.println("自动生成的UUID：(截取了自动生成的UUID后面16位)" + uid);
		String sql = "INSERT INTO t_user "
				+ "(u_id, u_account, u_pwd, u_avatar, u_role, u_level, u_score, u_exchangeScore,u_memo) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

		final User usr = user;

		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, uid);
					ps.setString(2, usr.getAccount());
					ps.setString(3, usr.getPwd());
					ps.setString(4, usr.getAvatar());
					ps.setString(5, usr.getRole());
					ps.setInt(6, usr.getLevel());
					ps.setInt(7, usr.getScore());
					ps.setInt(8, usr.getExchangeScore());
					ps.setString(9, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

		return uid;
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
				+ "(p_id,p_name,p_owner,p_publishTime,p_tags,p_description,p_allowStory,p_mobImgId,p_mobImgSize,p_mobImgPath,p_rawImgId,p_rawImgSize,p_rawImgPath,p_pass,p_memo) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						TPicItem picture = (TPicItem) objList.get(i);
						ps.setString(1, picture.getId());
						ps.setString(2, picture.getName());
						ps.setString(3, picture.getOwner());
						ps.setString(4, picture.getPublishTime());
						ps.setString(5, picture.getDescription());
						ps.setString(6, picture.getTags());
						ps.setInt(7, picture.getAllowStory());
						ps.setString(8, picture.getMobImgId());
						ps.setString(9, picture.getMobImgSize());
						ps.setString(10, picture.getMobImgPath());
						ps.setString(11, picture.getRawImgId());
						ps.setString(12, picture.getRawImgSize());
						ps.setString(13, picture.getRawImgPath());
						ps.setInt(14, picture.getPass());
						ps.setString(15, "");
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
				+ "' and p_publishTime <='"+endTime+"'";
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
				tpicItem.setTags(map.get("p_tags").toString());
				tpicItem.setAllowStory(Integer.parseInt(map.get("p_allowStory")
						.toString()));
				tpicItem.setMobImgId(map.get("p_mobImgId").toString());
				tpicItem.setMobImgSize(map.get("p_mobImgSize").toString());
				tpicItem.setMobImgPath(map.get("p_mobImgPath").toString());
				tpicItem.setRawImgId(map.get("p_rawImgId").toString());
				tpicItem.setRawImgSize(map.get("p_rawImgSize").toString());
				tpicItem.setRawImgPath(map.get("p_rawImgPath").toString());
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
	public List<Comment> getCommentForCache(String picIds) {
		List<Comment> resList = new ArrayList<Comment>();
//		String sql = "select * from t_comment where c_publishTime >='" + today + "'";
		String sql = "select * from t_comment where c_follow in (" + picIds + ")";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Comment comment = new Comment();
				comment.setId(map.get("c_id").toString());
				comment.setFollow(map.get("c_follow").toString());
				comment.setOwner(map.get("c_owner").toString());
				comment.setPublishTime(map.get("c_publishTime").toString());
				comment.setContent(map.get("c_content").toString());
				resList.add(comment);
			}
		}
		
		return resList;
	}

	@Override
	public List<Vote> getVoteForCache(String storyIds) {
		List<Vote> resList = new ArrayList<Vote>();
		String sql = "select * from t_vote where v_follow in (" + storyIds + ")";
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
			user.setRole(map.get("u_role").toString());
			user.setLevel(Integer.parseInt(map.get("u_level").toString()));
			user.setScore(Integer.parseInt(map.get("u_score").toString()));
			user.setExchangeScore(Integer.parseInt(map.get("u_exchangeScore").toString()));
		}
		return user;
	}

	@Override
	public int insertComment(final List<Object> objList) {
		String sql = "INSERT INTO t_comment "
				 + "(c_id,c_follow,c_owner,c_publishTime,c_content,c_memo) "
				 + "VALUES (?, ?, ?, ?, ?, ?)";

		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						Comment cmt = (Comment) objList.get(i);
						ps.setString(1, cmt.getId());
						ps.setString(2, cmt.getFollow());
						ps.setString(3, cmt.getOwner());
						ps.setString(4, cmt.getPublishTime());
						ps.setString(5, cmt.getContent());
						ps.setString(6, "");
					}

					public int getBatchSize() {
						return objList.size();
					}
				});
		return res.length;
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
	public List<Comment> getCommentsOfPic(String tpId) {
		List<Comment> cmtList = new ArrayList<Comment>();
		String sql = "select * from t_comment where c_follow = '"+tpId+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if(rows!=null && rows.size()>0){
			for(int i = 0;i<rows.size();i++){
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Comment comment = new Comment();
				comment.setId(map.get("c_id").toString());
				comment.setFollow(map.get("c_follow").toString());
				comment.setOwner(map.get("c_owner").toString());
				comment.setPublishTime(map.get("c_publishTime").toString());
				comment.setContent(map.get("c_content").toString());
				cmtList.add(comment);
			}
		}
		return cmtList;
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

		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ps.setString(1, vote.getId());
						ps.setString(2, vote.getFollow());
						ps.setString(3, vote.getType());
						ps.setInt(4, vote.getAmount());
						ps.setString(5, "");
					}

					@Override
					public int getBatchSize() {
						return 1;
					}

				});
		return res.length;
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
	public List<Vote> getVoteOfStory(String storyId) {
		List<Vote> voteList = new ArrayList<Vote>();
		String sql = "select * from t_vote where v_follow = '"+storyId+"'";
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
	public List<Vote> getVoteByFollowAndType(String storyId, String type) {
		List<Vote> voteList = new ArrayList<Vote>();
		String sql = "select * from t_vote where v_follow = '"+storyId+"' and v_type = '"+type+"'";
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
		String sql = "select * from t_picture where p_id = '"+tpId+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if(rows!=null && rows.size()>0){
			Map<String, Object> map = (Map<String, Object>) rows.get(0);
			pic.setId(map.get("p_id").toString());
			pic.setName(map.get("p_name").toString());
			pic.setOwner(map.get("p_owner").toString());
			pic.setPublishTime(map.get("p_publishTime").toString());
			pic.setTags(map.get("p_tags").toString());
			pic.setDescription(map.get("p_description").toString());
			pic.setAllowStory(Integer.parseInt(map.get("p_allowStory").toString()));
			pic.setMobImgId(map.get("p_mobImgId").toString());
			pic.setMobImgSize(map.get("p_mobImgSize").toString());
			pic.setMobImgPath(map.get("p_mobImgPath").toString());
			pic.setRawImgId(map.get("p_rawImgId").toString());
			pic.setRawImgSize(map.get("p_rawImgSize").toString());
			pic.setRawImgPath(map.get("p_rawImgPath").toString());
			pic.setPass(Integer.parseInt(map.get("p_pass").toString()));
		}
		return pic;
	}

	@Override
	public int insertMessage(final Message msg) {
		String sql = "INSERT INTO t_message "
				 + "(m_id,m_sender,m_receiver,m_content,m_writeTime,m_read,m_memo) "
				 + "VALUES (?, ?, ?, ?, ?, ?, ?)";

		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ps.setString(1, msg.getId());
						ps.setString(2, msg.getSender());
						ps.setString(3, msg.getReceiver());
						ps.setString(4, msg.getContent());
						ps.setString(5, msg.getWriteTime());
						ps.setInt(6, msg.getRead());
						ps.setString(7, "");
					}

					public int getBatchSize() {
						return 1;
					}

				});
				
			return res.length;
	}

	@Override
	public List<Message> getUserMessages(String userId) {
		List<Message> msgList = new ArrayList<Message>();
		String sql = "select * from t_message where m_sender = '"+userId+"'"+" or m_receiver = '"+userId+"' and m_read=0";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Message msg = new Message();
				msg.setId(map.get("m_id").toString());
				msg.setReceiver(map.get("m_receiver").toString());
				msg.setSender(map.get("m_sender").toString());
				msg.setWriteTime(map.get("m_writeTime").toString());
				msg.setRead(Integer.parseInt(map.get("m_read").toString()));
				msg.setContent(map.get("m_content").toString());
				msgList.add(msg);
			}
		}
		return msgList;
	}

	@Override
	public int updateMsg(final String msgId) {
		String sql = "update t_message  set m_read = 1 where m_id =?";
		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ps.setString(1, msgId);
					}

					public int getBatchSize() {
						return 1;
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
		String sql = "update t_user  set u_level = ? where u_id =?";
		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ps.setInt(1, level);
						ps.setString(2, userId);
					}

					public int getBatchSize() {
						return 1;
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
		String sql = "delete from t_wealth where w_type="+type+" and w_owner="+userId; 
		int rows = jdbcTemplate.update(sql);
		return rows;
	}
	
	@Override
	public Map<String,Integer> getOnesPicCountByTime(String startTime, String endTime) {
		String sql = "select p_owner,count(p_id) from t_picture where p_publishTime >='"
				+ startTime + "' and p_publishTime <='" + endTime + "' group by p_owner";
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
				resMap.put(map.get("u_id").toString(),Integer.parseInt(map.get("u_exchangeScore").toString()));
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

		int[] res = jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ps.setString(1, fav.getId());
						ps.setString(2, fav.getOwner());
						ps.setString(3, fav.getPicture());
						ps.setString(4, fav.getCollectTime());
						ps.setString(5, "");
					}

					@Override
					public int getBatchSize() {
						return 1;
					}

				});
		return res.length;
	}

	@Override
	public int deleteFavorite(String fId) {
		String sql = "delete from t_favorite where f_id ='"+fId+"'"; 
		int rows = jdbcTemplate.update(sql);
		return rows;
	}

	@Override
	public int checkExistFavorite(String userId, String picId) {
		String sql = "select count(*) from t_favorite where f_owner = '"+userId+"' and f_picture = '"+picId+"'";
		int rows = jdbcTemplate.queryForInt(sql);
		return rows;
	}

	@Override
	public List<TPicItem> getFavoriteTpics(String userId, int pageNum, int pageSize) {
		List<TPicItem> resList = new ArrayList<TPicItem>();
		int startLine = (pageNum -1)*pageSize;
		String sql = "select p.p_id,p.p_name,p.p_owner,p.p_publishTime,p.p_description,p.p_tags,p.p_allowStory," +
				"p.p_mobImgId,p.p_mobImgSize,p.p_mobImgPath,p.p_rawImgId,p.p_rawImgSize,p.p_rawImgPath" +
				" from t_picture p, t_favorite f where p.p_id = f.f_picture and f.f_owner = '"+userId+"' limit "+startLine+","+pageSize;
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
				tpicItem.setTags(map.get("p_tags").toString());
				tpicItem.setAllowStory(Integer.parseInt(map.get("p_allowStory")
						.toString()));
				tpicItem.setMobImgId(map.get("p_mobImgId").toString());
				tpicItem.setMobImgSize(map.get("p_mobImgSize").toString());
				tpicItem.setMobImgPath(map.get("p_mobImgPath").toString());
				tpicItem.setRawImgId(map.get("p_rawImgId").toString());
				tpicItem.setRawImgSize(map.get("p_rawImgSize").toString());
				tpicItem.setRawImgPath(map.get("p_rawImgPath").toString());
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
		String sql = "select * from t_picture where p_owner = '"+userId+"' order by p_publishTime desc limit "+startLine+","+pageSize;
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
				tpicItem.setTags(map.get("p_tags").toString());
				tpicItem.setAllowStory(Integer.parseInt(map.get("p_allowStory")
						.toString()));
				tpicItem.setMobImgId(map.get("p_mobImgId").toString());
				tpicItem.setMobImgSize(map.get("p_mobImgSize").toString());
				tpicItem.setMobImgPath(map.get("p_mobImgPath").toString());
				tpicItem.setRawImgId(map.get("p_rawImgId").toString());
				tpicItem.setRawImgSize(map.get("p_rawImgSize").toString());
				tpicItem.setRawImgPath(map.get("p_rawImgPath").toString());
				resList.add(tpicItem);
			}
		}
		return resList;
	}



	// @Override
	// public String insertOneEvent(Event event) {
	// final String eid = UUID.randomUUID().toString().replace("-", "")
	// .substring(16);
	// System.out.println("自动生成的UUID：(截取了自动生成的UUID后面16位)" + eid);
	// String sql = "INSERT INTO t_comment "
	// + "(e_id,e_title,e_detail,e_eventTime,e_memo) "
	// + "VALUES (?, ?, ?, ?, ?)";
	// return eid;
	// }
	//
	// @Override
	// public String insertOneMessage(Message message) {
	// final String mid = UUID.randomUUID().toString().replace("-", "")
	// .substring(16);
	// System.out.println("自动生成的UUID：(截取了自动生成的UUID后面16位)" + mid);
	// String sql = "INSERT INTO t_message "
	// + "(m_id,m_sender,m_receiver,m_content,m_wir,m_memo) ";
	//
	// return mid;
	// }
	//
	// @Override
	// public String insertOneFavorite(Favorite favorite) {
	// final String fid = UUID.randomUUID().toString().replace("-", "")
	// .substring(16);
	// System.out.println("自动生成的UUID：(截取了自动生成的UUID后面16位)" + fid);
	// String sql = "INSERT INTO t_favorite "
	// + "(f_id,f_owner,f_picture,f_collectTime,f_memo) "
	// + "VALUES (?, ?, ?, ?, ?)";
	//
	// return fid;
	// }
	// @Override
	// public String insertOneGift(Gift gift) {
	// final String gid = UUID.randomUUID().toString().replace("-", "")
	// .substring(16);
	// System.out.println("自动生成的UUID：(截取了自动生成的UUID后面16位)" + gid);
	// String sql = "INSERT INTO t_gift "
	// + "(g_id,g_name,g_type,g_value,g_imgPath,g_amount,g_memo) "
	// + "VALUES (?, ?, ?, ?, ?, ?, ?)";
	//
	// return gid;
	// }
}
