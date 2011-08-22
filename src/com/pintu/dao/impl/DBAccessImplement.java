package com.pintu.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.pintu.beans.Comment;
import com.pintu.beans.Story;
import com.pintu.beans.TPicItem;
import com.pintu.beans.User;
import com.pintu.beans.Vote;
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
	public List<String> getPicIdsByTime(String startTime, String endTime) {
		String sql = "select p_id from t_picture where p_publishTime >='"
				+ startTime + "' and p_publishTime <='" + endTime + "'";
		System.out.println(sql);
		List<String> idList = new ArrayList<String>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if(rows!=null && rows.size()>0){
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				idList.add(map.get("p_id").toString());
			}
		}
		return idList;
	}

	@Override
	public List<TPicItem> getPictureForCache(String today) {
		List<TPicItem> resList = new ArrayList<TPicItem>();
		String sql = "select * from t_picture where p_publishTime >='" + today
				+ "'";
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
	public List<Story> getStoryForCache(String today) {
		List<Story> resList = new ArrayList<Story>();
		
		String sql = "select * from t_story where s_publishTime >='" + today + "'";
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
	public List<Comment> getCommentForCache(String today) {
		List<Comment> resList = new ArrayList<Comment>();
		
		String sql = "select * from t_comment where c_publishTime >='" + today + "'";
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
	//
	// @Override
	// public String insertOneWealth(Wealth wealth) {
	// final String wid = UUID.randomUUID().toString().replace("-", "")
	// .substring(16);
	// System.out.println("自动生成的UUID：(截取了自动生成的UUID后面16位)" + wid);
	// String sql = "INSERT INTO t_wealth "
	// + "(w_id,w_owner,w_type,w_amount,w_memo) ";
	//
	// return wid;
	// }
	//
	// @Override
	// public String updateOneWealth(String id, Wealth wealth) {
	// String sql = "update t_wealth set type=?, amount=?, where owner=?";
	// final Wealth wea = wealth;
	// jdbcTemplate.update(sql, new PreparedStatementSetter() {
	// public void setValues(PreparedStatement ps) {
	//
	// try {
	// ps.setString(1, wea.getType());
	// ps.setInt(2, wea.getAmount());
	// ps.setString(3, wea.getOwner());
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// return null;
	// }
	//
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

}
