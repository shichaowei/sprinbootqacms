package com.fengdai.qa.utils.bizmethod;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.fengdai.qa.meta.MethodInfo;

public class DemoVisitor extends ASTVisitor {


	private List<MethodInfo> methodInfos;
	private String javapath;
    public DemoVisitor(List<MethodInfo> methodinfos,String javapath) {
    	this.methodInfos=methodinfos;
    	this.javapath=javapath;
    }




	@Override
    public boolean visit(FieldDeclaration node) {
        for (Object obj: node.fragments()) {
            VariableDeclarationFragment v = (VariableDeclarationFragment)obj;
//            System.out.println("Field:\t" + v.getName());
        }

        return true;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
    	MethodInfo methodInfo = new MethodInfo();
//        System.out.println(ToStringBuilder.reflectionToString(node));
//        System.out.println("Method parameter:\t" +node.parameters());
//        System.out.println("MethodParent:\t" +node.getParent().toString());
        if(node.getParent().toString().trim().startsWith("{")) {
//        	System.out.println("Method:\t" + node.getName());
//        	System.out.println("InnerMethod:\t" + node.getName());
//        	System.out.println("Methodbody:\t" +node.getBody());

        }else {
//        	System.out.println("Method:\t" + node.getName());
//        	System.out.println("ClassMethod:\t" + node.getName());
//        	System.out.println("Methodbody:\t" +node.getBody());
        	if(node.getBody() !=null) {
	        	methodInfo.setMethodbody(node.getBody().toString().trim());
	        	methodInfo.setMethodname(node.getName().toString().trim());
	        	if(!node.parameters().isEmpty())
	        		methodInfo.setMethodparams(node.parameters().toString().trim());
	        	methodInfo.setClassname("com" + javapath.replace(".java", "").replace("\\", ".").split("com")[1]);
				methodInfo.setMethodpath(javapath);
	        	methodInfos.add(methodInfo);
        	}
		}
        return true;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
//        System.out.println("Class:\t" + node.getName());
//        System.out.println(node.isInterface());
//    	methodInfo.setClassname(node.getName().toString());
    	if(node.isInterface()) {
    		return false;
    	}
        return true;
    }
}