package com.pintu.jobs;

/**
 * 计算积分、等级、经典品图、资产
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.pintu.beans.User;
import com.pintu.beans.Vote;
import com.pintu.beans.Wealth;
import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;
import com.pintu.utils.PintuUtils;

public class CalculateTask extends TimerTask {

	private DBAccessInterface dbAccess;
	private CacheAccessInterface cacheAccess;

	private Properties propertyConfigurer;
	
	// 这里存毫秒数
	private Long start= System.currentTimeMillis()-60*60*1000;
	private Long end=System.currentTimeMillis();

	private Logger log = Logger.getLogger(CalculateTask.class);

	public CalculateTask(DBAccessInterface dbVisitor,
			CacheAccessInterface cacheVisitor,Properties propertyConfigurer) {
		this.dbAccess = dbVisitor;
		this.cacheAccess = cacheVisitor;
		this.propertyConfigurer = propertyConfigurer;
	}

	@Override
	public void run() {
		calculate();
	}

	private void calculate() {
		System.out.println(">>> calculate task executed...");
		// 需要进行计算的活跃用户
		ArrayList<User> userList = new ArrayList<User>();
		for (Long i = this.start / (60 * 1000); i <= this.end / (60 * 1000); i++) {
			List<User> liveList = this.cacheAccess.getLiveUser(String
					.valueOf(i));
			if (liveList.size() > 0) {
				for (int j = 0; j < liveList.size(); j++) {
					User user = liveList.get(j);
					userList.add(user);
				}
			}
		}

		// 已计算积分的用户列表
		List<User> resultList = this.calAndUpdateScore(userList, this.start,
				this.end);

		// 已计算后的用户列表来更新完数据库
		if (resultList.size() > 0) {
			int m = this.dbAccess.updateUserScore(resultList);
			if (m == resultList.size()) {
				log.info("更新数据库用户积分成功");
			} else {
				log.info("更新数据库用户积分失败");
			}
		}

		// 取一小时内被操作的故事，并观察投票数，若start类的值超过一个限值，则将故事表中经典字段
		findAndSetClassical(start, end);

		// 财产直接根据可用积分计算查找并更新数据库的wealth表
		calAndUpdateWealth(resultList);

	}

	// 计算积分
	private List<User> calAndUpdateScore(List<User> userList, Long start,
			Long end) {
		// 将毫秒数转换成数据库所存储的字符串格式"yyyy-MM-dd HH:mm:ss"
		String startTime = PintuUtils.formatLong(start);
		String endTime = PintuUtils.formatLong(end);
		// 用于存储有积分变化的活动用户
		List<User> resultList = new ArrayList<User>();

		if (userList.size() > 0) {
			// 1、从数据库中取出<userId,发图个数>的统计结果
			Map<String, Integer> picMap = this.dbAccess.getOnesPicCountByTime(
					startTime, endTime);
			// 2、<userId,发故事个数>
			Map<String, Integer> storyMap = this.dbAccess
					.getOnesStoryCountByTime(startTime, endTime);

			for (int i = 0; i < userList.size(); i++) {
				User user = userList.get(i);
				String userId = user.getId();
				// 若某用户同时有插入图片和写故事的操作
				if (picMap.containsKey(userId) && storyMap.containsKey(userId)) {
					user.setScore(picMap.get(userId)
							* Integer.parseInt(propertyConfigurer
									.getProperty("uploadPictureScore"))
							+ picMap.get(userId)
							* Integer.parseInt(propertyConfigurer
									.getProperty("tellStoryScore")));

					user.setExchangeScore(user.getScore());
					resultList.add(user);
					// 只有发图片操作
				} else if (picMap.containsKey(userId)) {
					user.setScore(picMap.get(userId)
							* Integer.parseInt(propertyConfigurer
									.getProperty("uploadPictureScore")));

					user.setExchangeScore(user.getScore());
					resultList.add(user);
					// 只有写故事操作
				} else if (storyMap.containsKey(userId)) {
					user.setScore(picMap.get(userId)
							* Integer.parseInt(propertyConfigurer
									.getProperty("tellStoryScore")));
					user.setExchangeScore(user.getScore());
					resultList.add(user);
				}
			}
		}
		return resultList;
	}

	private void findAndSetClassical(Long start, Long end) {
		// 将毫秒数转换成数据库所存储的字符串格式"yyyy-MM-dd HH:mm:ss"
		String startTime = PintuUtils.formatLong(start);
		String endTime = PintuUtils.formatLong(end);
		// 取到一段时间间隔内有所有故事的id集
		List<String> storyIdList = this.dbAccess.getStoryIdsByTime(startTime,
				endTime);
		// 将storyIdList转化成后面的形式--('','','')
		if (storyIdList.size() > 0) {
			StringBuffer storyIds = new StringBuffer();
			for (int i = 0; i < storyIdList.size(); i++) {
				String storyId = storyIdList.get(i);
				if (storyIds.length() > 0) {
					storyIds.append(",");
				}
				storyIds.append("'");
				storyIds.append(storyId);
				storyIds.append("'");
			}
			// 根据故事id取得投票信息
			List<Vote> voteList = this.dbAccess.getVoteForCache(storyIds
					.toString());
			// 存储需要更新classical字段的故事id
			List<String> needUpdateStoryIds = new ArrayList<String>();
			if (voteList.size() > 0) {
				for (int i = 0; i < voteList.size(); i++) {
					Vote vote = voteList.get(i);
					// 查看并判断经典投票的数量
					if (vote.getType().equals(Vote.STAR_TYPE)
							&& vote.getAmount() > Integer
									.parseInt(propertyConfigurer
											.getProperty("classicaVoteNum"))) {
						needUpdateStoryIds.add(vote.getFollow());
					}
				}
			}
			// 更新数据库中的经典字段
			int res = this.dbAccess.updateStoryClassical(needUpdateStoryIds);
			if (res == needUpdateStoryIds.size()) {
				log.info("更新故事经典字段成功！");
			} else {
				log.info("更新故事经典字段有误！");
			}
		}

	}

	private void calAndUpdateWealth(List<User> storyIdList) {
		StringBuffer userIds = new StringBuffer();
		if (storyIdList.size() > 0) {
			for (int i = 0; i < storyIdList.size(); i++) {
				User user = storyIdList.get(i);
				if (userIds.length() > 0) {
					userIds.append(",");
				}
				userIds.append("'");
				userIds.append(user.getId());
				userIds.append("'");
			}
		}
			// 1、 获取活动用户的可用积分信息
			Map<String, Integer> idScoreMap = this.dbAccess
					.getUserExchangeInfo(userIds);

			// 2、为用户计算财产，以可用积分换贝壳，
			// 3、从wealth表中取出要计算的用户的财产信息
			// 为该用户将相同类型的贝类累加，得到一个结果集用来重新更新wealth，
			// 比较判断，数据库wealth表里有的类型记录更新，不存在的新类型则直接插入
			// 若数据库表里有的类型记录与新的同类记录合并后升到上一级此类型个数变0，则删除
			if (idScoreMap.size() > 0) {
				for (String userId : idScoreMap.keySet()) {
					Integer exchangeScore = idScoreMap.get(userId);
					// 换算
					Map<String, Integer> typeAmountMap = conversion(exchangeScore);
					//取出数据库wealth表中的相应用户的记录
					List<Wealth> oldWealthList = this.dbAccess.getUsersWealthInfo(userId);
					if (oldWealthList.size() > 0) {
						// 有财富，需要合并升集，累加后判断更新相应记录也有删除某些数量为0的记录
						//合并、更新、删除财富;更新剩余可用积分
						
					} else {
						// 数据库wealth表里有的类型记录更新，不存在的新类型则直接插入
						insertWealthAndUpdateExScore(typeAmountMap, userId);
					}
				}
			}

	}

	private void insertWealthAndUpdateExScore(
			Map<String, Integer> typeAmountMap, String userId) {
		List<Wealth> wealthList = new ArrayList<Wealth>();
		for (String type : typeAmountMap.keySet()) {
			if (type == Wealth.REMAIN_SCORE) {
				int row = this.dbAccess.updateUserExchageScore(userId,
						typeAmountMap.get(type));
				if (row == 1) {
					log.info("更新用户可用积分字段成功！");
				}
			} else {
				// 判断若财富种类不为零则为该用户插入
				if (typeAmountMap.get(type) != 0) {
					Wealth w = new Wealth();
					w.setId(PintuUtils.generateUID());
					w.setOwner(userId);
					w.setType(type);
					w.setAmount(typeAmountMap.get(type));
					wealthList.add(w);
				}
				int row = this.dbAccess.insertOnesWealth(wealthList);
				if (row == wealthList.size()) {
					log.info("插入用户的财产信息成功！");
				}
			}
		}
	}

	private Map<String, Integer> conversion(Integer exchangeScore) {
		// 这里包含四个是类型数量，第五个为剩余积分（先初始化，再在递归计算的过程中为其分别赋值）
		Map<String, Integer> typeAmount = new HashMap<String, Integer>();
		typeAmount.put(Wealth.SEA_TYPE, 0);
		typeAmount.put(Wealth.COPPER_TYPE, 0);
		typeAmount.put(Wealth.SILVER_TYPE, 0);
		typeAmount.put(Wealth.GOLD_TYPE, 0);
		typeAmount.put(Wealth.REMAIN_SCORE, 0);

		// 这里考虑用一个递归来写

		return null;
	}

}
