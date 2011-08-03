package com.pintu.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.pintu.beans.Comment;
import com.pintu.beans.Event;
import com.pintu.beans.Favorite;
import com.pintu.beans.Gift;
import com.pintu.beans.Message;
import com.pintu.beans.Story;
import com.pintu.beans.TPicItem;
import com.pintu.beans.TastePic;
import com.pintu.beans.User;
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
	public String insertPicture(final List<Object> objList) {
		String sql = "INSERT INTO t_picture "
				+ "(p_id,p_name,p_owner,p_publishTime,p_tags,p_description,p_allowStory,p_mobImgId,p_mobImgSize,p_mobImgPath,p_rawImgId,p_rawImgSize,p_rawImgPath,p_pass,p_memo) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				TPicItem picture = (TPicItem) objList.get(i);
				ps.setString(1, picture.getId());
				ps.setString(2, picture.getName());
				ps.setString(3, picture.getOwner());
				ps.setDate(4, picture.getPublishTime());
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

		return objList.size() + "";
	}

	@Override
	public String insertOneStory(Story story) {
		final String sid = UUID.randomUUID().toString().replace("-", "")
				.substring(16);
		System.out.println("自动生成的UUID：(截取了自动生成的UUID后面16位)" + sid);
		String sql = "INSERT INTO t_story "
				+ "(s_id,s_follow,s_owner,s_publishTime,s_content,s_classical,s_memo) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		final Story stor = story;

		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, sid);
					ps.setString(2, stor.getFollow());
					ps.setString(3, stor.getOwner());
					ps.setString(4, stor.getPublishTime() + "");
					ps.setString(5, stor.getContent());
					ps.setInt(6, stor.getClassical());
					ps.setString(7, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		return sid;
	}

	@Override
	public String insertOneComment(Comment comment) {
		final String cid = UUID.randomUUID().toString().replace("-", "")
				.substring(16);
		System.out.println("自动生成的UUID：(截取了自动生成的UUID后面16位)" + cid);
		String sql = "INSERT INTO t_comment "
				+ "(c_id,c_follow,c_owner,c_publishTime,c_content,c_memo) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		final Comment comm = comment;

		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, cid);
					ps.setString(2, comm.getFollow());
					ps.setString(3, comm.getOwner());
					ps.setString(4, comm.getPublishTime() + "");
					ps.setString(5, comm.getContent());
					ps.setString(6, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		return cid;
	}

	@Override
	public String insertOneGift(Gift gift) {
		final String gid = UUID.randomUUID().toString().replace("-", "")
				.substring(16);
		System.out.println("自动生成的UUID：(截取了自动生成的UUID后面16位)" + gid);
		String sql = "INSERT INTO t_gift "
				+ "(g_id,g_name,g_type,g_value,g_imgPath,g_amount,g_memo) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		final Gift gft = gift;

		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, gid);
					ps.setString(2, gft.getName());
					ps.setString(3, gft.getType());
					ps.setInt(4, gft.getValue());
					ps.setString(5, gft.getImgPath());
					ps.setInt(6, gft.getAmount());
					ps.setString(7, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		return gid;
	}

	@Override
	public String insertOneWealth(Wealth wealth) {
		final String wid = UUID.randomUUID().toString().replace("-", "")
				.substring(16);
		System.out.println("自动生成的UUID：(截取了自动生成的UUID后面16位)" + wid);
		String sql = "INSERT INTO t_wealth "
				+ "(w_id,w_owner,w_type,w_amount,w_memo) "
				+ "VALUES (?, ?, ?, ?, ?)";

		final Wealth weal = wealth;

		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, wid);
					ps.setString(2, weal.getOwner());
					ps.setString(3, weal.getType());
					ps.setInt(4, weal.getAmount());
					ps.setString(5, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		return wid;
	}

	@Override
	public String updateOneWealth(String id, Wealth wealth) {
		String sql = "update t_wealth set type=?, amount=?, where owner=?";
		final Wealth wea = wealth;
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {

				try {
					ps.setString(1, wea.getType());
					ps.setInt(2, wea.getAmount());
					ps.setString(3, wea.getOwner());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return null;
	}

	@Override
	public String insertOneEvent(Event event) {
		final String eid = UUID.randomUUID().toString().replace("-", "")
				.substring(16);
		System.out.println("自动生成的UUID：(截取了自动生成的UUID后面16位)" + eid);
		String sql = "INSERT INTO t_comment "
				+ "(e_id,e_title,e_detail,e_eventTime,e_memo) "
				+ "VALUES (?, ?, ?, ?, ?)";

		final Event eve = event;

		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, eid);
					ps.setString(2, eve.getTitle());
					ps.setString(3, eve.getDetail());
					ps.setString(4, eve.getEventTime() + "");
					ps.setString(5, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		return eid;
	}

	@Override
	public String insertOneMessage(Message message) {
		final String mid = UUID.randomUUID().toString().replace("-", "")
				.substring(16);
		System.out.println("自动生成的UUID：(截取了自动生成的UUID后面16位)" + mid);
		String sql = "INSERT INTO t_message "
				+ "(m_id,m_sender,m_receiver,m_content,m_wir,m_memo) "
				+ "VALUES (?, ?, ?, ?, ?)";

		final Message msg = message;

		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, mid);
					ps.setString(2, msg.getSender());
					ps.setString(3, msg.getReceiver());
					ps.setString(4, msg.getContent());
					ps.setString(5, msg.getWriteTime());
					ps.setInt(6, msg.getRead());
					ps.setString(7, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		return mid;
	}

	@Override
	public String insertOneFavorite(Favorite favorite) {
		final String fid = UUID.randomUUID().toString().replace("-", "")
				.substring(16);
		System.out.println("自动生成的UUID：(截取了自动生成的UUID后面16位)" + fid);
		String sql = "INSERT INTO t_favorite "
				+ "(f_id,f_owner,f_picture,f_collectTime,f_memo) "
				+ "VALUES (?, ?, ?, ?, ?)";

		final Favorite favor = favorite;

		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, fid);
					ps.setString(2, favor.getOwner());
					ps.setString(3, favor.getPicture());
					ps.setString(4, favor.getCollectTime() + "");
					ps.setString(5, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		return fid;
	}

}
