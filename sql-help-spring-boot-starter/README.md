## sql-help-spring-boot-starter集成说明

### maven依赖：
```xml
<dependency>
    <groupId>io.github.scrollsyou</groupId>
    <artifactId>sql-help-spring-boot-starter</artifactId>
    <version>0.0.1</version>
</dependency>
```
### spring项目中开始使用
1. 在spring启动配置中增加@EnableSqlHelp开启框架功能  
如：
```
/**
 * 启动程序
 * 
 * @author qinghong
 */
@SpringBootApplication
@EnableSqlHelp
public class QinghongApplication
{
    public static void main(String[] args)
    {
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(QinghongApplication.class, args);
        System.out.println("##########  启动完成   ##########");
    }
    .
    .
    .
```

2. 增加PO与数据库映射类  
* 声明在PO类上的注解：@Entity(tableName = "数据库表名，若驼峰命名后与类名一致此属性可不设置此属性")  
* 声明在PO类属性上的注解：  
    - @Id(name="主键ID列名，若驼峰命名后与PO类属性名一致此属性可不设置此属性", strategy = 主键策略，使用enum类GenerationType)  
    - @Column(name = "数据库表的列名，若驼峰命名后与PO类属性名一致此属性可不设置此属性", comments = "注释", dateType = "在数据库中的类型")  
    - @Version(strategy = 版本策略，使用enum类VersionGenerationType)：标记为版本号字段  
    - @Transient：标记为非数据库字段  

  如：
```java
@Entity
public class SysUser
{
	@Transient
    private static final long serialVersionUID = 1L;

    /** 用户ID */
	@Id
    private Long id;

    /** 用户账号 */
    private String userName;

    /** 用户昵称 */
    private String nickName;
.
.
.

@Entity
public class SysUserDept
{
	@Transient
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long userId;

    /** $column.columnComment */
    private Long deptId;
.
.
.

@Entity
public class SysDept
{
	@Transient
    private static final long serialVersionUID = 1L;

    /** 部门ID */
	@Id
    private Long id;

    /** 父部门ID */
    private Long parentId;

    /** 祖级列表 */
    private String ancestors;
.
.
.
```
3. 增加VO与PO映射类进行查询操作  
* 声明在VO类上的注解：  
    - @VOBean(mainPo = 主表的PO类.class, entityAlias = "此表在sql中的别名")：明确注解VO类，mainPo为主PO关联类，sql以此表为主表，主PO类必须存在id主键，属性名默认一一映射
    - @Join(joinType = Join.LEFT_JOIN_TYPE或者Join.INNER_JOIN_TYPE, po = 关联表的PO类.class, entityAlias = "此表在sql中的别名", joinConditions = "此表PO类与主表PO类中的对应关系，例如：{b.id}= {bd.bookId}")：明确两个PO类对象的关联属性  
* 声明在VO类属性上的注解：
    - @PropertyMapping(originalName = "表别名.列名") 明确数据库表的字段与VO类属性的对应关系，非主表字段不可省略


  如：
```java
@VOBean(mainPo = SysUser.class, entityAlias = "u")
@Join(po = SysUserDept.class, joinType = Join.LEFT_JOIN_TYPE, entityAlias = "a", joinConditions = "{a.userId}= {u.id}")
@Join(po = SysDept.class, entityAlias = "d", joinConditions = "{a.deptId} = {d.id}")
public class SelectUserListVo {

	private Long id;
	private String nickName;
	private String userName;
	private String email;
	private String avatar;
	private String phonenumber;
	private String password;
	private String sex;
	private String status;
	private String delFlag;
	private String loginIp;
	private Date loginDate;
	@OneToMany(tagerClass = Dept.class)
	private List<Dept> depts;
	
	public static class Dept{
		@PropertyMapping(originalName = "d.deptName")
		private String deptName;
		@PropertyMapping(originalName = "d.leader")
		private String leader;
		public String getDeptName() {
			return deptName;
		}
		public void setDeptName(String deptName) {
			this.deptName = deptName;
		}
		public String getLeader() {
			return leader;
		}
		public void setLeader(String leader) {
			this.leader = leader;
		}
		
	}
省略get/set方法
```
4. 通过注入SqlHelpBaseDao类进行数据库操作,通过dao进行bean操作  
使用Example：
* 实例化一个Example对象Example example = ExampleImpl.newInstance()
* 设置筛选条件可使用：
    + example.condition()：编写条件使用表的列表，需要使用列名，不能使用类属性名，如：example.condition("1=1 and a.user_name like '%test%'");  
    + 按照where的筛选逻辑使用，example.and()、or()，之后的条件可以使用如下方法【注：条件指定的字段建议统一写法为VO类定义的别名.类属性名，其中若表字段名在sql中不重复，VO类定义的别名可省略】：  
        + equals("VO类定义的别名.类属性名", "值")
        + like("VO类定义的别名.类属性名", "值")：若要模糊查询，值需要包含% 
        + contains("VO类定义的别名.类属性名", "值")：指定字段是否包含某值，等价于%值%
        + 例子：example.and().equals("a.userName", "test123").and().contains("a.userName", "test").and().like("a.userName", "te%");
* 设置排序规则可使用：orderByAsc("VO类定义的别名.类属性名")、orderByDesc("VO类定义的别名.类属性名")
* 设置分页：page(new Page(pageIndex, pageSize))；pageIndex第几页，从1开始，默认为1；pageSize每页返回数据量

例子：
```java
@Resource
private SqlHelpBaseDao dao;

// 保存用户
@Transactional
public void save(SysUser user, SysUserDept userDept) {
	user = dao.save(user);
	userDept.setUserId(user.getId());
	userDept = dao.save(userDept);
}

// 查询用户列表
public void test(){
	Example example = ExampleImpl.newInstance();
	example.condition("1=1 and a.user_name like '%test%'");
	example.and().contains("a.userName", "test").and().like("a.nickName", "test%");
	example.orderByAsc("a.version").orderByDesc("a.id").page(new Page(1, 10));
	log.info(JSON.toJSONString(dao.findAll(example, SelectUserListVo.class)));
}
```
打印结果为：
```json
[
    {
        "avatar":"/profile/avatar/2020/06/09/2023bfcabc4c40a19f7d78e1e1a633f3.jpeg",
        "delFlag":"0",
        "depts":[
            {
                "deptName":"XX科技",
                "leader":"XX科技"
            }
        ],
        "email":"ry@163.com",
        "id":1,
        "loginDate":1521171180000,
        "loginIp":"127.0.0.1",
        "nickName":"XX科技",
        "password":"$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2",
        "phonenumber":"15888888888",
        "sex":"0",
        "status":"0",
        "userName":"admin"
    },
    {
        "avatar":"",
        "delFlag":"2",
        "depts":[
            {
                "deptName":"XX科技",
                "leader":"XX科技"
            }
        ],
        "email":"22@qq.com",
        "id":9,
        "loginIp":"",
        "nickName":"test",
        "password":"$2a$10$WvdWoE3z9egzAtR2vhSSm.ablDp.I2hU5PzYyXxwWIsBvbR1wfw4O",
        "phonenumber":"18188566545",
        "sex":"0",
        "status":"0",
        "userName":"test"
    },
    {
        "avatar":"",
        "delFlag":"0",
        "depts":[
            {
                "deptName":"XX科技",
                "leader":"XX科技"
            },
            {
                "deptName":"java研发部"
            }
        ],
        "email":"dss@qq.com",
        "id":10,
        "loginIp":"",
        "nickName":"test1",
        "password":"$2a$10$/HqNCw155Wi2l325lnzqg.n6T01D53PAibLPobkL/O33yirtG5zEu",
        "phonenumber":"18188600100",
        "sex":"0",
        "status":"0",
        "userName":"test1"
    }
]
```
