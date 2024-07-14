package database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;


public class MySQLGeneratorEntityUtil {
    //表名
    private String tableName;
    //列名数组
    private String[] colNames;
    //列名对应的java字段名
    private String[] filedNames;
    //列名类型数组
    private String[] colTypes;
    //列名大小数组
    private int[] colSizes;
    //列名注释
    private Map colNamesComment = new HashMap();
    //是否需要导入包java.util.*
    private boolean needUtil = false;
    //是否需要导入包java.sql.*
    private boolean needSql = false;
    //是否需要导入包java.math.BigDecimal
    private boolean needBigDecimal = false;
    //是否导入包 lombok.*
    private boolean needLombok  = true;
    //是否创建EntityHelper
    private boolean needEntityHelper = false;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String SQL = "SELECT * FROM ";// 数据库操作

    // 数据库配置信息
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/test?"
                                            +"nullCatalogMeansCurrent=true";
    public static final String schemaName = "training";
    private static final String NAME = "root";
    private static final String PASS = "123456";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    //指定实体生成所在包的路径
    private static String basePath = new File("").getAbsolutePath();
    //指定包名
    private static String packageOutPath = "com.dcits.project.pojo";
    //作者名字
    private String authorName = "王鹏飞";
    //指定需要生成的表的表名，全部生成设置为null
    private String[] generateTables = null;
    //主键
    private static String pk;

    private MySQLGeneratorEntityUtil() {
    }

    /**
    * @Description:数据库中表名与 类名的映射  经常改
    * @param: [tableName]
    * @return:
    *       java.lang.String
    *
    */
    private String tableNameMappingClassName(){
        //单纯下划线转驼峰
        return under2camel(tableName, true);
    }

    /**
    * @Description:字段映射的规则
    * @param: [filedName]
    * @return:
    *       java.lang.String
    *
    */
    private String propertyMappingFiled(String filedName){
        System.out.println("filedName = " + filedName);
        return under2camel(filedName.replace(tableName,""),
                false);
    }

    // 类文件属于哪个包下，且类需要引入哪些类型的库
    private String generatePackageInformation(){
        StringBuilder sb = new StringBuilder();
        sb.append("package " + packageOutPath + ";\r\n");
        sb.append("\r\n");
        // 判断是否导入工具包
        if (needUtil) {
            sb.append("import java.util.Date;\r\n");
        }
        if (needSql) {
            sb.append("import java.sql.*;\r\n");
        }
        if(needLombok){
            sb.append("import lombok.*;\r\n");
        }

        //根据 数据库的表属性类型的 java 映射关系，判断是否要导入额外包
        for (int i = 0; i < filedNames.length; i++) {
            String hasbd = sqlTypeMappingJavaType(colTypes[i]);
            if(hasbd =="BigDecimal" || "BigDecimal".equals(hasbd)) {needBigDecimal=true;}
        }
        if(needBigDecimal) {
            sb.append("import java.math.BigDecimal;\r\n");
        }
        return sb.toString();
    }

    //生成注释
    private String generateComment(){
        StringBuilder sb = new StringBuilder();
        sb.append("/**\r\n");
        sb.append(" * table name:  " + tableName + "\r\n");
        sb.append(" * author name: " + authorName + "\r\n");
        sb.append(" * create time: " + SDF.format(new Date()) + "\r\n");
        sb.append(" */ \r\n");
        return sb.toString();
    }

    /**
    * @Description:生成注解 @
    * @param: [sb]
    * @return:
    *       void
    *
    */
    private void generateAnnotations(StringBuffer sb){
        if(needLombok){
            sb.append("@Getter\r\n" +
                    "@Setter\r\n" +
                    "@NoArgsConstructor\r\n" +
                    "@AllArgsConstructor\r\n");
        }

    }

    private String generateClassDefinition(){
        StringBuilder sb = new StringBuilder();
        String classExtends = "";
        if(needEntityHelper) {
            classExtends=" extends EntityHelper";
        }
        sb.append("public class ");

        sb.append(tableNameMappingClassName());
        sb.append(classExtends);

        sb.append("{\r\n\r\n");
        return sb.toString();
    }



    /**
     * @description 生成class的所有内容
     */
    private String parse() {
        StringBuffer sb = new StringBuffer();

        sb.append(generatePackageInformation());
        // 注释部分
        sb.append(generateComment());
        //注解类部分
        generateAnnotations(sb);
        // 类定义
        sb.append(generateClassDefinition());
        // 实体部分
        processAllAttrs(sb);// 属性
        sb.append("\r\n");
        if(!needLombok){
            processConstructor(sb);//构造函数
            processAllMethod(sb);// get set方法
        }
        processToString(sb);

        if(needEntityHelper) {
            processEntityHelper(sb,pk);
        }

        sb.append("}\r\n");
        return sb.toString();
    }

    /**
     * @description 生成所有成员变量及注释
     * @param sb
     * @author paul
     * @version V1.0
     */
    private void processAllAttrs(StringBuffer sb) {
        for (int i = 0; i < filedNames.length; i++) {
            if(colNamesComment.get(colNames[i])!=null &&!"".equals(colNamesComment.get(colNames[i]))) {
                sb.append("\t/*"+colNamesComment.get(colNames[i])+"*/\r\n");
            }
            sb.append("\tprivate " + sqlTypeMappingJavaType(colTypes[i]) + " " + filedNames[i] + ";\r\n");
        }
    }

    /**
     * EntityHelper
     * @param sb
     * @param pk
     */
    private void processEntityHelper(StringBuffer sb, String pk) {
        sb.append("\t@Override\r\n");
        sb.append("\tpublic String getPrimaryKey() {\r\n");
        sb.append("\t\treturn \""+pk+"\";\r\n");
        sb.append("\t}\r\n");
    }

    /**
    * @Description:重写toString 方法，该方法使用JSON格式输出
    * @param: [sb]
    * @return:
    *       void
    *
    */
    private void processToString(StringBuffer sb) {
        sb.append("\t@Override\r\n\tpublic String toString() {\r\n");
        sb.append("\t\t"
                +"return new StringBuilder(\"{\")"
                + "\r\n");
        String comma = "";
        for (int i = 0; i < filedNames.length; i++) {
            sb.append("\t\t\t\t.append(");

            sb.append("\""
                    + comma
                    +"\\\""+ filedNames[i]+"\\\":\\\""
                    +"\")\r\n")
                .append("\t\t\t\t.append("
                        + filedNames[i]
                        +")")
                .append(".append("
                        + "'\\\"'"
                        +")\r\n");
            if(i == 0)
                comma = ",";
        }
        sb.append("\t\t\t\t.append(\"}\")\r\n");
        sb.append("\t\t\t\t.toString();\r\n");
        sb.append("\t}\r\n");
    }

    /**
     * 构造函数
     * @param sb
     */
    private void processConstructor(StringBuffer sb) {
        StringBuffer p = new StringBuffer();
        StringBuffer v = new StringBuffer();
        for(int i = 0; i < filedNames.length; i++) {
            p.append(sqlTypeMappingJavaType(colTypes[i])+" "+ filedNames[i]);
            if(i!= filedNames.length-1) {
                p.append(",");
            }
            v.append("\t\tthis."+ filedNames[i]+"="+ filedNames[i]+";\r\n");
        }
        //无参数构造函数
        sb.append("\tpublic "+tableNameMappingClassName()+"() {\r\n");
        sb.append("\t\tsuper();\r\n");
        sb.append("\t}\r\n");
        //带参构造函数
        sb.append("\tpublic "+tableNameMappingClassName()+"("+p.toString()+") {\r\n");
        sb.append(v.toString());
        sb.append("\t}\r\n");
    }

    /**
     * @param sb
     * @description 生成所有get/set方法
     */
    private void processAllMethod(StringBuffer sb) {
        for (int i = 0; i < filedNames.length; i++) {
            sb.append("\tpublic void set" + initCap(filedNames[i]) + "(" + sqlTypeMappingJavaType(colTypes[i]) + " "
                    + filedNames[i] + "){\r\n");
            sb.append("\t\tthis." + filedNames[i] + "=" + filedNames[i] + ";\r\n");
            sb.append("\t}\r\n");
            sb.append("\tpublic " + sqlTypeMappingJavaType(colTypes[i]) + " get" + initCap(filedNames[i]) + "(){\r\n");
            sb.append("\t\treturn " + filedNames[i] + ";\r\n");
            sb.append("\t}\r\n");
        }
    }

    /**
     * @param str 传入字符串
     * @return
     * @description 将传入字符串的首字母转成大写
     */
    private String initCap(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z')
            ch[0] = (char) (ch[0] - 32);
        return new String(ch);
    }

    /**
     * 功能：下划线命名转驼峰命名
     * @param s
     * @param fistCharToUpperCase 首字母是否大写
     * @author 呐喊
     * @return
     */
    private String under2camel(String s, boolean fistCharToUpperCase) {

        String separator = "_";
        StringBuffer under= new StringBuffer();
        s = s.toLowerCase().replace(separator, " ");
        String sarr[]=s.split(" ");
        System.out.println("s = " + s);
        for(int i=0;i<sarr.length;i++)
        {
            if(sarr[i].trim().length()>0){
                String w=sarr[i].substring(0,1).toUpperCase()+sarr[i].substring(1);
                under.append(w);
            }
        }
        System.out.println("under = " + under);
        return !fistCharToUpperCase?
                under.toString().substring(0,1).toLowerCase()+under.substring(1)
                : under.toString();
    }

    /**
     * @return
     * @description 查找sql字段类型所对应的Java类型
     */
    private String sqlTypeMappingJavaType(String sqlType) {
        //二进制位
        if (sqlType.equalsIgnoreCase("bit")
                ||sqlType.equalsIgnoreCase("boolean")) {
            return "Boolean";
        }else if (sqlType.equalsIgnoreCase("blob")) {
            return "byte[]";
        }
        // 开始数值类型
        else if (sqlType.equalsIgnoreCase("tinyint")
                ||sqlType.equalsIgnoreCase("smallint")
                ||sqlType.equalsIgnoreCase("int")) {
            return "Integer";
        }else if (sqlType.equalsIgnoreCase("bigint")) {
            return "BigInteger";
        } else if (sqlType.equalsIgnoreCase("integer")) {
            return "Long";
        } else if (sqlType.equalsIgnoreCase("float")) {
            return "Float";
        } else if (sqlType.equalsIgnoreCase("DOUBLE")) {
            return "Double";
        } else if (sqlType.equalsIgnoreCase("decimal")) {
            return "BigDecimal";
        }
        //开始字符串类型
        else if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
                || sqlType.equalsIgnoreCase("text")|| sqlType.equalsIgnoreCase("longtext")) {
            return "String";
        }
        //开始 时间类型
        else if(sqlType.equalsIgnoreCase("date")
                ||sqlType.equalsIgnoreCase("datetime")){
            return "Date";
        }else if(sqlType.equalsIgnoreCase("time")){
            return "Time";
        }else if(sqlType.equalsIgnoreCase("timestamp")){
            return "Timestamp";
        }
        return null;
    }

    /**
     * 功能：获取并创建实体所在的路径目录
     * @return
     */
    private static String pkgDirName() {
        String dirName = basePath + "/src/main/java/" + packageOutPath.replace(".", "/");
        File dir = new File(dirName);
        if (!dir.exists()) {dir.mkdirs();
            System.out.println("mkdirs dir 【" + dirName + "】");}
        return dirName;
    }

    /**
     * 生成EntityHelper
     */
    private void EntityHelper() {
        String dirName = MySQLGeneratorEntityUtil.pkgDirName();
        String javaPath = dirName + "/EntityHelper.java";
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("package " + packageOutPath + ";\r\n");
            sb.append("\r\n");
            sb.append("public abstract class EntityHelper{\r\n\r\n");
            sb.append("\tpublic abstract String getPrimaryKey();\r\n");
            sb.append("\r\n");
            sb.append("}\r\n");
            FileWriter fw = new FileWriter(javaPath);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(sb.toString());
            pw.flush();
            if (pw != null){pw.close();}
            System.out.println("create class 【EntityHelper】");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 用于清楚上一张表生成的个性化信息，例如包依赖
     * @how:
     *      目前只是机械代码枚举依赖，是否改进看未来需要。
     * @return:
     *       void
    **/
    private void clearLastFormDependencies(){
        needSql = false;
        needBigDecimal = false;
        needUtil = false;
    }


    /**
     * @description 生成方法
     */
    private void generate() throws Exception {
        //与数据库的连接
        Connection con;
        PreparedStatement pStemt = null;
        Class.forName(DRIVER);
        con = DriverManager.getConnection(URL, NAME, PASS);
        System.out.println("connect database success..."+con);
        //获取数据库的元数据
        DatabaseMetaData db = con.getMetaData();
        //是否有指定生成表，有指定则直接用指定表，没有则全表生成
        List<String> tableNames = new ArrayList<>();
        if (generateTables == null) {
            //从元数据中获取到所有的表名
            System.out.println(db);
            ResultSet rs = db.getTables(null, schemaName, null, new String[] { "TABLE" });
            while (rs.next()) tableNames.add(rs.getString(3));
        } else {
            for (String tableName : generateTables) tableNames.add(tableName);
        }
        if(needEntityHelper) {
            EntityHelper();
        }
        String tableSql;
        PrintWriter pw = null;
        // 以 select * from table name 来获取 表内 列名
        for (int j = 0; j < tableNames.size(); j++) {
            clearLastFormDependencies();
            tableName = tableNames.get(j);
            tableSql = SQL + tableName;
            pStemt = con.prepareStatement(tableSql);

            //通过元数据 获得 主键值
            ResultSet rsk = con.getMetaData().getPrimaryKeys(con.getCatalog().toLowerCase(), null, tableName);
            if (rsk.next()) {
                String primaryKey = rsk.getString("COLUMN_NAME");
                pk=primaryKey;
            }
            //以查询结果的元数据 获得 表 信息
            ResultSetMetaData rsmd = pStemt.getMetaData();
            int size = rsmd.getColumnCount();
            colNames = new String[size];
            filedNames = new String[size];
            colTypes = new String[size];
            colSizes = new int[size];

            //获取所需的信息
            for (int i = 0; i < size; i++) {
                //这里需要映射一下结果值
                colNames[i] = rsmd.getColumnName(i + 1);
                filedNames[i] = propertyMappingFiled(rsmd.getColumnName(i + 1));

                colTypes[i] = rsmd.getColumnTypeName(i + 1);

                //这里应该 正则判断  从类型判断要引用哪些包
                if (colTypes[i].equalsIgnoreCase("datetime"))
                    needUtil = true;
                if (colTypes[i].toLowerCase().matches("(image|text|timestamp)"))
                    needSql = true;

                colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
            }
            //获取字段注释  show full columns from table
            ResultSet rsComment = pStemt.executeQuery("show full columns from " + tableName);
            while (rsComment.next()) {
                colNamesComment.put(rsComment.getString("Field"), rsComment.getString("Comment"));
            }
            //解析生成实体java文件的所有内容
            String content = parse();
            //输出生成文件
            String dirName = MySQLGeneratorEntityUtil.pkgDirName();

            String javaPath = dirName + "/" + tableNameMappingClassName() + ".java";
            FileWriter fw = new FileWriter(javaPath);
            pw = new PrintWriter(fw);
            pw.println(content);
            pw.flush();
            System.out.println("create class 【" + tableNameMappingClassName() + "】");
        }
        if (pw != null)
            pw.close();
    }

    public static void main(String[] args) {
        MySQLGeneratorEntityUtil instance = new MySQLGeneratorEntityUtil();
        //instance.basePath=""; //指定生成的位置,默认是当前工程
        try {
            instance.generate();
            System.out.println("generate Entity to classes successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
