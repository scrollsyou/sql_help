package sql_help;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.gugusong.sqlmapper.db.mysql.MysqlSqlHelp;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

public class TestMysqlSqlHelp {
	
	static ClassPool pool = ClassPool.getDefault();
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, CannotCompileException {
		try {
			 String aaStr = ""; 
			 MysqlSqlHelp aa = null; 
			 CtClass ctClass = pool.getCtClass("com.gugusong.sqlmapper.db.mysql.MysqlSqlHelp"); 
			 CtMethod aaaMethod = ctClass.getDeclaredMethod("aaa"); 
			 System.out.println("aaa" + TestMysqlSqlHelp.class.getClassLoader().toString());
			 aaaMethod.insertBefore("{System.out.println(\"1234565\");}"); 
			 aa = (MysqlSqlHelp) ctClass.toClass().newInstance(); 
			 aa.aaa();
			 System.out.println(" ===================== ");
			 aa = new MysqlSqlHelp();
			 aa.aaa();
			 
			 
			
//			CtClass strCtClass = pool.getCtClass("java.lang.String");
//			CtMethod toStringMethod = strCtClass.getDeclaredMethod("toString");
//			CtField f = new CtField(CtClass.intType, "hiddenValue", strCtClass);
//			f.setModifiers(Modifier.PUBLIC);
//			toStringMethod.insertBefore("{System.out.println(\"测试toString()方法!\");}");
//			String strSASA = (String) strCtClass.toClass().newInstance();
//			strSASA.toString();
//			strSASA = strSASA + "283	293";
//			strSASA.toString();
//			String test = new String("123");
//			test.toString();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	private void mai() throws IOException {
//		URL url = new URL("http://www.baidu.com");
//		HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
//        httpUrlConn.setDoOutput(true);
//        httpUrlConn.setDoInput(true);
//        httpUrlConn.setUseCaches(false);
//        httpUrlConn.setRequestMethod("GET");
//        httpUrlConn.connect();
//	}

}
