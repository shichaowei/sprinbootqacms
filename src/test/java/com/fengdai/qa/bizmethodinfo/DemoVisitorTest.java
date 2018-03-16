package com.fengdai.qa.bizmethodinfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.tools.ant.DirectoryScanner;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fengdai.qa.dao.admin.FengdaiMethodInfoDao;
import com.fengdai.qa.meta.MethodInfo;
import com.fengdai.qa.utils.bizmethod.DemoVisitor;
import com.fengdai.qa.utils.bizmethod.JdtAstUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoVisitorTest {

	private static final Logger logger = LoggerFactory.getLogger(DemoVisitorTest.class);
	@Autowired
	private FengdaiMethodInfoDao fengdaiMethodInfoDao;

	public List<MethodInfo> getMethodinfo(String path) {
		CompilationUnit comp = JdtAstUtil.getCompilationUnit(path);
		List<MethodInfo> methodinfos = new ArrayList<>();
		DemoVisitor visitor = new DemoVisitor(methodinfos, path);
		comp.accept(visitor);
		return methodinfos;
	}

	public static ArrayList<String> scanFiles(String classesDir) {

		String classDir = classesDir;
		File file = new File(classDir);
		DirectoryScanner scanner = new DirectoryScanner();
		String[] includes = { "**//*.java" };
		scanner.setIncludes(includes);
		scanner.setBasedir(file);
		scanner.setCaseSensitive(true);
		scanner.scan();
		String[] files = scanner.getIncludedFiles();
		ArrayList<String> result = new ArrayList<>();
		for (int ioe = 0; ioe < files.length; ioe++) {

			if (!files[ioe].contains("src/test/java") && !files[ioe].contains("src\\test\\java")) {
				files[ioe] = file.getPath() + file.separatorChar + files[ioe];
				result.add(files[ioe]);
			}
		}
		return result;
	}

	@Test
	public void testInsertMehodInfo() {

		logger.info("start");
		fengdaiMethodInfoDao.deleteAllMethodInfo();
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(8);

		for (String var : scanFiles(
				"D:\\51\\fd-server\\services")) {
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					List<MethodInfo> methodInfos = new DemoVisitorTest().getMethodinfo(var);
					for (MethodInfo methodInfo : methodInfos) {
//						System.out.println("methodinfo is :" + methodInfo.toString());
						fengdaiMethodInfoDao.addMethodInfo(methodInfo);
					}
				}
			});

		}
		fixedThreadPool.shutdown();
		while (true) {
			if (fixedThreadPool.isTerminated()) {
				break;
			}
		}
		logger.info("stop");
	}

	@Test
	public void testDifferMehodInfo() throws Exception {
		fengdaiMethodInfoDao.deleteHandledMethodInfo();
		ArrayList<MethodInfo> modifyMethods = new ArrayList<>();
		ArrayList<MethodInfo> addMethods = new ArrayList<>();
		ArrayList<MethodInfo> deletedMethods = new ArrayList<>();
		List<MethodInfo> badMethodinfolisthodinfolist = new ArrayList<>();
		List<MethodInfo> newMethodinfolist = new ArrayList<>();

		System.out.println("start:" + new java.util.Date());
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(8);
		for (String var : scanFiles(
				"D:\\51\\fd-server\\services")) {
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					// System.out.println(new File(var).lastModified()); // System.out.println(var);
					List<MethodInfo> methodInfos = new DemoVisitorTest().getMethodinfo(var);

					for (MethodInfo methodInfo : methodInfos) {
						newMethodinfolist.add(methodInfo);
							if (methodInfo.getMethodbody() != null) {
								try {
									MethodInfo oldmethodinfo = fengdaiMethodInfoDao.getMethodInfo(methodInfo);
									if (null != oldmethodinfo&& oldmethodinfo.getMethodbody().equals(methodInfo.getMethodbody())) {
										oldmethodinfo.setFlag("handled");
										fengdaiMethodInfoDao.updateMehodFlag(oldmethodinfo);
										// System.out.println("没有变更过的代码" + oldmethodinfo.toString());
									} else if (null == oldmethodinfo) {
										methodInfo.setFlag("add");
										addMethods.add(methodInfo);
										fengdaiMethodInfoDao.addMethodInfo(methodInfo);
										// System.out.println("新增的代码:"+methodInfo);
									} else {
										methodInfo.setFlag("changed");
										modifyMethods.add(methodInfo);
										fengdaiMethodInfoDao.addMethodInfo(methodInfo);
										oldmethodinfo.setFlag("handled");
										fengdaiMethodInfoDao.updateMehodFlag(oldmethodinfo);
										// System.err.println("change的代码" + oldmethodinfo);
									}
								} // 此处特别坑，不能使用toomanyexceptiong 内嵌的exception捕获不了
								catch (MyBatisSystemException e) {
									// TODO Auto-generated catch block
									// e.printStackTrace();
									// System.err.println("查询入参:" + methodInfo.toString());
									methodInfo.setFlag("bad");
									// 不使用update 因为old代码不一定有这个类方法
									fengdaiMethodInfoDao.addMethodInfo(methodInfo);
									badMethodinfolisthodinfolist.add(methodInfo);
									for(MethodInfo oldmethodinfo: fengdaiMethodInfoDao.getMethodInfos(methodInfo)) {
										oldmethodinfo.setFlag("handled");
										fengdaiMethodInfoDao.updateMehodFlag(oldmethodinfo);
									}
								} catch (Exception e) {
									e.printStackTrace();
									System.err.println("wrong:"+methodInfo);
								}

							}

					}

				}
			});
		}
		fixedThreadPool.shutdown();
		while (true) {
			if (fixedThreadPool.isTerminated()) {
				break;
			}
		}
		// 删除的类方法
		List<MethodInfo> oldmethodinfos = fengdaiMethodInfoDao.getAllNormalMethodInfo();
		deletedMethods.addAll(oldmethodinfos);
		/*for (MethodInfo old : oldmethodinfos) {

			Boolean flag = true;
			for (MethodInfo newmethod : newMethodinfolist) {
					// if(newmethod.getMethodname().equals("getValueByFactorCode")) {
					// System.out.println("delete check old:"+old.toString());
					logger.info("delete check new:" + newmethod.toString());
					newmethod.setFlag("new");
					fengdaiMethodInfoDao.addMethodInfo(newmethod);
					logger.info("delete check old:" + old.toString());
					// }
					if (old.getClassname().equals(newmethod.getClassname())&& old.getMethodname().equals(newmethod.getMethodname())) {
						if (null != old.getMethodparams()&& old.getMethodparams().equals(newmethod.getMethodparams())) {
							flag = true;
							break;
						} else if (null == old.getMethodparams() && null == newmethod.getMethodparams()) {
							flag = true;
							break;
						} else {
							flag = false;
						}
					} else {
						flag = false;
					}

			}
			if (!flag)
				deletedMethods.add(old);
		}*/
		for (MethodInfo var : deletedMethods) {
			var.setFlag("deleted");
			// System.out.println("deleted is:"+var);
			fengdaiMethodInfoDao.addMethodInfo(var);
		}
		System.out.println("end:" + new java.util.Date());
		logger.info("badMethodinfolisthodinfolist:{}",badMethodinfolisthodinfolist);
		System.out.println(badMethodinfolisthodinfolist);

	}

	/**
	 * 获取两个List的不同元素
	 *
	 * @param list1
	 * @param list2
	 * @return
	 */
	private static List<String> getDiffrent4(List<String> list1, List<String> list2) {
		long st = System.nanoTime();
		Map<String, Integer> map = new HashMap<String, Integer>(list1.size() + list2.size());
		List<String> diff = new ArrayList<String>();
		List<String> maxList = list1;
		List<String> minList = list2;
		if (list2.size() > list1.size()) {
			maxList = list2;
			minList = list1;
		}
		for (String string : maxList) {
			map.put(string, 1);
		}
		for (String string : minList) {
			Integer cc = map.get(string);
			if (cc != null) {
				map.put(string, ++cc);
				continue;
			}
			map.put(string, 1);
		}
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			if (entry.getValue() == 1) {
				diff.add(entry.getKey());
			}
		}
		System.out.println("getDiffrent4 total times " + (System.nanoTime() - st));
		return diff;

	}

	public static void main(String[] args) {

		// new DemoVisitorTest().getMethodinfo(
		// "D:\\51\\fd-server\\services\\service-dock\\sdk\\src\\main\\java\\com\\tairanchina\\fd\\dock\\dto\\thirdparty\\FiveOneGjjOpertorDto.java");

		/*
		 * for (String var : scanFiles(
		 * "D:\\51\\fd-server\\services\\service-dock\\service\\src\\main\\java\\com\\tairanchina\\fd\\dock\\service\\impl"
		 * )) { System.out.println("com" + var.replace("\\", ".").split("com")[1]); }
		 */
		ArrayList<String> old = new ArrayList<>();
		ArrayList<String> b = new ArrayList<>();
		old.add("1");
		old.add("2");
		old.add("5");
		b.add("5");
		b.add("5");
		b.add("6");

		List<String> diffs = getDiffrent4(old, b);
		System.out.println(diffs);

		for (String var : diffs) {
			if (!old.contains(var))
				System.out.println("add str is :" + var);
		}
		System.out.println(old.removeAll(b));
		System.out.println("delete str is：" + old);
		System.out.println(getDiffrent4(b, diffs));

	}

}