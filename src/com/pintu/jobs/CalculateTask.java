package com.pintu.jobs;

/**
 * 计算积分、等级、经典品图、资产
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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

	private Logger log = Logger.getLogger(CalculateTask.class);

	public CalculateTask(DBAccessInterface dbVisitor,
			CacheAccessInterface cacheVisitor, Properties propertyConfigurer) {
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
		// 这里存毫秒数
		Long start = System.currentTimeMillis() - 60 * 60 * 1000;
	    Long end = System.currentTimeMillis();

		// 取一小时内被操作的故事，并观察投票数，若start类的值超过一个限值，则将故事表中经典字段
		findAndSetClassical(start, end);
		
		// 取得活跃用户或缺省用户的信息
		ArrayList<User> userList = getLiveOrDefaultUser(start,end);

		// 计算活跃用用户的积分并更新数据库
		List<User> updateScoreUserList = new ArrayList<User>();
		if(userList.size() >0){
			updateScoreUserList = this.calAndUpdateScore(userList, start, end);
		}

		// 财产直接根据可用积分计算结果查找并更新数据库的wealth表
		if(updateScoreUserList.size() >0){
			calAndUpdateWealth(updateScoreUserList);
		}
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
											.getProperty("classicalVoteNum"))) {
						
						//在这里需要判断一下，若数据库里故事已为经典就不放到更新里去
						String storyId =vote.getFollow();
						if(!this.dbAccess.isClassicalStory(storyId)){
							needUpdateStoryIds.add(storyId);
						}
					}
				}
			}
			if(needUpdateStoryIds.size() > 0){
				// 更新数据库中的经典字段
				int res = this.dbAccess.updateStoryClassical(needUpdateStoryIds);
				if (res == needUpdateStoryIds.size()) {
					log.info("更新故事经典字段成功！");
				} else {
					log.info("更新故事经典字段有误！");
				}
			}
		}

	}

	private ArrayList<User> getLiveOrDefaultUser(Long start, Long end) {
		// 需要进行计算的活跃用户
		ArrayList<User> userList = new ArrayList<User>();
		List<User> liveList = new ArrayList<User>();
		
		for (Long i = start / (60 * 1000); i <= end / (60 * 1000); i++) {
			liveList = this.cacheAccess.getLiveUser(String.valueOf(i));
			if (liveList.size() > 0) {
				for (int j = 0; j < liveList.size(); j++) {
					User user = liveList.get(j);
					userList.add(user);
				}
			}
		}
		
		//FIXME 这里后期加入登录可缓存用户了后即可删除
		if (liveList.size() == 0) {
			// 缺省用户,这里的id是我本身数据库里存在的一个用户id(必须保证存在)
			User user = this.dbAccess.getUserById("a053beae20125b5b");
			userList.add(user);
		}
		
		return userList;
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

		// 已计算后的用户列表来更新完数据库
		if (resultList.size() > 0) {
			int m = this.dbAccess.updateUserScore(resultList);
			if (m == resultList.size()) {
				log.info("更新数据库用户积分成功！");
			} else {
				log.info("更新数据库用户积分失败！");
			}
		}

		return resultList;
	}


	private void calAndUpdateWealth(List<User> list) {
		StringBuffer userIds = new StringBuffer();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				User user = list.get(i);
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
				.getUserExchangeInfo(userIds.toString());

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

				// 换算结束后先更新用户的剩余可用积分字段，下面再做wealth表的操作
				if (typeAmountMap.containsKey(Wealth.REMAIN_SCORE)) {
					int row = this.dbAccess.updateUserExchageScore(userId,
							typeAmountMap.get(Wealth.REMAIN_SCORE));
					if (row == 1) {
						typeAmountMap.remove(Wealth.REMAIN_SCORE);
						log.info("更新用户可用积分字段成功！");
					}
				}

				// 取出数据库wealth表中的相应用户的记录
				List<Wealth> oldWealthList = this.dbAccess
						.getUsersWealthInfo(userId);
				if (oldWealthList.size() > 0) {
					// 有财富，需要合并升集，累加后判断更新相应记录也有删除某些数量为0的记录
					// 合并、更新、删除财富
					unionAndUpdateWealth(typeAmountMap, oldWealthList, userId);

				} else {
					// 数据库wealth表里有的类型记录更新，不存在的新类型则直接插入
					insertWealth(typeAmountMap, userId);
				}
			}
		}
	}

	private void unionAndUpdateWealth(Map<String, Integer> typeAmountMap,
			List<Wealth> oldWealthList, String userId) {

		// 存储数据库中已存在的财富类型，用来匹配哪些是要更新的，其他是来插入的
		Set<String> dbTypeSet = new HashSet<String>();
		// 将数据库中已存在的类型与新计算产生的类型取并集
		Map<String, Integer> unionMap = new HashMap<String, Integer>();

		Map<String, Integer> updateMap = new HashMap<String, Integer>();
		Map<String, Integer> insertMap = new HashMap<String, Integer>();

		for (int i = 0; i < oldWealthList.size(); i++) {
			Wealth wealth = oldWealthList.get(i);
			String type = wealth.getType();
			dbTypeSet.add(type);
			if (typeAmountMap.containsKey(type)) {
				Integer newCount = typeAmountMap.get(type) + wealth.getAmount();
				unionMap.remove(type);
				unionMap.put(type, newCount);
			} else {
				unionMap.put(type, typeAmountMap.get(type));
			}
		}

		// 这里需要判断一下unionMap,升级
		Map<String, Integer> resultMap = reCalAndUpgrade(unionMap);

		// 需要注意这里，合并unionMap应与数据库里的部分重新做一下比较，若有赋值updateMap，否则add到insertMap
		for (String type : resultMap.keySet()) {
			if (dbTypeSet.contains(type)) {
				updateMap.put(type, resultMap.get(type));
			} else {
				insertMap.put(type, resultMap.get(type));
			}
		}

		updateWealth(updateMap, userId);
		insertWealth(insertMap, userId);

	}

	// 计算并升级(这里因传入的参数为确定的四个级别，要按等级从低到高计算)
	private Map<String, Integer> reCalAndUpgrade(Map<String, Integer> map) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		Map<String, Integer> upGrade = new HashMap<String, Integer>();

		String[] gradeArray = new String[map.size()];
		gradeArray[0] = Wealth.ONE_YUAN;
		gradeArray[1] = Wealth.TEN_YUAN;
		gradeArray[2] = Wealth.FIFTY_YUAN;
		gradeArray[3] = Wealth.HUNDRED_YUAN;

		for (String str : gradeArray) {
			Integer index = map.get(str);
			Integer upGradeCount = new Integer(0);
			Integer localGradeCount = new Integer(0);
			if (Wealth.ONE_YUAN.equals(str)) {
				upGradeCount = index / 10;
				localGradeCount = index % 10;
				upGrade.put(Wealth.TEN_YUAN, upGradeCount);
				result.put(Wealth.ONE_YUAN, localGradeCount);
			} else if (Wealth.TEN_YUAN.equals(str)) {
				Integer upGradeCount4me = upGrade.get(Wealth.TEN_YUAN);
				index += upGradeCount4me;
				upGradeCount = index / 5;
				localGradeCount = index % 5;
				upGrade.put(Wealth.FIFTY_YUAN, upGradeCount);
				result.put(Wealth.TEN_YUAN, localGradeCount);
			} else if (Wealth.FIFTY_YUAN.equals(str)) {
				Integer upGradeCount4me = upGrade.get(Wealth.FIFTY_YUAN);
				index += upGradeCount4me;
				upGradeCount = index / 2;
				localGradeCount = index % 2;
				upGrade.put(Wealth.HUNDRED_YUAN, upGradeCount);
				result.put(Wealth.FIFTY_YUAN, localGradeCount);
			} else if (Wealth.HUNDRED_YUAN.equals(str)) {
				Integer upGradeCount4me = upGrade.get(Wealth.HUNDRED_YUAN);
				index += upGradeCount4me;
				result.put(Wealth.HUNDRED_YUAN, index);
			}
		}

		return result;
	}

	private void updateWealth(Map<String, Integer> map, String userId) {
		List<Wealth> updateList = new ArrayList<Wealth>();
		for (String type : map.keySet()) {
			if (map.get(type) != 0) {
				Wealth w = new Wealth();
				w.setOwner(userId);
				w.setType(type);
				w.setAmount(map.get(type));
				updateList.add(w);
			} else {
				// 若某一类型的财富值变成0，则删除该条记录
				this.dbAccess.deleteOnesWealth(type, userId);
			}
		}
		
		if(updateList.size() > 0){
			int row = this.dbAccess.updateOnesWealth(updateList);
			if (row == updateList.size()) {
				log.info("更新用户的财产信息成功！");
			}
		}
	}

	private void insertWealth(Map<String, Integer> typeAmountMap, String userId) {
		List<Wealth> wealthList = new ArrayList<Wealth>();
		for (String type : typeAmountMap.keySet()) {
			// 判断若财富种类不为零则为该用户插入
			if (typeAmountMap.get(type) != 0) {
				Wealth w = new Wealth();
				w.setId(PintuUtils.generateUID());
				w.setOwner(userId);
				w.setType(type);
				w.setAmount(typeAmountMap.get(type));
				wealthList.add(w);
			}
		}
		
		if(wealthList.size() > 0){
			int row = this.dbAccess.insertOnesWealth(wealthList);
			if (row == wealthList.size()) {
				log.info("插入用户的财产信息成功！");
			}
		}

	}

	// 将积分转化为财富的类型与数量map
	private Map<String, Integer> conversion(int exchangeScore) {
		// 这里包含四个是类型数量，第五个为剩余积分（先初始化，再在递归计算的过程中为其分别赋值）
		Map<String, Integer> typeAmount = new HashMap<String, Integer>();
		typeAmount.put(Wealth.ONE_YUAN, 0);
		typeAmount.put(Wealth.TEN_YUAN, 0);
		typeAmount.put(Wealth.FIFTY_YUAN, 0);
		typeAmount.put(Wealth.HUNDRED_YUAN, 0);
		typeAmount.put(Wealth.REMAIN_SCORE, 0);

		// 这里用一个递归来实现
		 calculateScore(exchangeScore,typeAmount);

		return typeAmount;
	}
	
	private void calculateScore(int score,Map<String,Integer> map ){
		//600
		int gold = Integer.parseInt(propertyConfigurer.getProperty("goldShell"));
		//300
		int silver = Integer.parseInt(propertyConfigurer.getProperty("silverShell"));
		//60
		int copper = Integer.parseInt(propertyConfigurer.getProperty("copperShell"));
		//6
		int sea = Integer.parseInt(propertyConfigurer.getProperty("seaShell"));
		
		if(score >= gold){
			int value = map.get(Wealth.HUNDRED_YUAN);
			map.put(Wealth.HUNDRED_YUAN, value+1);
			score -= gold;
			calculateScore(score,map);
		}else if(score >= silver){
			int value = map.get(Wealth.FIFTY_YUAN);
			map.put(Wealth.FIFTY_YUAN, value+1);
			score -= silver;
			calculateScore(score,map);
		}else if(score >= copper){
			int value = map.get(Wealth.TEN_YUAN);
			map.put(Wealth.TEN_YUAN, value+1);
			score -= copper;
			calculateScore(score,map);
		}else if(score >= sea){
			int value = map.get(Wealth.ONE_YUAN);
			map.put(Wealth.ONE_YUAN, value+1);
			score -= sea;
			calculateScore(score,map);
		}else {
			map.put(Wealth.REMAIN_SCORE, score);	
		}
	}

}
