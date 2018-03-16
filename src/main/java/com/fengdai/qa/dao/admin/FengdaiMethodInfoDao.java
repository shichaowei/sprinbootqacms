package com.fengdai.qa.dao.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.fengdai.qa.annotation.DS;
import com.fengdai.qa.constants.DataSourceConsts;
import com.fengdai.qa.meta.MethodInfo;
@DS(value=DataSourceConsts.DEFAULT)
@Mapper
public interface FengdaiMethodInfoDao {

	@Insert("INSERT INTO FDmethodInfo ( classname, methodname, methodparams, methodbody,methodpath, addtime,flag) VALUES (#{classname},#{methodname},#{methodparams},#{methodbody},#{methodpath},now(),#{flag})")
	public int addMethodInfo(MethodInfo methodInfo);

	@Update("update FDmethodInfo set flag=#{flag} where id=#{id}")
	public int updateMehodFlag(MethodInfo methodInfo);

	@Delete("delete from FDmethodInfo")
	public int deleteAllMethodInfo();

	@Delete("delete from FDmethodInfo where flag is not null")
	public int deleteHandledMethodInfo();

//	@Select("select * from FDmethodInfo where classname=#{classname} and methodname=#{methodname} and methodparams=#{methodparams}")
	@Select({"<script>",
	    "select id,classname,methodname,methodparams,methodpath,methodbody from FDmethodInfo where classname=#{classname} and methodname=#{methodname} and methodpath=#{methodpath}",
	    "<when test='methodparams!=null'>",
	    "AND methodparams = #{methodparams}",
	    "</when>",
	    "<when test='methodparams ==null'>",
	    "AND methodparams is null",
	    "</when>",
	    "</script>"})
	public MethodInfo getMethodInfo(MethodInfo methodInfo);

	@Select({"<script>",
		"select id,classname,methodname,methodparams,methodpath,methodbody from FDmethodInfo where classname=#{classname} and methodname=#{methodname} and methodpath=#{methodpath}",
		"<when test='methodparams!=null'>",
		"AND methodparams = #{methodparams}",
		"</when>",
		"<when test='methodparams ==null'>",
		"AND methodparams is null",
		"</when>",
	"</script>"})
	public ArrayList<MethodInfo> getMethodInfos(MethodInfo methodInfo);

	@Select("select classname,methodname,methodparams,methodpath,methodbody from FDmethodInfo where flag is null")
	public List<MethodInfo> getAllNormalMethodInfo();


}
